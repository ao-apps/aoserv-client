/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.command.SetLinuxAccountPredisablePasswordCommand;
import com.aoindustries.aoserv.client.validator.Gecos;
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.aoserv.client.validator.LinuxID;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

/**
 * One user may have shell, FTP, and/or email access to any number
 * of servers.  However, some of the information is common across
 * all machines, and that set of information is contained in a
 * <code>LinuxAccount</code>.
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxAccount extends AOServObjectIntegerKey<LinuxAccount> implements BeanFactory<com.aoindustries.aoserv.client.beans.LinuxAccount> /* PasswordProtected, Removable, Disablable*/ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * Some commonly used system and application account usernames.
     */
    public static final UserId
        APACHE,
        AWSTATS,
        BIN,
        CYRUS,
        EMAILMON,
        FTP,
        FTPMON,
        MAIL,
        NOBODY,
        OPERATOR,
        POSTGRES,
        ROOT
    ;
    static {
        try {
            APACHE=UserId.valueOf("apache").intern();
            AWSTATS=UserId.valueOf("awstats").intern();
            BIN=UserId.valueOf("bin").intern();
            CYRUS=UserId.valueOf("cyrus").intern();
            EMAILMON=UserId.valueOf("emailmon").intern();
            FTP=UserId.valueOf("ftp").intern();
            FTPMON=UserId.valueOf("ftpmon").intern();
            MAIL=UserId.valueOf("mail").intern();
            NOBODY=UserId.valueOf("nobody").intern();
            OPERATOR=UserId.valueOf("operator").intern();
            POSTGRES=UserId.valueOf("postgres").intern();
            ROOT=UserId.valueOf("root").intern();
        } catch(ValidationException err) {
            throw new AssertionError(err.getMessage());
        }
    }

    /**
     * The value used in <code>/etc/shadow</code> when no password is set.
     */
    public static final String NO_PASSWORD_CONFIG_VALUE="!!";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private String linuxAccountType;
    final private UserId username;
    final private LinuxID uid;
    final private UnixPath home;
    final private Gecos name;
    final private Gecos officeLocation;
    final private Gecos officePhone;
    final private Gecos homePhone;
    final private UnixPath shell;
    final private String predisablePassword;

    public LinuxAccount(
        LinuxAccountService<?,?> service,
        int aoServerResource,
        String linuxAccountType,
        UserId username,
        LinuxID uid,
        UnixPath home,
        Gecos name,
        Gecos officeLocation,
        Gecos officePhone,
        Gecos homePhone,
        UnixPath shell,
        String predisablePassword
    ) {
        super(service, aoServerResource);
        this.linuxAccountType = linuxAccountType.intern();
        this.username = username.intern();
        this.uid = uid;
        this.home = home;
        this.name = name;
        this.officeLocation = officeLocation;
        this.officePhone = officePhone;
        this.homePhone = homePhone;
        this.shell = shell.intern();
        this.predisablePassword = predisablePassword;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(LinuxAccount other) throws RemoteException {
        if(key==other.key) return 0;
        int diff = username.equals(other.username) ? 0 : getUsername().compareTo(other.getUsername());
        if(diff!=0) return diff;
        AOServerResource aor1 = getAoServerResource();
        AOServerResource aor2 = other.getAoServerResource();
        return aor1.aoServer==aor2.aoServer ? 0 : aor1.getAoServer().compareTo(aor2.getAoServer());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    static final String COLUMN_AO_SERVER_RESOURCE = "ao_server_resource";
    @SchemaColumn(order=0, name=COLUMN_AO_SERVER_RESOURCE, index=IndexType.PRIMARY_KEY, description="the unique resource id")
    public AOServerResource getAoServerResource() throws RemoteException {
        return getService().getConnector().getAoServerResources().get(key);
    }

    static final String COLUMN_LINUX_ACCOUNT_TYPE = "linux_account_type";
    @SchemaColumn(order=1, name=COLUMN_LINUX_ACCOUNT_TYPE, index=IndexType.INDEXED, description="the type of account")
    public LinuxAccountType getLinuxAccountType() throws RemoteException {
        return getService().getConnector().getLinuxAccountTypes().get(linuxAccountType);
    }

    static final String COLUMN_USERNAME = "username";
    @SchemaColumn(order=2, name=COLUMN_USERNAME, index=IndexType.INDEXED, description="the username of the user")
    public Username getUsername() throws RemoteException {
        return getService().getConnector().getUsernames().get(username);
    }

    static final String COLUMN_UID = "uid";
    @SchemaColumn(order=3, name=COLUMN_UID, index=IndexType.INDEXED, description="the uid of the user on the machine")
    public LinuxID getUid() {
        return uid;
    }

    @SchemaColumn(order=4, name="home", description="the home directory of the user on this machine")
    public UnixPath getHome() {
        return home;
    }

    @SchemaColumn(order=5, name="name", description="the full name of the user")
    public Gecos getName() {
        return name;
    }

    @SchemaColumn(order=6, name="office_location", description="the location of the user")
    public Gecos getOfficeLocation() {
        return officeLocation;
    }

    @SchemaColumn(order=7, name="office_phone", description="the work phone number of the user")
    public Gecos getOfficePhone() {
        return officePhone;
    }

    @SchemaColumn(order=8, name="home_phone", description="the home phone number of the user")
    public Gecos getHomePhone() {
        return homePhone;
    }

    static final String COLUMN_SHELL = "shell";
    @SchemaColumn(order=9, name=COLUMN_SHELL, index=IndexType.INDEXED, description="the users shell preference")
    public Shell getShell() throws RemoteException {
        return getService().getConnector().getShells().get(shell);
    }

    @SchemaColumn(order=10, name="predisable_password", description="stores the password that was used before the account was disabled")
    public String getPredisablePassword() {
        return predisablePassword;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.LinuxAccount getBean() {
        return new com.aoindustries.aoserv.client.beans.LinuxAccount(
            key,
            linuxAccountType,
            username.getBean(),
            uid.getBean(),
            home.getBean(),
            name==null ? null : name.getBean(),
            officeLocation==null ? null : officeLocation.getBean(),
            officePhone==null ? null : officePhone.getBean(),
            homePhone==null ? null : homePhone.getBean(),
            shell.getBean(),
            predisablePassword
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getAoServerResource(),
            getLinuxAccountType(),
            getUsername(),
            getShell()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            AOServObjectUtils.createDependencySet(
                getFtpGuestUser(),
                getEmailInbox()
            ),
            getLinuxAccountGroups()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) throws RemoteException {
        return ApplicationResources.accessor.getMessage(userLocale, "LinuxAccount.toString", username, getAoServerResource().getAoServer().getHostname());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public EmailInbox getEmailInbox() throws RemoteException {
        if(!linuxAccountType.equals(ResourceType.EMAIL_INBOX) && !linuxAccountType.equals(ResourceType.SHELL_ACCOUNT)) return null;
        return getService().getConnector().getEmailInboxes().get(key);
    }

    public FtpGuestUser getFtpGuestUser() throws RemoteException {
        return getService().getConnector().getFtpGuestUsers().filterUnique(FtpGuestUser.COLUMN_LINUX_ACCOUNT, this);
    }

    public IndexedSet<LinuxAccountGroup> getLinuxAccountGroups() throws RemoteException {
        return getService().getConnector().getLinuxAccountGroups().filterIndexed(LinuxAccountGroup.COLUMN_LINUX_ACCOUNT, this);
    }

    public LinuxGroup getPrimaryLinuxGroup() throws RemoteException {
        return getLinuxAccountGroups().filterUnique(LinuxAccountGroup.COLUMN_IS_PRIMARY, true).getLinuxGroup();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Commands">
    public void setPredisablePassword(String password) throws RemoteException {
        new SetLinuxAccountPredisablePasswordCommand(key, password).execute(getService().getConnector());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public void addFtpGuestUser() throws IOException, SQLException {
        getService().getConnector().getFtpGuestUsers().addFtpGuestUser(pkey);
    }

    public void addLinuxGroup(LinuxGroup group) throws IOException, SQLException {
        getService().getConnector().getLinuxGroupAccounts().addLinuxGroupAccount(group, this);
    }

    public int addLinuxServerAccount(AOServer aoServer, String home) throws IOException, SQLException {
        return getService().getConnector().getLinuxServerAccounts().addLinuxServerAccount(this, aoServer, home);
    }

    public int arePasswordsSet() throws IOException, SQLException {
        return Username.groupPasswordsSet(getLinuxServerAccounts());
    }

    public boolean canDisable() throws IOException, SQLException {
        // Already disabled
        if(disable_log!=-1) return false;

        // linux_server_accounts
        for(LinuxServerAccount lsa : getLinuxServerAccounts()) if(lsa.disable_log==-1) return false;

        return true;
    }

    public boolean canEnable() throws SQLException, IOException {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getUsername().disable_log==-1;
    }

    public PasswordChecker.Result[] checkPassword(Locale userLocale, String password) throws IOException {
        return checkPassword(userLocale, pkey, type, password);
    }
    */
    /**
     * Checks the strength of a password as required for this
     * <code>LinuxAccount</code>.  The strength requirement
     * depends on the <code>LinuxAccountType</code>.
     *
     * @see  LinuxAccountType#enforceStrongPassword(String)
     * @see  PasswordChecker#checkPassword(Locale,String,String,boolean,boolean)
     */
    /* TODO
    public static PasswordChecker.Result[] checkPassword(Locale userLocale, String username, String type, String password) throws IOException {
        boolean enforceStrong=LinuxAccountType.enforceStrongPassword(type);
        return PasswordChecker.checkPassword(userLocale, username, password, enforceStrong, !enforceStrong);
    }

    public void disable(DisableLog dl) throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.LINUX_ACCOUNTS, dl.pkey, pkey);
    }
    
    public void enable() throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.LINUX_ACCOUNTS, pkey);
    }

    public List<LinuxGroup> getLinuxGroups() throws IOException, SQLException {
        return getService().getConnector().getLinuxGroupAccounts().getLinuxGroups(this);
    }

    public LinuxGroup getPrimaryGroup() throws IOException, SQLException {
        return getService().getConnector().getLinuxGroupAccounts().getPrimaryGroup(this);
    }

    /**
     * @deprecated  Please provide the locale for locale-specific errors.
     */
    /* TODO
    public List<String> getValidHomeDirectories(AOServer ao) throws SQLException, IOException {
        return getValidHomeDirectories(pkey, ao);
    }

    public List<String> getValidHomeDirectories(AOServer ao, Locale locale) throws SQLException, IOException {
        return getValidHomeDirectories(pkey, ao, locale);
    }
    */
    /**
     * @deprecated  Please provide the locale for locale-specific errors.
     */
    /* TODO
    public static List<String> getValidHomeDirectories(String username, AOServer ao) throws SQLException, IOException {
        return getValidHomeDirectories(username, ao, Locale.getDefault());
    }

    public static List<String> getValidHomeDirectories(String username, AOServer ao, Locale locale) throws SQLException, IOException {
        List<String> dirs=new ArrayList<String>();
        if(username!=null) dirs.add(LinuxServerAccount.getDefaultHomeDirectory(username, locale));

        List<HttpdSite> hss=ao.getHttpdSites();
        int hsslen=hss.size();
        for(int c=0;c<hsslen;c++) {
            HttpdSite hs=hss.get(c);
            String siteDir=hs.getInstallDirectory();
            dirs.add(siteDir);
            if(hs.getHttpdTomcatSite()!=null) dirs.add(siteDir+"/webapps");
        }

        List<HttpdSharedTomcat> hsts=ao.getHttpdSharedTomcats();
        int hstslen=hsts.size();
        for(int c=0;c<hstslen;c++) {
            HttpdSharedTomcat hst=hsts.get(c);
            dirs.add(hst.getAOServer().getServer().getOperatingSystemVersion().getHttpdSharedTomcatsDirectory()+'/'+hst.getName());
        }
        return dirs;
    }

    public List<CannotRemoveReason> getCannotRemoveReasons(Locale userLocale) throws SQLException, IOException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        // All LinuxServerAccounts must be removable
        for(LinuxServerAccount lsa : getLinuxServerAccounts()) {
            reasons.addAll(lsa.getCannotRemoveReasons(userLocale));
        }

        return reasons;
    }

    public void remove() throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.LINUX_ACCOUNTS,
            pkey
        );
    }

    public void removeLinuxGroup(LinuxGroup group) throws IOException, SQLException {
        getService().getConnector().getLinuxGroupAccounts().getLinuxGroupAccount(group.pkey, pkey).remove();
    }

    public void setHomePhone(String phone) throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_ACCOUNT_HOME_PHONE, pkey, phone==null?"":phone);
    }

    public void setName(String name) throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_ACCOUNT_NAME, pkey, name);
    }

    public void setOfficeLocation(String location) throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_ACCOUNT_OFFICE_LOCATION, pkey, location==null?"":location);
    }

    public void setOfficePhone(String phone) throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_ACCOUNT_OFFICE_PHONE, pkey, phone==null?"":phone);
    }

    public void setPassword(String password) throws SQLException, IOException {
        for(LinuxServerAccount lsa : getLinuxServerAccounts()) {
            if(lsa.canSetPassword()) lsa.setPassword(password);
        }
    }

    public void setShell(Shell shell) throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_ACCOUNT_SHELL, pkey, shell.pkey);
    }

    public boolean canSetPassword() throws IOException, SQLException {
        return disable_log==-1 && getType().canSetPassword();
    }

    public void setPrimaryLinuxGroup(LinuxGroup group) throws SQLException, IOException {
        getService().getConnector().getLinuxGroupAccounts().getLinuxGroupAccount(group.getName(), pkey).setAsPrimary();
    }
    */
    // </editor-fold>
}
