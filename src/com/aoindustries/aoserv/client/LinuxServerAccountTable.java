package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.io.unix.*;
import com.aoindustries.profiler.*;
import com.aoindustries.security.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  LinuxServerAccount
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxServerAccountTable extends CachedTableIntegerKey<LinuxServerAccount> {

    LinuxServerAccountTable(AOServConnector connector) {
	super(connector, LinuxServerAccount.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(LinuxServerAccount.COLUMN_USERNAME_name, ASCENDING),
        new OrderBy(LinuxServerAccount.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addLinuxServerAccount(LinuxAccount linuxAccount, AOServer aoServer, String home) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccountTable.class, "addLinuxServerAccount(LinuxAccount,AOServer,String)", null);
        try {
            int pkey=connector.requestIntQueryIL(
                AOServProtocol.CommandID.ADD,
                SchemaTable.TableID.LINUX_SERVER_ACCOUNTS,
                linuxAccount.pkey,
                aoServer.pkey,
                home
            );
            return pkey;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void clearCache() {
        super.clearCache();
        synchronized(uidHash) {
            uidHashBuilt=false;
        }
        synchronized(nameHash) {
            nameHashBuilt=false;
        }
    }

    public LinuxServerAccount get(Object pkey) {
	return getUniqueRow(LinuxServerAccount.COLUMN_PKEY, pkey);
    }

    public LinuxServerAccount get(int pkey) {
	return getUniqueRow(LinuxServerAccount.COLUMN_PKEY, pkey);
    }

    List<LinuxServerAccount> getAlternateLinuxServerAccounts(LinuxServerGroup group) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccountTable.class, "getAlternateLinuxServerAccounts(LinuxServerGroup)", null);
        try {
            int aoServer=group.getAOServer().pkey;
            String groupName = group.getLinuxGroup().pkey;

            List<LinuxServerAccount> cached = getRows();
            int cachedLen = cached.size();
            List<LinuxServerAccount> matches=new ArrayList<LinuxServerAccount>(cachedLen);
            for (int c = 0; c < cachedLen; c++) {
                LinuxServerAccount linuxServerAccount = cached.get(c);
                if(linuxServerAccount.ao_server==aoServer) {
                    String username = linuxServerAccount.username;

                    // Must also have a non-primary entry in the LinuxGroupAccounts that is also a group on this server
                    LinuxGroupAccount linuxGroupAccount = connector.linuxGroupAccounts.getLinuxGroupAccount(groupName, username);
                    if (linuxGroupAccount != null && !linuxGroupAccount.is_primary) matches.add(linuxServerAccount);
                }
            }
            return matches;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    private boolean nameHashBuilt=false;
    private final Map<Integer,Map<String,LinuxServerAccount>> nameHash=new HashMap<Integer,Map<String,LinuxServerAccount>>();

    LinuxServerAccount getLinuxServerAccount(AOServer aoServer, String username) {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccountTable.class, "getLinuxServerAccount(AOServer,String)", null);
        try {
            synchronized(nameHash) {
                if(!nameHashBuilt) {
                    nameHash.clear();

                    List<LinuxServerAccount> list=getRows();
                    int len=list.size();
                    for(int c=0; c<len; c++) {
                        LinuxServerAccount lsa=list.get(c);
                        Integer I=Integer.valueOf(lsa.getAOServer().pkey);
                        Map<String,LinuxServerAccount> serverHash=nameHash.get(I);
                        if(serverHash==null) nameHash.put(I, serverHash=new HashMap<String,LinuxServerAccount>());
                        if(serverHash.put(lsa.username, lsa)!=null) throw new WrappedException(new SQLException("LinuxServerAccount username exists more than once on server: "+lsa.username+" on "+I.intValue()));
                    }
		    nameHashBuilt=true;
                }
                Map<String,LinuxServerAccount> serverHash=nameHash.get(Integer.valueOf(aoServer.pkey));
                if(serverHash==null) return null;
                return serverHash.get(username);
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Finds a LinuxServerAccount by a username and password combination.  It tries to return an account that is not disabled and
     * matches both username and password.
     *
     * @return   the <code>LinuxServerAccount</code> found if a non-disabled username and password are found, or <code>null</code> if no match found
     *
     * @exception  LoginException  if a possible account match is found but the account is disabled or has a different password
     */
    public LinuxServerAccount getLinuxServerAccountFromUsernamePassword(String username, String password, boolean emailOnly) throws LoginException {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccountTable.class, "getLinuxServerAccountFromUsernamePassword(String,String,boolean)", null);
        try {
            List<LinuxServerAccount> list = getRows();
            LinuxServerAccount badPasswordLSA=null;
            LinuxServerAccount disabledLSA=null;
            int len = list.size();
            for (int c = 0; c < len; c++) {
                LinuxServerAccount account=list.get(c);
                if(
                    account.username.equals(username)
                    && (!emailOnly || account.getLinuxAccount().getType().isEmail())
                ) {
                    if(account.disable_log!=-1) {
                        if(disabledLSA==null) disabledLSA=account;
                    } else {
                        if(account.passwordMatches(password)) return account;
                        else {
                            if(badPasswordLSA==null) badPasswordLSA=account;
                        }
                    }
                }
            }
            if(badPasswordLSA!=null) throw new BadPasswordException("The password does not match the password for the \""+badPasswordLSA.getLinuxAccount().getUsername().getUsername()+"\" account on the \""+badPasswordLSA.getAOServer().getHostname()+"\" server.");
            if(disabledLSA!=null) {
                DisableLog dl=disabledLSA.getDisableLog();
                String reason=dl==null?null:dl.getDisableReason();
                if(reason==null) throw new AccountDisabledException("The \""+disabledLSA.getLinuxAccount().getUsername().getUsername()+"\" account on the \""+disabledLSA.getAOServer().getHostname()+"\" server has been disabled for an unspecified reason.");
                else throw new AccountDisabledException("The \""+disabledLSA.getLinuxAccount().getUsername().getUsername()+"\" account on the \""+disabledLSA.getAOServer().getHostname()+"\" server has been disabled for the following reason: "+reason);
            }
            return null;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Finds a LinuxServerAccount by an email address and password combination.  It tries to return an account that is not disabled and
     * matches both email address and password.
     *
     * @return   the <code>LinuxServerAccount</code> found if a non-disabled email address and password are found, or <code>null</code> if no match found
     *
     * @exception  LoginException  if a possible account match is found but the account is disabled or has a different password
     */
    public LinuxServerAccount getLinuxServerAccountFromEmailAddress(String address, String domain, String password) throws LoginException {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccountTable.class, "getLinuxServerAccountFromEmailAddress(String,String,String)", null);
        try {
            LinuxServerAccount badPasswordLSA=null;
            LinuxServerAccount disabledLSA=null;

            List<EmailDomain> domains=connector.emailDomains.getRows();
            int domainsLen=domains.size();
            for(int c=0;c<domainsLen;c++) {
                EmailDomain ed=domains.get(c);
                if(ed.domain.equals(domain)) {
                    AOServer ao=ed.getAOServer();
                    EmailAddress ea=ed.getEmailAddress(address);
                    if(ea!=null) {
                        List<LinuxServerAccount> lsas=ea.getLinuxServerAccounts();
                        int lsasLen=lsas.size();
                        for(int d=0;d<lsasLen;d++) {
                            LinuxServerAccount lsa=lsas.get(d);
                            if(lsa.disable_log!=-1) {
                                if(disabledLSA==null) disabledLSA=lsa;
                            } else {
                                if(lsa.passwordMatches(password)) return lsa;
                                else {
                                    if(badPasswordLSA==null) badPasswordLSA=lsa;
                                }
                            }
                        }
                    }
                }
            }

            if(badPasswordLSA!=null) throw new BadPasswordException("The \""+address+"@"+domain+"\" address resolves to the \""+badPasswordLSA.getLinuxAccount().getUsername().getUsername()+"\" account on the \""+badPasswordLSA.getAOServer().getHostname()+"\" server, but the password does not match.");
            if(disabledLSA!=null) {
                DisableLog dl=disabledLSA.getDisableLog();
                String reason=dl==null?null:dl.getDisableReason();
                if(reason==null) throw new AccountDisabledException("The \""+address+"@"+domain+"\" address resolves to the \""+disabledLSA.getLinuxAccount().getUsername().getUsername()+"\" account on the \""+disabledLSA.getAOServer().getHostname()+"\" server, but the account has been disabled for an unspecified reason.");
                else throw new AccountDisabledException("The \""+address+"@"+domain+"\" address resolves to the \""+disabledLSA.getLinuxAccount().getUsername().getUsername()+"\" account on the \""+disabledLSA.getAOServer().getHostname()+"\" server, but the account has been disabled for the following reason: "+reason);
            }
            return null;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }
    
    private boolean uidHashBuilt=false;
    private final Map<Integer,Map<Integer,LinuxServerAccount>> uidHash=new HashMap<Integer,Map<Integer,LinuxServerAccount>>();

    LinuxServerAccount getLinuxServerAccount(AOServer aoServer, int uid) {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccountTable.class, "getLinuxServerAccount(AOServer,int)", null);
        try {
            synchronized(uidHash) {
                if(!uidHashBuilt) {
                    uidHash.clear();

                    List<LinuxServerAccount> list = getRows();
                    int len = list.size();
                    for (int c = 0; c < len; c++) {
                        LinuxServerAccount lsa=list.get(c);
                        int lsaUID=lsa.getUID().getID();
                        // Only hash the root user for uid of 0
                        if(lsaUID!=UnixFile.ROOT_UID || lsa.username.equals(LinuxAccount.ROOT)) {
                            Integer aoI=Integer.valueOf(lsa.getAOServer().pkey);
                            Map<Integer,LinuxServerAccount> serverHash=uidHash.get(aoI);
                            if(serverHash==null) uidHash.put(aoI, serverHash=new HashMap<Integer,LinuxServerAccount>());
                            Integer I=Integer.valueOf(lsaUID);
                            if(!serverHash.containsKey(I)) serverHash.put(I, lsa);
                        }
                    }
		    uidHashBuilt=true;
                }
                Map<Integer,LinuxServerAccount> serverHash=uidHash.get(Integer.valueOf(aoServer.pkey));
                if(serverHash==null) return null;
                return serverHash.get(Integer.valueOf(uid));
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    List<LinuxServerAccount> getLinuxServerAccounts(LinuxAccount linuxAccount) {
        return getIndexedRows(LinuxServerAccount.COLUMN_USERNAME, linuxAccount.pkey);
    }

    List<LinuxServerAccount> getLinuxServerAccounts(AOServer aoServer) {
        return getIndexedRows(LinuxServerAccount.COLUMN_AO_SERVER, aoServer.pkey);
    }

    public List<LinuxServerAccount> getMailAccounts() {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccountTable.class, "getMailAccounts()", null);
        try {
            List<LinuxServerAccount> cached = getRows();
            int len = cached.size();
            List<LinuxServerAccount> matches=new ArrayList<LinuxServerAccount>(len);
            for (int c = 0; c < len; c++) {
                LinuxServerAccount lsa=cached.get(c);
                if(lsa.getLinuxAccount().getType().isEmail()) matches.add(lsa);
            }
            return matches;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.LINUX_SERVER_ACCOUNTS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccountTable.class, "handleCommand(String[],InputStream,TerminalWriter,TerminalWriter,boolean)", null);
        try {
            String command=args[0];
            if(command.equalsIgnoreCase(AOSHCommand.ADD_LINUX_SERVER_ACCOUNT)) {
                if(AOSH.checkParamCount(AOSHCommand.ADD_LINUX_SERVER_ACCOUNT, args, 3, err)) {
                    int pkey=connector.simpleAOClient.addLinuxServerAccount(
                        args[1],
                        args[2],
                        args[3]
                    );
                    out.println(pkey);
                    out.flush();
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.COMPARE_LINUX_SERVER_ACCOUNT_PASSWORD)) {
                if(AOSH.checkParamCount(AOSHCommand.COMPARE_LINUX_SERVER_ACCOUNT_PASSWORD, args, 3, err)) {
                    boolean result=connector.simpleAOClient.compareLinuxServerAccountPassword(
                        args[1],
                        args[2],
                        args[3]
                    );
                    out.println(result);
                    out.flush();
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.COPY_HOME_DIRECTORY)) {
                if(AOSH.checkParamCount(AOSHCommand.COPY_HOME_DIRECTORY, args, 3, err)) {
                    long byteCount=connector.simpleAOClient.copyHomeDirectory(
                        args[1],
                        args[2],
                        args[3]
                    );
                    if(isInteractive) {
                        out.print(byteCount);
                        out.println(byteCount==1?" byte":" bytes");
                    } else out.println(byteCount);
                    out.flush();
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.COPY_LINUX_SERVER_ACCOUNT_PASSWORD)) {
                if(AOSH.checkParamCount(AOSHCommand.COPY_LINUX_SERVER_ACCOUNT_PASSWORD, args, 4, err)) {
                    connector.simpleAOClient.copyLinuxServerAccountPassword(
                        args[1],
                        args[2],
                        args[3],
                        args[4]
                    );
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_LINUX_SERVER_ACCOUNT)) {
                if(AOSH.checkParamCount(AOSHCommand.DISABLE_LINUX_SERVER_ACCOUNT, args, 3, err)) {
                    out.println(
                        connector.simpleAOClient.disableLinuxServerAccount(
                            args[1],
                            args[2],
                            args[3]
                        )
                    );
                    out.flush();
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_LINUX_SERVER_ACCOUNT)) {
                if(AOSH.checkParamCount(AOSHCommand.ENABLE_LINUX_SERVER_ACCOUNT, args, 2, err)) {
                    connector.simpleAOClient.enableLinuxServerAccount(args[1], args[2]);
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.GET_CRON_TABLE)) {
                if(AOSH.checkParamCount(AOSHCommand.GET_CRON_TABLE, args, 2, err)) {
                    out.print(
                        connector.simpleAOClient.getCronTable(
                            args[1],
                            args[2]
                        )
                    );
                    out.flush();
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.GET_IMAP_FOLDER_SIZES)) {
                if(AOSH.checkMinParamCount(AOSHCommand.GET_IMAP_FOLDER_SIZES, args, 3, err)) {
                    String[] folderNames=new String[args.length-3];
                    System.arraycopy(args, 3, folderNames, 0, folderNames.length);
                    long[] sizes=connector.simpleAOClient.getImapFolderSizes(args[1], args[2], folderNames);
                    for(int c=0;c<folderNames.length;c++) {
                        out.print(folderNames[c]);
                        out.print('\t');
                        out.print(sizes[c]);
                        out.println();
                    }
                    out.flush();
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.GET_INBOX_ATTRIBUTES)) {
                if(AOSH.checkParamCount(AOSHCommand.GET_INBOX_ATTRIBUTES, args, 2, err)) {
                    InboxAttributes attr=connector.simpleAOClient.getInboxAttributes(args[1], args[2]);
                    out.print("System Time..: ");
                    out.println(SQLUtility.getDateTime(attr.getSystemTime()));
                    out.print("File Size....: ");
                    out.println(attr.getFileSize());
                    out.print("Last Modified: ");
                    out.println(SQLUtility.getDateTime(attr.getLastModified()));
                    out.flush();
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.IS_LINUX_SERVER_ACCOUNT_PASSWORD_SET)) {
                if(AOSH.checkParamCount(AOSHCommand.IS_LINUX_SERVER_ACCOUNT_PASSWORD_SET, args, 2, err)) {
                    out.println(
                        connector.simpleAOClient.isLinuxServerAccountPasswordSet(
                            args[1],
                            args[2]
                        )
                    );
                    out.flush();
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.IS_LINUX_SERVER_ACCOUNT_PROCMAIL_MANUAL)) {
                if(AOSH.checkParamCount(AOSHCommand.IS_LINUX_SERVER_ACCOUNT_PROCMAIL_MANUAL, args, 2, err)) {
                    out.println(
                        connector.simpleAOClient.isLinuxServerAccountProcmailManual(
                            args[1],
                            args[2]
                        )
                    );
                    out.flush();
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_LINUX_SERVER_ACCOUNT)) {
                if(AOSH.checkParamCount(AOSHCommand.REMOVE_LINUX_SERVER_ACCOUNT, args, 2, err)) {
                    connector.simpleAOClient.removeLinuxServerAccount(
                        args[1],
                        args[2]
                    );
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.SET_AUTORESPONDER)) {
                if(AOSH.checkParamCount(AOSHCommand.SET_AUTORESPONDER, args, 7, err)) {
                    connector.simpleAOClient.setAutoresponder(
                        args[1],
                        args[2],
                        args[3],
                        args[4],
                        args[5],
                        args[6],
                        AOSH.parseBoolean(args[7], "enabled")
                    );
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.SET_CRON_TABLE)) {
                if(AOSH.checkParamCount(AOSHCommand.SET_CRON_TABLE, args, 3, err)) {
                    connector.simpleAOClient.setCronTable(
                        args[1],
                        args[2],
                        args[3]
                    );
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_SERVER_ACCOUNT_JUNK_EMAIL_RETENTION)) {
                if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_SERVER_ACCOUNT_JUNK_EMAIL_RETENTION, args, 3, err)) {
                    connector.simpleAOClient.setLinuxServerAccountJunkEmailRetention(
                        args[1],
                        args[2],
                        args[3]==null||args[3].length()==0?-1:AOSH.parseInt(args[3], "junk_email_retention")
                    );
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_SERVER_ACCOUNT_PASSWORD)) {
                if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_SERVER_ACCOUNT_PASSWORD, args, 3, err)) {
                    connector.simpleAOClient.setLinuxServerAccountPassword(
                        args[1],
                        args[2],
                        args[3]
                    );
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_INTEGRATION_MODE)) {
                if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_INTEGRATION_MODE, args, 3, err)) {
                    connector.simpleAOClient.setLinuxServerAccountSpamAssassinIntegrationMode(
                        args[1],
                        args[2],
                        args[3]
                    );
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_REQUIRED_SCORE)) {
                if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_REQUIRED_SCORE, args, 3, err)) {
                    connector.simpleAOClient.setLinuxServerAccountSpamAssassinRequiredScore(
                        args[1],
                        args[2],
                        AOSH.parseFloat(args[3], "required_score")
                    );
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_SERVER_ACCOUNT_TRASH_EMAIL_RETENTION)) {
                if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_SERVER_ACCOUNT_TRASH_EMAIL_RETENTION, args, 3, err)) {
                    connector.simpleAOClient.setLinuxServerAccountTrashEmailRetention(
                        args[1],
                        args[2],
                        args[3]==null||args[3].length()==0?-1:AOSH.parseInt(args[3], "trash_email_retention")
                    );
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_SERVER_ACCOUNT_USE_INBOX)) {
                if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_SERVER_ACCOUNT_USE_INBOX, args, 3, err)) {
                    connector.simpleAOClient.setLinuxServerAccountUseInbox(
                        args[1],
                        args[2],
                        AOSH.parseBoolean(args[3], "use_inbox")
                    );
                }
                return true;
            }
            return false;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    boolean isHomeUsed(AOServer aoServer, String directory) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccountTable.class, "isHomeUsed(AOServer,String)", null);
        try {
            int pkey=aoServer.pkey;

            String startsWith=directory+'/';
            int startLen=startsWith.length();

            List<LinuxServerAccount> cached=getRows();
            int size=cached.size();
            for(int c=0;c<size;c++) {
                LinuxServerAccount lsa=cached.get(c);
                if(lsa.ao_server==pkey) {
                    String home=lsa.getHome();
                    if(
                        home.equals(directory)
                        || home.startsWith(startsWith)
                    ) return true;
                }
            }
            return false;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }
}