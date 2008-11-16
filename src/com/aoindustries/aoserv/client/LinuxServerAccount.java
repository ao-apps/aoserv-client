package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.io.unix.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * A <code>LinuxServerAccount</code> grants a <code>LinuxAccount</code>
 * access to an <code>AOServer</code>.
 *
 * @see  LinuxAccount
 * @see  AOServer
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxServerAccount extends CachedObjectIntegerKey<LinuxServerAccount> implements Removable, PasswordProtected, Disablable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_USERNAME=1,
        COLUMN_AO_SERVER=2
    ;
    static final String COLUMN_USERNAME_name = "username";
    static final String COLUMN_AO_SERVER_name = "ao_server";

    /**
     * The default number of days email messages will be kept in the "Trash" folder.
     */
    public static final int DEFAULT_TRASH_EMAIL_RETENTION=31;

    /**
     * The default number of days email messages will be kept in the "Junk" folder.
     */
    public static final int DEFAULT_JUNK_EMAIL_RETENTION=31;
    
    /**
     * The default SpamAssassin required score.
     */
    public static final float DEFAULT_SPAM_ASSASSIN_REQUIRED_SCORE=3.0F;

    String username;
    int ao_server;
    int uid;
    private String home;
    int autoresponder_from;
    private String autoresponder_subject;
    private String autoresponder_path;
    private boolean is_autoresponder_enabled;
    int disable_log;
    private String predisable_password;
    private long created;
    private boolean use_inbox;
    private int trash_email_retention;
    private int junk_email_retention;
    private String sa_integration_mode;
    private float sa_required_score;

    public boolean canDisable() {
        // already disabled
        if(disable_log!=-1) return false;

        // is a system user
        if(uid<UnixFile.MINIMUM_USER_UID) return false;

        // cvs_repositories
        for(CvsRepository cr : getCvsRepositories()) if(cr.disable_log==-1) return false;

        // httpd_shared_tomcats
        for(HttpdSharedTomcat hst : getHttpdSharedTomcats()) if(hst.disable_log==-1) return false;

        // email_lists
        for(EmailList el : getEmailLists()) if(el.disable_log==-1) return false;

        // httpd_sites
        for(HttpdSite hs : getHttpdSites()) if(hs.disable_log==-1) return false;

        return true;
    }

    public boolean canEnable() {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getLinuxAccount().disable_log==-1;
    }

    public PasswordChecker.Result[] checkPassword(Locale userLocale, String password) {
        return getLinuxAccount().checkPassword(userLocale, password);
    }

    public long copyHomeDirectory(AOServer toServer) {
        try {
            AOServConnection connection=table.connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.CommandID.COPY_HOME_DIRECTORY.ordinal());
                out.writeCompressedInt(pkey);
                out.writeCompressedInt(toServer.pkey);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code!=AOServProtocol.DONE) AOServProtocol.checkResult(code, in);
                return in.readLong();
            } catch(IOException err) {
                    connection.close();
                throw err;
            } finally {
                table.connector.releaseConnection(connection);
            }
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public void copyPassword(LinuxServerAccount other) {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.COPY_LINUX_SERVER_ACCOUNT_PASSWORD, pkey, other.pkey);
    }

    public void disable(DisableLog dl) {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.LINUX_SERVER_ACCOUNTS, dl.pkey, pkey);
    }
    
    public void enable() {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.LINUX_SERVER_ACCOUNTS, pkey);
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_USERNAME: return username;
            case COLUMN_AO_SERVER: return Integer.valueOf(ao_server);
            case 3: return Integer.valueOf(uid);
            case 4: return home;
            case 5: return autoresponder_from==-1?null:Integer.valueOf(autoresponder_from);
            case 6: return autoresponder_subject;
            case 7: return autoresponder_path;
            case 8: return is_autoresponder_enabled?Boolean.TRUE:Boolean.FALSE;
            case 9: return disable_log==-1?null:Integer.valueOf(disable_log);
            case 10: return predisable_password;
            case 11: return new java.sql.Date(created);
            case 12: return use_inbox?Boolean.TRUE:Boolean.FALSE;
            case 13: return trash_email_retention==-1?null:Integer.valueOf(trash_email_retention);
            case 14: return junk_email_retention==-1?null:Integer.valueOf(junk_email_retention);
            case 15: return sa_integration_mode;
            case 16: return new Float(sa_required_score);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public List<CvsRepository> getCvsRepositories() {
        return table.connector.cvsRepositories.getCvsRepositories(this);
    }

    public List<EmailAttachmentBlock> getEmailAttachmentBlocks() {
        return table.connector.emailAttachmentBlocks.getEmailAttachmentBlocks(this);
    }

    public String getAutoresponderContent() {
        String content=table.connector.requestStringQuery(AOServProtocol.CommandID.GET_AUTORESPONDER_CONTENT, pkey);
        if(content.length()==0) return null;
        return content;
    }

    public LinuxAccAddress getAutoresponderFrom() {
        if(autoresponder_from==-1) return null;
        // Might be filtered
        return table.connector.linuxAccAddresses.get(autoresponder_from);
    }

    public String getAutoresponderSubject() {
        return autoresponder_subject;
    }
    
    public String getAutoresponderPath() {
        return autoresponder_path;
    }

    public boolean isAutoresponderEnabled() {
        return is_autoresponder_enabled;
    }
    
    public String getCronTable() {
        return table.connector.requestStringQuery(AOServProtocol.CommandID.GET_CRON_TABLE, pkey);
    }

    /**
     * @deprecated  Please provide the locale for generated errors.
     */
    public static String getDefaultHomeDirectory(String username) {
        return getDefaultHomeDirectory(username, Locale.getDefault());
    }

    public static String getDefaultHomeDirectory(String username, Locale locale) {
        String check = Username.checkUsername(username, locale);
        if(check!=null) throw new IllegalArgumentException(check);
        return "/home/"+username.charAt(0)+'/'+username;
    }

    public DisableLog getDisableLog() {
        if(disable_log==-1) return null;
        DisableLog obj=table.connector.disableLogs.get(disable_log);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find DisableLog: "+disable_log));
        return obj;
    }

    public List<EmailAddress> getEmailAddresses() {
        return table.connector.linuxAccAddresses.getEmailAddresses(this);
    }

    public List<HttpdSharedTomcat> getHttpdSharedTomcats() {
        return table.connector.httpdSharedTomcats.getHttpdSharedTomcats(this);
    }

    public List<HttpdSite> getHttpdSites() {
        return table.connector.httpdSites.getHttpdSites(this);
    }

    public InboxAttributes getInboxAttributes() {
        try {
            AOServConnection connection=table.connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.CommandID.GET_INBOX_ATTRIBUTES.ordinal());
                out.writeCompressedInt(pkey);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) {
                    InboxAttributes attr;
                    if(in.readBoolean()) {
                        attr=new InboxAttributes(table.connector, this);
                        attr.read(in);
                    } else attr=null;
                    return attr;
                } else {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unexpected response code: "+code);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                table.connector.releaseConnection(connection);
            }
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public long[] getImapFolderSizes(String[] folderNames) {
        try {
            long[] sizes=new long[folderNames.length];
            if(sizes.length>0) {
                AOServConnection connection=table.connector.getConnection();
                try {
                    CompressedDataOutputStream out=connection.getOutputStream();
                    out.writeCompressedInt(AOServProtocol.CommandID.GET_IMAP_FOLDER_SIZES.ordinal());
                    out.writeCompressedInt(pkey);
                    out.writeCompressedInt(folderNames.length);
                    for(int c=0;c<folderNames.length;c++) out.writeUTF(folderNames[c]);
                    out.flush();

                    CompressedDataInputStream in=connection.getInputStream();
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        for(int c=0;c<folderNames.length;c++) {
                            sizes[c]=in.readLong();
                        }
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                } catch(IOException err) {
                    connection.close();
                    throw err;
                } finally {
                    table.connector.releaseConnection(connection);
                }
            }
            return sizes;
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public List<LinuxAccAddress> getLinuxAccAddresses() {
        return table.connector.linuxAccAddresses.getLinuxAccAddresses(this);
    }

    public String getHome() {
        return home;
    }

    public LinuxAccount getLinuxAccount() {
        Username usernameObj=table.connector.usernames.get(username);
        if(usernameObj==null) throw new WrappedException(new SQLException("Unable to find Username: "+username));
        LinuxAccount linuxAccountObject = usernameObj.getLinuxAccount();
        if (linuxAccountObject == null) throw new WrappedException(new SQLException("Unable to find LinuxAccount: " + username));
        return linuxAccountObject;
    }

    public String getPredisablePassword() {
        return predisable_password;
    }
    
    public long getCreated() {
        return created;
    }
    
    public boolean useInbox() {
        return use_inbox;
    }
    
    public int getTrashEmailRetention() {
        return trash_email_retention;
    }
    
    public int getJunkEmailRetention() {
        return junk_email_retention;
    }
    
    public EmailSpamAssassinIntegrationMode getEmailSpamAssassinIntegrationMode() {
        EmailSpamAssassinIntegrationMode esaim=table.connector.emailSpamAssassinIntegrationModes.get(sa_integration_mode);
        if(esaim==null) throw new WrappedException(new SQLException("Unable to find EmailSpamAssassinIntegrationMode: "+sa_integration_mode));
        return esaim;
    }
    
    public float getSpamAssassinRequiredScore() {
        return sa_required_score;
    }

    /**
     * Gets the primary <code>LinuxServerGroup</code> for this <code>LinuxServerAccount</code>
     *
     * @exception  SQLException  if the primary group is not found
     *                           or two or more groups are marked as primary
     *                           or the primary group does not exist on the same server
     */
    public LinuxServerGroup getPrimaryLinuxServerGroup() {
        return table.connector.linuxServerGroups.getPrimaryLinuxServerGroup(this);
    }

    public AOServer getAOServer() {
        AOServer ao=table.connector.aoServers.get(ao_server);
        if(ao==null) throw new WrappedException(new SQLException("Unable to find AOServer: " + ao_server));
        return ao;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.LINUX_SERVER_ACCOUNTS;
    }

    public LinuxID getUID() {
        LinuxID obj=table.connector.linuxIDs.get(uid);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find LinuxID: "+uid));
        return obj;
    }

    public void init(ResultSet result) throws SQLException {
        int pos=1;
        pkey=result.getInt(pos++);
        username=result.getString(pos++);
        ao_server=result.getInt(pos++);
        uid=result.getInt(pos++);
        home=result.getString(pos++);
        autoresponder_from=result.getInt(pos++);
        if(result.wasNull()) autoresponder_from=-1;
        autoresponder_subject = result.getString(pos++);
        autoresponder_path = result.getString(pos++);
        is_autoresponder_enabled=result.getBoolean(pos++);
        disable_log=result.getInt(pos++);
        if(result.wasNull()) disable_log=-1;
        predisable_password=result.getString(pos++);
        created=result.getTimestamp(pos++).getTime();
        use_inbox=result.getBoolean(pos++);
        trash_email_retention=result.getInt(pos++);
        if(result.wasNull()) trash_email_retention=-1;
        junk_email_retention=result.getInt(pos++);
        if(result.wasNull()) junk_email_retention=-1;
        sa_integration_mode=result.getString(pos++);
        sa_required_score=result.getFloat(pos++);
    }

    public int isProcmailManual() {
        return table.connector.requestIntQuery(AOServProtocol.CommandID.IS_LINUX_SERVER_ACCOUNT_PROCMAIL_MANUAL, pkey);
    }

    public int arePasswordsSet() {
        return table.connector.requestBooleanQuery(AOServProtocol.CommandID.IS_LINUX_SERVER_ACCOUNT_PASSWORD_SET, pkey)?PasswordProtected.ALL:PasswordProtected.NONE;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        username=in.readUTF().intern();
        ao_server=in.readCompressedInt();
        uid=in.readCompressedInt();
        home=in.readUTF();
        autoresponder_from=in.readCompressedInt();
        autoresponder_subject=in.readNullUTF();
        autoresponder_path=in.readNullUTF();
        is_autoresponder_enabled=in.readBoolean();
        disable_log=in.readCompressedInt();
        predisable_password=in.readNullUTF();
        created=in.readLong();
        use_inbox=in.readBoolean();
        trash_email_retention=in.readCompressedInt();
        junk_email_retention=in.readCompressedInt();
        sa_integration_mode=in.readUTF().intern();
        sa_required_score=in.readFloat();
    }

    public List<EmailList> getEmailLists() {
        return table.connector.emailLists.getEmailLists(this);
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        if(uid<UnixFile.MINIMUM_USER_UID) reasons.add(new CannotRemoveReason<LinuxServerAccount>("Not allowed to remove accounts with UID less than "+UnixFile.MINIMUM_USER_UID));

        AOServer ao=getAOServer();

        // No CVS repositories
        for(CvsRepository cr : ao.getCvsRepositories()) {
            if(cr.linux_server_account==pkey) reasons.add(new CannotRemoveReason<CvsRepository>("Used by CVS repository "+cr.getPath()+" on "+cr.getLinuxServerAccount().getAOServer().getHostname(), cr));
        }

        // No email lists
        for(EmailList el : getEmailLists()) {
            reasons.add(new CannotRemoveReason<EmailList>("Used by email list "+el.getPath()+" on "+el.getLinuxServerAccount().getAOServer().getHostname(), el));
        }

        // No httpd_servers
        for(HttpdServer hs : ao.getHttpdServers()) {
            if(hs.linux_server_account==pkey) reasons.add(new CannotRemoveReason<HttpdServer>("Used by Apache server #"+hs.getNumber()+" on "+hs.getAOServer().getHostname(), hs));
        }

        // No httpd shared tomcats
        for(HttpdSharedTomcat hst : ao.getHttpdSharedTomcats()) {
            if(hst.linux_server_account==pkey) reasons.add(new CannotRemoveReason<HttpdSharedTomcat>("Used by Multi-Site Tomcat JVM "+hst.getInstallDirectory()+" on "+hst.getAOServer().getHostname(), hst));
        }

        // No majordomo_servers
        for(MajordomoServer ms : ao.getMajordomoServers()) {
            if(ms.linux_server_account==pkey) {
                EmailDomain ed=ms.getDomain();
                reasons.add(new CannotRemoveReason<MajordomoServer>("Used by Majordomo server "+ed.getDomain()+" on "+ed.getAOServer().getHostname(), ms));
            }
        }

        // No private FTP servers
        for(PrivateFTPServer pfs : ao.getPrivateFTPServers()) {
            if(pfs.pub_linux_server_account==pkey) reasons.add(new CannotRemoveReason<PrivateFTPServer>("Used by private FTP server "+pfs.getRoot()+" on "+pfs.getLinuxServerAccount().getAOServer().getHostname(), pfs));
        }

        // No httpd_sites
        for(HttpdSite site : ao.getHttpdSites()) {
            if(site.linuxAccount.equals(username)) reasons.add(new CannotRemoveReason<HttpdSite>("Used by website "+site.getInstallDirectory()+" on "+site.getAOServer().getHostname(), site));
        }

        return reasons;
    }

    public void remove() {
        table.connector.requestUpdateIL(
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.LINUX_SERVER_ACCOUNTS,
            pkey
        );
    }

    public void setImapFolderSubscribed(String folder, boolean subscribed) {
        table.connector.requestUpdate(AOServProtocol.CommandID.SET_IMAP_FOLDER_SUBSCRIBED, pkey, folder, subscribed);
    }

    public void setCronTable(String cronTable) {
        table.connector.requestUpdate(AOServProtocol.CommandID.SET_CRON_TABLE, pkey, cronTable);
    }

    public void setPassword(String password) {
        AOServConnector connector=table.connector;
        if(!connector.isSecure()) throw new WrappedException(new IOException("Passwords for linux accounts may only be set when using secure protocols.  Currently using the "+connector.getProtocol()+" protocol, which is not secure."));
        connector.requestUpdateIL(AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_PASSWORD, pkey, password);
    }

    public void setAutoresponder(
        LinuxAccAddress from,
        String subject,
        String content,
        boolean enabled
    ) {
        try {
            IntList invalidateList;
            AOServConnection connection=table.connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.CommandID.SET_AUTORESPONDER.ordinal());
                out.writeCompressedInt(pkey);
                out.writeCompressedInt(from==null?-1:from.getPkey());
                out.writeBoolean(subject!=null);
                if(subject!=null) out.writeUTF(subject);
                out.writeBoolean(content!=null);
                if(content!=null) out.writeUTF(content);
                out.writeBoolean(enabled);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
                else {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unexpected response code: "+code);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                table.connector.releaseConnection(connection);
            }
            table.connector.tablesUpdated(invalidateList);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public void setTrashEmailRetention(int days) {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_TRASH_EMAIL_RETENTION, pkey, days);
    }

    public void setJunkEmailRetention(int days) {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_JUNK_EMAIL_RETENTION, pkey, days);
    }

    public void setEmailSpamAssassinIntegrationMode(EmailSpamAssassinIntegrationMode mode) {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_EMAIL_SPAMASSASSIN_INTEGRATION_MODE, pkey, mode.pkey);
    }

    public void setSpamAssassinRequiredScore(float required_score) {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_REQUIRED_SCORE, pkey, required_score);
    }

    public void setUseInbox(boolean useInbox) {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_USE_INBOX, pkey, useInbox);
    }

    public void setPredisablePassword(String password) {
        try {
            IntList invalidateList;
            AOServConnector connector=table.connector;
            AOServConnection connection=connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_PREDISABLE_PASSWORD.ordinal());
                out.writeCompressedInt(pkey);
                out.writeNullUTF(password);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
                else {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unexpected response code: "+code);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                connector.releaseConnection(connection);
            }
            connector.tablesUpdated(invalidateList);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    String toStringImpl() {
        return username+" on "+getAOServer().getHostname();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeUTF(username);
        out.writeCompressedInt(ao_server);
        out.writeCompressedInt(uid);
        out.writeUTF(home);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
            out.writeShort(0);
            out.writeShort(7);
            out.writeShort(0);
            out.writeShort(7);
            out.writeShort(0);
            out.writeShort(7);
        }
        out.writeCompressedInt(autoresponder_from);
        out.writeNullUTF(autoresponder_subject);
        out.writeNullUTF(autoresponder_path);
        out.writeBoolean(is_autoresponder_enabled);
        out.writeCompressedInt(disable_log);
        out.writeNullUTF(predisable_password);
        out.writeLong(created);
        out.writeBoolean(use_inbox);
        out.writeCompressedInt(trash_email_retention);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_120)>=0) {
            out.writeCompressedInt(junk_email_retention);
            out.writeUTF(sa_integration_mode);
        }
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_124)>=0) {
            out.writeFloat(sa_required_score);
        }
    }

    public boolean canSetPassword() {
        return disable_log==-1 && getLinuxAccount().canSetPassword();
    }
    
    public boolean passwordMatches(String password) {
        return table.connector.requestBooleanQuery(AOServProtocol.CommandID.COMPARE_LINUX_SERVER_ACCOUNT_PASSWORD, pkey, password);
    }

    public int addEmailAddress(EmailAddress address) {
        return table.connector.linuxAccAddresses.addLinuxAccAddress(address, this);
    }
}