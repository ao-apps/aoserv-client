/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.email_inboxes)
public interface EmailInboxService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,Integer,EmailInbox> {

    /* TODO
    int addLinuxServerAccount(LinuxAccount linuxAccount, AOServer aoServer, String home) throws IOException, SQLException {
        int pkey=connector.requestIntQueryIL(
            true,
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.LINUX_SERVER_ACCOUNTS,
            linuxAccount.pkey,
            aoServer.pkey,
            home
        );
        return pkey;
    }

    List<LinuxServerAccount> getAlternateLinuxServerAccounts(LinuxServerGroup group) throws SQLException, IOException {
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
                LinuxGroupAccount linuxGroupAccount = connector.getLinuxGroupAccounts().getLinuxGroupAccount(groupName, username);
                if (linuxGroupAccount != null && !linuxGroupAccount.is_primary) matches.add(linuxServerAccount);
            }
        }
        return matches;
    }
    */
    /**
     * Finds a LinuxServerAccount by a username and password combination.  It tries to return an account that is not disabled and
     * matches both username and password.
     *
     * @return   the <code>LinuxServerAccount</code> found if a non-disabled username and password are found, or <code>null</code> if no match found
     *
     * @exception  LoginException  if a possible account match is found but the account is disabled or has a different password
     */
    /* TODO
    public LinuxServerAccount getLinuxServerAccountFromUsernamePassword(String username, String password, boolean emailOnly) throws LoginException, IOException, SQLException {
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
    }
    */
    /**
     * Finds a LinuxServerAccount by an email address and password combination.  It tries to return an account that is not disabled and
     * matches both email address and password.
     *
     * @return   the <code>LinuxServerAccount</code> found if a non-disabled email address and password are found, or <code>null</code> if no match found
     *
     * @exception  LoginException  if a possible account match is found but the account is disabled or has a different password
     */
    /* TODO
    public LinuxServerAccount getLinuxServerAccountFromEmailAddress(String address, String domain, String password) throws LoginException, IOException, SQLException {
        LinuxServerAccount badPasswordLSA=null;
        LinuxServerAccount disabledLSA=null;

        List<EmailDomain> domains=connector.getEmailDomains().getRows();
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
    }

    public List<LinuxServerAccount> getMailAccounts() throws IOException, SQLException {
        List<LinuxServerAccount> cached = getRows();
        int len = cached.size();
        List<LinuxServerAccount> matches=new ArrayList<LinuxServerAccount>(len);
        for (int c = 0; c < len; c++) {
            LinuxServerAccount lsa=cached.get(c);
            if(lsa.getLinuxAccount().getType().isEmail()) matches.add(lsa);
        }
        return matches;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_LINUX_SERVER_ACCOUNT)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_LINUX_SERVER_ACCOUNT, args, 3, err)) {
                int pkey=connector.getSimpleAOClient().addLinuxServerAccount(
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
                boolean result=connector.getSimpleAOClient().compareLinuxServerAccountPassword(
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
                long byteCount=connector.getSimpleAOClient().copyHomeDirectory(
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
                connector.getSimpleAOClient().copyLinuxServerAccountPassword(
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
                    connector.getSimpleAOClient().disableLinuxServerAccount(
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
                connector.getSimpleAOClient().enableLinuxServerAccount(args[1], args[2]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.GET_CRON_TABLE)) {
            if(AOSH.checkParamCount(AOSHCommand.GET_CRON_TABLE, args, 2, err)) {
                out.print(
                    connector.getSimpleAOClient().getCronTable(
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
                long[] sizes=connector.getSimpleAOClient().getImapFolderSizes(args[1], args[2], folderNames);
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
                InboxAttributes attr=connector.getSimpleAOClient().getInboxAttributes(args[1], args[2]);
                out.print("System Time..: ");
                out.println(attr==null ? "Server Unavailable" : SQLUtility.getDateTime(attr.getSystemTime()));
                out.print("File Size....: ");
                out.println(attr==null ? "Server Unavailable" : attr.getFileSize());
                out.print("Last Modified: ");
                if(attr==null) out.println("Server Unavailable");
                else {
                    long lastModified = attr.getLastModified();
                    if(lastModified==0) out.println("Unknown");
                    else out.println(SQLUtility.getDateTime(lastModified));
                }
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.IS_LINUX_SERVER_ACCOUNT_PASSWORD_SET)) {
            if(AOSH.checkParamCount(AOSHCommand.IS_LINUX_SERVER_ACCOUNT_PASSWORD_SET, args, 2, err)) {
                out.println(
                    connector.getSimpleAOClient().isLinuxServerAccountPasswordSet(
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
                    connector.getSimpleAOClient().isLinuxServerAccountProcmailManual(
                        args[1],
                        args[2]
                    )
                );
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_LINUX_SERVER_ACCOUNT)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_LINUX_SERVER_ACCOUNT, args, 2, err)) {
                connector.getSimpleAOClient().removeLinuxServerAccount(
                    args[1],
                    args[2]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_AUTORESPONDER)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_AUTORESPONDER, args, 7, err)) {
                connector.getSimpleAOClient().setAutoresponder(
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
                connector.getSimpleAOClient().setCronTable(
                    args[1],
                    args[2],
                    args[3]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_SERVER_ACCOUNT_JUNK_EMAIL_RETENTION)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_SERVER_ACCOUNT_JUNK_EMAIL_RETENTION, args, 3, err)) {
                connector.getSimpleAOClient().setLinuxServerAccountJunkEmailRetention(
                    args[1],
                    args[2],
                    args[3]==null||args[3].length()==0?-1:AOSH.parseInt(args[3], "junk_email_retention")
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_SERVER_ACCOUNT_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_SERVER_ACCOUNT_PASSWORD, args, 3, err)) {
                connector.getSimpleAOClient().setLinuxServerAccountPassword(
                    args[1],
                    args[2],
                    args[3]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_INTEGRATION_MODE)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_INTEGRATION_MODE, args, 3, err)) {
                connector.getSimpleAOClient().setLinuxServerAccountSpamAssassinIntegrationMode(
                    args[1],
                    args[2],
                    args[3]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_REQUIRED_SCORE)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_REQUIRED_SCORE, args, 3, err)) {
                connector.getSimpleAOClient().setLinuxServerAccountSpamAssassinRequiredScore(
                    args[1],
                    args[2],
                    AOSH.parseFloat(args[3], "required_score")
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_SERVER_ACCOUNT_TRASH_EMAIL_RETENTION)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_SERVER_ACCOUNT_TRASH_EMAIL_RETENTION, args, 3, err)) {
                connector.getSimpleAOClient().setLinuxServerAccountTrashEmailRetention(
                    args[1],
                    args[2],
                    args[3]==null||args[3].length()==0?-1:AOSH.parseInt(args[3], "trash_email_retention")
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_SERVER_ACCOUNT_USE_INBOX)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_SERVER_ACCOUNT_USE_INBOX, args, 3, err)) {
                connector.getSimpleAOClient().setLinuxServerAccountUseInbox(
                    args[1],
                    args[2],
                    AOSH.parseBoolean(args[3], "use_inbox")
                );
            }
            return true;
        }
        return false;
    }

    boolean isHomeUsed(AOServer aoServer, String directory) throws IOException, SQLException {
        int pkey=aoServer.pkey;

        String startsWith=directory+'/';

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
    }
     */
}