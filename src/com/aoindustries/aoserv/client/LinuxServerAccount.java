package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.unix.UnixFile;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    public static final float DEFAULT_SPAM_ASSASSIN_REQUIRED_SCORE = 3.0F;
    
    /**
     * The default SpamAssassin discard score.
     */
    public static final int DEFAULT_SPAM_ASSASSIN_DISCARD_SCORE = 20;

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
    private int sa_discard_score;

    public boolean canDisable() throws IOException, SQLException {
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

    public boolean isDisabled() {
        return disable_log!=-1;
    }

    public boolean canEnable() throws SQLException, IOException {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getLinuxAccount().disable_log==-1;
    }

    public PasswordChecker.Result[] checkPassword(Locale userLocale, String password) throws SQLException, IOException {
        return getLinuxAccount().checkPassword(userLocale, password);
    }

    public long copyHomeDirectory(final AOServer toServer) throws IOException, SQLException {
        return table.connector.requestResult(
            false,
            new AOServConnector.ResultRequest<Long>() {
                long result;
                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.COPY_HOME_DIRECTORY.ordinal());
                    out.writeCompressedInt(pkey);
                    out.writeCompressedInt(toServer.pkey);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code!=AOServProtocol.DONE) {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                    result = in.readLong();
                }

                public Long afterRelease() {
                    return result;
                }
            }
        );
    }

    public void copyPassword(LinuxServerAccount other) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.COPY_LINUX_SERVER_ACCOUNT_PASSWORD, pkey, other.pkey);
    }

    public void disable(DisableLog dl) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.LINUX_SERVER_ACCOUNTS, dl.pkey, pkey);
    }
    
    public void enable() throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.LINUX_SERVER_ACCOUNTS, pkey);
    }

    Object getColumnImpl(int i) {
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
            case 17: return sa_discard_score==-1 ? null : Integer.valueOf(sa_discard_score);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public List<CvsRepository> getCvsRepositories() throws IOException, SQLException {
        return table.connector.getCvsRepositories().getCvsRepositories(this);
    }

    public List<EmailAttachmentBlock> getEmailAttachmentBlocks() throws IOException, SQLException {
        return table.connector.getEmailAttachmentBlocks().getEmailAttachmentBlocks(this);
    }

    public String getAutoresponderContent() throws IOException, SQLException {
        String content=table.connector.requestStringQuery(true, AOServProtocol.CommandID.GET_AUTORESPONDER_CONTENT, pkey);
        if(content.length()==0) return null;
        return content;
    }

    public LinuxAccAddress getAutoresponderFrom() throws IOException, SQLException {
        if(autoresponder_from==-1) return null;
        // Might be filtered
        return table.connector.getLinuxAccAddresses().get(autoresponder_from);
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
    
    public String getCronTable() throws IOException, SQLException {
        return table.connector.requestStringQuery(true, AOServProtocol.CommandID.GET_CRON_TABLE, pkey);
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

    public DisableLog getDisableLog() throws SQLException, IOException {
        if(disable_log==-1) return null;
        DisableLog obj=table.connector.getDisableLogs().get(disable_log);
        if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
        return obj;
    }

    public List<EmailAddress> getEmailAddresses() throws SQLException, IOException {
        return table.connector.getLinuxAccAddresses().getEmailAddresses(this);
    }

    public List<HttpdSharedTomcat> getHttpdSharedTomcats() throws IOException, SQLException {
        return table.connector.getHttpdSharedTomcats().getHttpdSharedTomcats(this);
    }

    public List<HttpdSite> getHttpdSites() throws IOException, SQLException {
        return table.connector.getHttpdSites().getHttpdSites(this);
    }

    public InboxAttributes getInboxAttributes() throws IOException, SQLException {
        return table.connector.requestResult(
            true,
            new AOServConnector.ResultRequest<InboxAttributes>() {

                InboxAttributes result;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.GET_INBOX_ATTRIBUTES.ordinal());
                    out.writeCompressedInt(pkey);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        InboxAttributes attr;
                        if(in.readBoolean()) {
                            attr=new InboxAttributes(table.connector, LinuxServerAccount.this);
                            attr.read(in);
                        } else attr=null;
                        result = attr;
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public InboxAttributes afterRelease() {
                    return result;
                }
            }
        );
    }

    public long[] getImapFolderSizes(final String[] folderNames) throws IOException, SQLException {
        final long[] sizes=new long[folderNames.length];
        if(sizes.length>0) {
            table.connector.requestUpdate(
                true,
                new AOServConnector.UpdateRequest() {
                    public void writeRequest(CompressedDataOutputStream out) throws IOException {
                        out.writeCompressedInt(AOServProtocol.CommandID.GET_IMAP_FOLDER_SIZES.ordinal());
                        out.writeCompressedInt(pkey);
                        out.writeCompressedInt(folderNames.length);
                        for(int c=0;c<folderNames.length;c++) out.writeUTF(folderNames[c]);
                    }

                    public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                        int code=in.readByte();
                        if(code==AOServProtocol.DONE) {
                            for(int c=0;c<folderNames.length;c++) {
                                sizes[c]=in.readLong();
                            }
                        } else {
                            AOServProtocol.checkResult(code, in);
                            throw new IOException("Unexpected response code: "+code);
                        }
                    }

                    public void afterRelease() {
                    }
                }
            );
        }
        return sizes;
    }

    public List<LinuxAccAddress> getLinuxAccAddresses() throws IOException, SQLException {
        return table.connector.getLinuxAccAddresses().getLinuxAccAddresses(this);
    }

    public String getHome() {
        return home;
    }

    public LinuxAccount getLinuxAccount() throws SQLException, IOException {
        Username usernameObj=table.connector.getUsernames().get(username);
        if(usernameObj==null) throw new SQLException("Unable to find Username: "+username);
        LinuxAccount linuxAccountObject = usernameObj.getLinuxAccount();
        if (linuxAccountObject == null) throw new SQLException("Unable to find LinuxAccount: " + username);
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
    
    /**
     * Gets the age of trash that will be auto-deleted, in days, or <code>-1</code>
     * to not auto-delete.
     */
    public int getTrashEmailRetention() {
        return trash_email_retention;
    }
    
    /**
     * Gets the age of junk that will be auto-deleted, in days, or <code>-1</code>
     * to not auto-delete.
     */
    public int getJunkEmailRetention() {
        return junk_email_retention;
    }
    
    public EmailSpamAssassinIntegrationMode getEmailSpamAssassinIntegrationMode() throws SQLException, IOException {
        EmailSpamAssassinIntegrationMode esaim=table.connector.getEmailSpamAssassinIntegrationModes().get(sa_integration_mode);
        if(esaim==null) throw new SQLException("Unable to find EmailSpamAssassinIntegrationMode: "+sa_integration_mode);
        return esaim;
    }
    
    public float getSpamAssassinRequiredScore() {
        return sa_required_score;
    }
    
    /**
     * Gets the minimum score where spam assassin should discard email or <code>-1</code> if this
     * feature is disabled.
     */
    public int getSpamAssassinDiscardScore() {
        return sa_discard_score;
    }

    /**
     * Gets the primary <code>LinuxServerGroup</code> for this <code>LinuxServerAccount</code>
     *
     * @exception  SQLException  if the primary group is not found
     *                           or two or more groups are marked as primary
     *                           or the primary group does not exist on the same server
     */
    public LinuxServerGroup getPrimaryLinuxServerGroup() throws SQLException, IOException {
        return table.connector.getLinuxServerGroups().getPrimaryLinuxServerGroup(this);
    }

    public AOServer getAOServer() throws SQLException, IOException {
        AOServer ao=table.connector.getAoServers().get(ao_server);
        if(ao==null) throw new SQLException("Unable to find AOServer: " + ao_server);
        return ao;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.LINUX_SERVER_ACCOUNTS;
    }

    public LinuxID getUID() throws SQLException {
        LinuxID obj=table.connector.getLinuxIDs().get(uid);
        if(obj==null) throw new SQLException("Unable to find LinuxID: "+uid);
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
        sa_discard_score = result.getInt(pos++);
        if(result.wasNull()) sa_discard_score = -1;
    }

    public int isProcmailManual() throws IOException, SQLException {
        return table.connector.requestIntQuery(true, AOServProtocol.CommandID.IS_LINUX_SERVER_ACCOUNT_PROCMAIL_MANUAL, pkey);
    }

    public int arePasswordsSet() throws IOException, SQLException {
        return table.connector.requestBooleanQuery(true, AOServProtocol.CommandID.IS_LINUX_SERVER_ACCOUNT_PASSWORD_SET, pkey)?PasswordProtected.ALL:PasswordProtected.NONE;
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
        sa_discard_score = in.readCompressedInt();
    }

    public List<EmailList> getEmailLists() throws IOException, SQLException {
        return table.connector.getEmailLists().getEmailLists(this);
    }

    public List<CannotRemoveReason> getCannotRemoveReasons(Locale userLocale) throws SQLException, IOException {
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
            if(pfs.pub_linux_server_account==pkey) {
                LinuxServerAccount lsa = pfs.getLinuxServerAccount();
                reasons.add(new CannotRemoveReason<PrivateFTPServer>("Used by private FTP server "+lsa.getHome()+" on "+lsa.getAOServer().getHostname(), pfs));
            }
        }

        // No httpd_sites
        for(HttpdSite site : ao.getHttpdSites()) {
            if(site.linuxAccount.equals(username)) reasons.add(new CannotRemoveReason<HttpdSite>("Used by website "+site.getInstallDirectory()+" on "+site.getAOServer().getHostname(), site));
        }

        return reasons;
    }

    public void remove() throws IOException, SQLException {
        table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.LINUX_SERVER_ACCOUNTS,
            pkey
        );
    }

    public void setImapFolderSubscribed(String folder, boolean subscribed) throws IOException, SQLException {
        table.connector.requestUpdate(true, AOServProtocol.CommandID.SET_IMAP_FOLDER_SUBSCRIBED, pkey, folder, subscribed);
    }

    public void setCronTable(String cronTable) throws IOException, SQLException {
        table.connector.requestUpdate(true, AOServProtocol.CommandID.SET_CRON_TABLE, pkey, cronTable);
    }

    public void setPassword(String password) throws IOException, SQLException {
        AOServConnector connector=table.connector;
        if(!connector.isSecure()) throw new IOException("Passwords for linux accounts may only be set when using secure protocols.  Currently using the "+connector.getProtocol()+" protocol, which is not secure.");
        connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_PASSWORD, pkey, password);
    }

    public void setAutoresponder(
        final LinuxAccAddress from,
        final String subject,
        final String content,
        final boolean enabled
    ) throws IOException, SQLException {
        table.connector.requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.SET_AUTORESPONDER.ordinal());
                    out.writeCompressedInt(pkey);
                    out.writeCompressedInt(from==null?-1:from.getPkey());
                    out.writeBoolean(subject!=null);
                    if(subject!=null) out.writeUTF(subject);
                    out.writeBoolean(content!=null);
                    if(content!=null) out.writeUTF(content);
                    out.writeBoolean(enabled);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
                    else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public void afterRelease() {
                    table.connector.tablesUpdated(invalidateList);
                }
            }
        );
    }

    public void setTrashEmailRetention(int days) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_TRASH_EMAIL_RETENTION, pkey, days);
    }

    public void setJunkEmailRetention(int days) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_JUNK_EMAIL_RETENTION, pkey, days);
    }

    public void setEmailSpamAssassinIntegrationMode(EmailSpamAssassinIntegrationMode mode) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_EMAIL_SPAMASSASSIN_INTEGRATION_MODE, pkey, mode.pkey);
    }

    public void setSpamAssassinRequiredScore(float required_score) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_REQUIRED_SCORE, pkey, required_score);
    }

    public void setSpamAssassinDiscardScore(int discard_score) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_DISCARD_SCORE, pkey, discard_score);
    }

    public void setUseInbox(boolean useInbox) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_USE_INBOX, pkey, useInbox);
    }

    public void setPredisablePassword(final String password) throws IOException, SQLException {
        table.connector.requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_PREDISABLE_PASSWORD.ordinal());
                    out.writeCompressedInt(pkey);
                    out.writeNullUTF(password);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
                    else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public void afterRelease() {
                    table.connector.tablesUpdated(invalidateList);
                }
            }
        );
    }

    @Override
    String toStringImpl(Locale userLocale) throws SQLException, IOException {
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
        if(version.compareTo(AOServProtocol.Version.VERSION_1_40)>=0) {
            out.writeCompressedInt(sa_discard_score);
        }
    }

    public boolean canSetPassword() throws IOException, SQLException {
        return disable_log==-1 && getLinuxAccount().canSetPassword();
    }
    
    public boolean passwordMatches(String password) throws IOException, SQLException {
        return table.connector.requestBooleanQuery(true, AOServProtocol.CommandID.COMPARE_LINUX_SERVER_ACCOUNT_PASSWORD, pkey, password);
    }

    public int addEmailAddress(EmailAddress address) throws IOException, SQLException {
        return table.connector.getLinuxAccAddresses().addLinuxAccAddress(address, this);
    }
}