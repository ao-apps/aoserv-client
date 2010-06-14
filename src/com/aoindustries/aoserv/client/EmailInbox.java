/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * An <code>EmailInbox</code> exists for each shell account and email inbox type of LinuxAccount.
 *
 * @see  LinuxAccount
 *
 * @author  AO Industries, Inc.
 */
final public class EmailInbox extends AOServObjectIntegerKey<EmailInbox> implements BeanFactory<com.aoindustries.aoserv.client.beans.EmailInbox> /* TODO , Removable, Disablable */ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The default number of days email messages will be kept in the "Trash" folder.
     */
    public static final short DEFAULT_TRASH_EMAIL_RETENTION=31;

    /**
     * The default number of days email messages will be kept in the "Junk" folder.
     */
    public static final short DEFAULT_JUNK_EMAIL_RETENTION=31;
    
    /**
     * The default SpamAssassin required score.
     */
    public static final float DEFAULT_SPAM_ASSASSIN_REQUIRED_SCORE = 3.0F;
    
    /**
     * The default SpamAssassin discard score.
     */
    public static final int DEFAULT_SPAM_ASSASSIN_DISCARD_SCORE = 20;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private Integer autoresponderFrom;
    private String autoresponderSubject;
    private UnixPath autoresponderPath;
    final private boolean isAutoresponderEnabled;
    final private boolean useInbox;
    final private Short trashEmailRetention;
    final private Short junkEmailRetention;
    private String saIntegrationMode;
    final private float saRequiredScore;
    final private Integer saDiscardScore;

    public EmailInbox(
        EmailInboxService<?,?> service,
        int linuxAccount,
        Integer autoresponderFrom,
        String autoresponderSubject,
        UnixPath autoresponderPath,
        boolean isAutoresponderEnabled,
        boolean useInbox,
        Short trashEmailRetention,
        Short junkEmailRetention,
        String saIntegrationMode,
        float saRequiredScore,
        Integer saDiscardScore
    ) {
        super(service, linuxAccount);
        this.autoresponderFrom = autoresponderFrom;
        this.autoresponderSubject = autoresponderSubject;
        this.autoresponderPath = autoresponderPath;
        this.isAutoresponderEnabled = isAutoresponderEnabled;
        this.useInbox = useInbox;
        this.trashEmailRetention = trashEmailRetention;
        this.junkEmailRetention = junkEmailRetention;
        this.saIntegrationMode = saIntegrationMode;
        this.saRequiredScore = saRequiredScore;
        this.saDiscardScore = saDiscardScore;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        autoresponderSubject = intern(autoresponderSubject);
        autoresponderPath = intern(autoresponderPath);
        saIntegrationMode = intern(saIntegrationMode);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(EmailInbox other) throws RemoteException {
        return key==other.key ? 0 : getLinuxAccount().compareTo(other.getLinuxAccount());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="linux_account", index=IndexType.PRIMARY_KEY, description="the Linux account that supports this inbox")
    public LinuxAccount getLinuxAccount() throws RemoteException {
        return getService().getConnector().getLinuxAccounts().get(key);
    }

    /* TODO
    @SchemaColumn(order=1, name="autoresponder_from", description="the pkey of the email address used for the autoresponder")
    public EmailInboxAddress getAutoresponderFrom() throws RemoteException {
        if(autoresponderFrom==null) return null;
        return getService().getConnector().getEmailInboxAddresses().get(autoresponderFrom);
    } */

    @SchemaColumn(order=1, name="autoresponder_subject", description="the subject of autoresponder messages")
    public String getAutoresponderSubject() {
        return autoresponderSubject;
    }

    @SchemaColumn(order=2, name="autoresponder_path", description="the full path of the autoresponder text file")
    public UnixPath getAutoresponderPath() {
        return autoresponderPath;
    }

    @SchemaColumn(order=3, name="isAutoresponder_enabled", description="flags if the autoresponder is enabled")
    public boolean isAutoresponderEnabled() {
        return isAutoresponderEnabled;
    }

    @SchemaColumn(order=4, name="use_inbox", description="email for this account will be stored in the inbox, otherwise it is just deleted")
    public boolean getUseInbox() {
        return useInbox;
    }

    static final String COLUMN_TRASH_EMAIL_RETENTION = "trash_email_retention";
    @SchemaColumn(order=5, name=COLUMN_TRASH_EMAIL_RETENTION, index=IndexType.INDEXED, description="the number of days before messages in the Trash folder are automatically removed.")
    public BackupRetention getTrashEmailRetention() throws RemoteException {
        if(trashEmailRetention==null) return null;
        return getService().getConnector().getBackupRetentions().get(trashEmailRetention);
    }

    static final String COLUMN_JUNK_EMAIL_RETENTION = "junk_email_retention";
    @SchemaColumn(order=6, name=COLUMN_JUNK_EMAIL_RETENTION, index=IndexType.INDEXED, description="the number of days before messages in the Junk folder are automatically removed.")
    public BackupRetention getJunkEmailRetention() throws RemoteException {
        if(junkEmailRetention==null) return null;
        return getService().getConnector().getBackupRetentions().get(junkEmailRetention);
    }

    static final String COLUMN_SA_INTEGRATION_MODE = "sa_integration_mode";
    @SchemaColumn(order=7, name=COLUMN_SA_INTEGRATION_MODE, index=IndexType.INDEXED, description="the integration mode for SpamAssassin")
    public EmailSpamAssassinIntegrationMode getEmailSpamAssassinIntegrationMode() throws RemoteException {
        return getService().getConnector().getEmailSpamAssassinIntegrationModes().get(saIntegrationMode);
    }

    @SchemaColumn(order=8, name="sa_required_score", description="the minimum SpamAssassin score considered Junk")
    public float getSpamAssassinRequiredScore() {
        return saRequiredScore;
    }

    @SchemaColumn(order=9, name="sa_discard_score", description="the minimum SpamAssassin score that will be discarded instead of tagged or placed in the Junk folder")
    public Integer getSpamAssassinDiscardScore() {
        return saDiscardScore;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    @Override
    public com.aoindustries.aoserv.client.beans.EmailInbox getBean() {
        return new com.aoindustries.aoserv.client.beans.EmailInbox(key, autoresponderFrom, autoresponderSubject, getBean(autoresponderPath), isAutoresponderEnabled, useInbox, trashEmailRetention, junkEmailRetention, saIntegrationMode, saRequiredScore, saDiscardScore);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getLinuxAccount(),
            // Caused cycle: getAutoresponderFrom(),
            getTrashEmailRetention(),
            getJunkEmailRetention(),
            getEmailSpamAssassinIntegrationMode()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            AOServObjectUtils.createDependencySet(
                getBrandFromSmtpEmailInbox(),
                getBrandFromImapEmailInbox()
            )
            // TODO: getEmailAttachmentBlocks()
            // TODO: getEmailInboxAddresses()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        LinuxAccount la = getLinuxAccount();
        return ApplicationResources.accessor.getMessage("EmailInbox.toString", la.getUsername().getUsername(), la.getAoServerResource().getAoServer().getHostname());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public Brand getBrandFromSmtpEmailInbox() throws RemoteException {
        return getService().getConnector().getBrands().filterUnique(Brand.COLUMN_SMTP_EMAIL_INBOX, this);
    }

    public Brand getBrandFromImapEmailInbox() throws RemoteException {
        return getService().getConnector().getBrands().filterUnique(Brand.COLUMN_IMAP_EMAIL_INBOX, this);
    }

    /* TODO
    public IndexedSet<EmailAttachmentBlock> getEmailAttachmentBlocks() throws IOException, SQLException {
        return getService().getConnector().getEmailAttachmentBlocks().getEmailAttachmentBlocks(this);
    }

    public List<EmailAddress> getEmailAddresses() throws SQLException, IOException {
        return getService().getConnector().getLinuxAccAddresses().getEmailAddresses(this);
    }

    public List<EmailInboxAddress> getEmailInboxAddresses() throws IOException, SQLException {
        return getService().getConnector().getLinuxAccAddresses().getLinuxAccAddresses(this);
    }
     */
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
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

    public PasswordChecker.Result[] checkPassword(String password) throws SQLException, IOException {
        return getLinuxAccount().checkPassword(password);
    }

    public void copyPassword(LinuxServerAccount other) throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.COPY_LINUX_SERVER_ACCOUNT_PASSWORD, pkey, other.pkey);
    }

    public void disable(DisableLog dl) throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.LINUX_SERVER_ACCOUNTS, dl.pkey, pkey);
    }
    
    public void enable() throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.LINUX_SERVER_ACCOUNTS, pkey);
    }

    public String getAutoresponderContent() throws IOException, SQLException {
        String content=getService().getConnector().requestStringQuery(true, AOServProtocol.CommandID.GET_AUTORESPONDER_CONTENT, pkey);
        if(content.length()==0) return null;
        return content;
    }

    public String getCronTable() throws IOException, SQLException {
        return getService().getConnector().requestStringQuery(true, AOServProtocol.CommandID.GET_CRON_TABLE, pkey);
    }

    public InboxAttributes getInboxAttributes() throws IOException, SQLException {
        return getService().getConnector().requestResult(
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
                            attr=new InboxAttributes(getService().getConnector(), LinuxServerAccount.this);
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
            getService().getConnector().requestUpdate(
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

    public static String getDefaultHomeDirectory(String username) {
        String check = Username.checkUsername(username);
        if(check!=null) throw new IllegalArgumentException(check);
        return "/home/"+username.charAt(0)+'/'+username;
    }

    public int isProcmailManual() throws IOException, SQLException {
        return getService().getConnector().requestIntQuery(true, AOServProtocol.CommandID.IS_LINUX_SERVER_ACCOUNT_PROCMAIL_MANUAL, pkey);
    }

    public int arePasswordsSet() throws IOException, SQLException {
        return getService().getConnector().requestBooleanQuery(true, AOServProtocol.CommandID.IS_LINUX_SERVER_ACCOUNT_PASSWORD_SET, pkey)?PasswordProtected.ALL:PasswordProtected.NONE;
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() throws SQLException, IOException {
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
        for(PrivateFtpServer pfs : ao.getPrivateFtpServers()) {
            if(pfs.pub_linux_server_account==pkey) {
                LinuxServerAccount lsa = pfs.getLinuxServerAccount();
                reasons.add(new CannotRemoveReason<PrivateFtpServer>("Used by private FTP server "+lsa.getHome()+" on "+lsa.getAOServer().getHostname(), pfs));
            }
        }

        // No httpd_sites
        for(HttpdSite site : ao.getHttpdSites()) {
            if(site.linux_server_account==pkey) reasons.add(new CannotRemoveReason<HttpdSite>("Used by website "+site.getInstallDirectory()+" on "+site.getAOServer().getHostname(), site));
        }

        return reasons;
    }

    public void remove() throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.LINUX_SERVER_ACCOUNTS,
            pkey
        );
    }

    public void setImapFolderSubscribed(String folder, boolean subscribed) throws IOException, SQLException {
        getService().getConnector().requestUpdate(true, AOServProtocol.CommandID.SET_IMAP_FOLDER_SUBSCRIBED, pkey, folder, subscribed);
    }

    public void setCronTable(String cronTable) throws IOException, SQLException {
        getService().getConnector().requestUpdate(true, AOServProtocol.CommandID.SET_CRON_TABLE, pkey, cronTable);
    }

    public void setPassword(String password) throws IOException, SQLException {
        AOServConnector connector=getService().getConnector();
        if(!connector.isSecure()) throw new IOException("Passwords for linux accounts may only be set when using secure protocols.  Currently using the "+connector.getProtocol()+" protocol, which is not secure.");
        connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_PASSWORD, pkey, password);
    }

    public void setAutoresponder(
        final LinuxAccAddress from,
        final String subject,
        final String content,
        final boolean enabled
    ) throws IOException, SQLException {
        getService().getConnector().requestUpdate(
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
                    getService().getConnector().tablesUpdated(invalidateList);
                }
            }
        );
    }

    public void setTrashEmailRetention(int days) throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_TRASH_EMAIL_RETENTION, pkey, days);
    }

    public void setJunkEmailRetention(int days) throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_JUNK_EMAIL_RETENTION, pkey, days);
    }

    public void setEmailSpamAssassinIntegrationMode(EmailSpamAssassinIntegrationMode mode) throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_EMAIL_SPAMASSASSIN_INTEGRATION_MODE, pkey, mode.pkey);
    }

    public void setSpamAssassinRequiredScore(float required_score) throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_REQUIRED_SCORE, pkey, required_score);
    }

    public void setSpamAssassinDiscardScore(int discard_score) throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_DISCARD_SCORE, pkey, discard_score);
    }

    public void setUseInbox(boolean useInbox) throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_USE_INBOX, pkey, useInbox);
    }

    public void setPredisablePassword(final String password) throws IOException, SQLException {
        getService().getConnector().requestUpdate(
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
                    getService().getConnector().tablesUpdated(invalidateList);
                }
            }
        );
    }

    public boolean canSetPassword() throws IOException, SQLException {
        return disable_log==-1 && getLinuxAccount().canSetPassword();
    }

    public boolean passwordMatches(String password) throws IOException, SQLException {
        return getService().getConnector().requestBooleanQuery(true, AOServProtocol.CommandID.COMPARE_LINUX_SERVER_ACCOUNT_PASSWORD, pkey, password);
    }

    public int addEmailAddress(EmailAddress address) throws IOException, SQLException {
        return getService().getConnector().getLinuxAccAddresses().addLinuxAccAddress(address, this);
    }
    */
    // </editor-fold>
}