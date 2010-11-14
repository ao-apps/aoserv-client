/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.command.AOServCommand;
import com.aoindustries.aoserv.client.command.CheckLinuxAccountPasswordCommand;
import com.aoindustries.aoserv.client.command.SetLinuxAccountPasswordCommand;
import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;
import java.util.List;

/**
 * One user may have shell, FTP, and/or email access to any number
 * of servers.  However, some of the information is common across
 * all machines, and that set of information is contained in a
 * <code>LinuxAccount</code>.
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxAccount extends AOServerResource implements Comparable<LinuxAccount>, DtoFactory<com.aoindustries.aoserv.client.dto.LinuxAccount>, PasswordProtected /* TODO , Removable, Disablable*/ {

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
    private String linuxAccountType;
    private UserId username;
    final private LinuxID uid;
    private UnixPath home;
    private Gecos name;
    private Gecos officeLocation;
    private Gecos officePhone;
    private Gecos homePhone;
    private UnixPath shell;
    private String predisablePassword;

    public LinuxAccount(
        AOServConnector connector,
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        int aoServer,
        int businessServer,
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
        super(connector, pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, aoServer, businessServer);
        this.linuxAccountType = linuxAccountType;
        this.username = username;
        this.uid = uid;
        this.home = home;
        this.name = name;
        this.officeLocation = officeLocation;
        this.officePhone = officePhone;
        this.homePhone = homePhone;
        this.shell = shell;
        this.predisablePassword = predisablePassword;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        linuxAccountType = intern(linuxAccountType);
        username = intern(username);
        home = intern(home);
        name = intern(name);
        officeLocation = intern(officeLocation);
        officePhone = intern(officePhone);
        homePhone = intern(homePhone);
        shell = intern(shell);
        predisablePassword = intern(predisablePassword);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(LinuxAccount other) {
        try {
            if(key==other.key) return 0;
            int diff = username==other.username ? 0 : getUsername().compareTo(other.getUsername()); // OK - interned
            if(diff!=0) return diff;
            return aoServer==other.aoServer ? 0 : getAoServer().compareTo(other.getAoServer());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    static final String COLUMN_LINUX_ACCOUNT_TYPE = "linux_account_type";
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+1, name=COLUMN_LINUX_ACCOUNT_TYPE, index=IndexType.INDEXED, description="the type of account")
    public LinuxAccountType getLinuxAccountType() throws RemoteException {
        return getConnector().getLinuxAccountTypes().get(linuxAccountType);
    }

    static final String COLUMN_USERNAME = "username";
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+2, name=COLUMN_USERNAME, index=IndexType.INDEXED, description="the username of the user")
    public Username getUsername() throws RemoteException {
        return getConnector().getUsernames().get(username);
    }
    public UserId getUserId() {
        return username;
    }

    static final String COLUMN_UID = "uid";
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+3, name=COLUMN_UID, index=IndexType.INDEXED, description="the uid of the user on the machine")
    public LinuxID getUid() {
        return uid;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+4, name="home", description="the home directory of the user on this machine")
    public UnixPath getHome() {
        return home;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+5, name="name", description="the full name of the user")
    public Gecos getName() {
        return name;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+6, name="office_location", description="the location of the user")
    public Gecos getOfficeLocation() {
        return officeLocation;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+7, name="office_phone", description="the work phone number of the user")
    public Gecos getOfficePhone() {
        return officePhone;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+8, name="home_phone", description="the home phone number of the user")
    public Gecos getHomePhone() {
        return homePhone;
    }

    static final String COLUMN_SHELL = "shell";
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+9, name=COLUMN_SHELL, index=IndexType.INDEXED, description="the users shell preference")
    public Shell getShell() throws RemoteException {
        return getConnector().getShells().get(shell);
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+10, name="predisable_password", description="stores the password that was used before the account was disabled")
    public String getPredisablePassword() {
        return predisablePassword;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public LinuxAccount(AOServConnector connector, com.aoindustries.aoserv.client.dto.LinuxAccount dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            dto.getResourceType(),
            getAccountingCode(dto.getAccounting()),
            getTimeMillis(dto.getCreated()),
            getUserId(dto.getCreatedBy()),
            dto.getDisableLog(),
            getTimeMillis(dto.getLastEnabled()),
            dto.getAoServer(),
            dto.getBusinessServer(),
            dto.getLinuxAccountType(),
            getUserId(dto.getUsername()),
            getLinuxID(dto.getUid()),
            getUnixPath(dto.getHome()),
            getGecos(dto.getName()),
            getGecos(dto.getOfficeLocation()),
            getGecos(dto.getOfficePhone()),
            getGecos(dto.getHomePhone()),
            getUnixPath(dto.getShell()),
            dto.getPredisablePassword()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.LinuxAccount getDto() {
        return new com.aoindustries.aoserv.client.dto.LinuxAccount(
            key,
            getResourceTypeName(),
            getDto(getAccounting()),
            created,
            getDto(getCreatedByUsername()),
            disableLog,
            lastEnabled,
            aoServer,
            businessServer,
            linuxAccountType,
            getDto(username),
            getDto(uid),
            getDto(home),
            getDto(name),
            getDto(officeLocation),
            getDto(officePhone),
            getDto(homePhone),
            getDto(shell),
            predisablePassword
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependencies(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getLinuxAccountType());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getUsername());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getShell());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependentObjects(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getFtpGuestUser());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getEmailInbox());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getLinuxAccountGroups());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        return username+"@"+getAoServer().toStringImpl();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public EmailInbox getEmailInbox() throws RemoteException {
        if(linuxAccountType!=ResourceType.EMAIL_INBOX && linuxAccountType!=ResourceType.SHELL_ACCOUNT) return null; // OK - interned
        return getConnector().getEmailInboxes().get(key);
    }

    public FtpGuestUser getFtpGuestUser() throws RemoteException {
        return getConnector().getFtpGuestUsers().filterUnique(FtpGuestUser.COLUMN_LINUX_ACCOUNT, this);
    }

    public IndexedSet<LinuxAccountGroup> getLinuxAccountGroups() throws RemoteException {
        return getConnector().getLinuxAccountGroups().filterIndexed(LinuxAccountGroup.COLUMN_LINUX_ACCOUNT, this);
    }

    public LinuxGroup getPrimaryLinuxGroup() throws RemoteException {
        return getLinuxAccountGroups().filterUnique(LinuxAccountGroup.COLUMN_IS_PRIMARY, true).getLinuxGroup();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Password Protected">
    @Override
    public AOServCommand<List<PasswordChecker.Result>> getCheckPasswordCommand(String password) {
        return new CheckLinuxAccountPasswordCommand(this, password);
    }

    @Override
    public AOServCommand<Void> getSetPasswordCommand(String plaintext) {
        return new SetLinuxAccountPasswordCommand(this, plaintext);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public void addFtpGuestUser() throws IOException, SQLException {
        getConnector().getFtpGuestUsers().addFtpGuestUser(pkey);
    }

    public void addLinuxGroup(LinuxGroup group) throws IOException, SQLException {
        getConnector().getLinuxGroupAccounts().addLinuxGroupAccount(group, this);
    }

    public int addLinuxServerAccount(AOServer aoServer, String home) throws IOException, SQLException {
        return getConnector().getLinuxServerAccounts().addLinuxServerAccount(this, aoServer, home);
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

    public void disable(DisableLog dl) throws IOException, SQLException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.LINUX_ACCOUNTS, dl.pkey, pkey);
    }
    
    public void enable() throws IOException, SQLException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.LINUX_ACCOUNTS, pkey);
    }

    public List<LinuxGroup> getLinuxGroups() throws IOException, SQLException {
        return getConnector().getLinuxGroupAccounts().getLinuxGroups(this);
    }

    public LinuxGroup getPrimaryGroup() throws IOException, SQLException {
        return getConnector().getLinuxGroupAccounts().getPrimaryGroup(this);
    }

    /* TODO
    public List<String> getValidHomeDirectories(AOServer ao) throws SQLException, IOException {
        return getValidHomeDirectories(pkey, ao);
    }

    public static List<String> getValidHomeDirectories(String username, AOServer ao) throws SQLException, IOException {
        List<String> dirs=new ArrayList<String>();
        if(username!=null) dirs.add(LinuxServerAccount.getDefaultHomeDirectory(username));

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

    public List<CannotRemoveReason> getCannotRemoveReasons() throws SQLException, IOException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        // All LinuxServerAccounts must be removable
        for(LinuxServerAccount lsa : getLinuxServerAccounts()) {
            reasons.addAll(lsa.getCannotRemoveReasons());
        }

        return reasons;
    }

    public void remove() throws IOException, SQLException {
        getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.LINUX_ACCOUNTS,
            pkey
        );
    }

    public void removeLinuxGroup(LinuxGroup group) throws IOException, SQLException {
        getConnector().getLinuxGroupAccounts().getLinuxGroupAccount(group.pkey, pkey).remove();
    }

    public void setHomePhone(String phone) throws IOException, SQLException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_ACCOUNT_HOME_PHONE, pkey, phone==null?"":phone);
    }

    public void setName(String name) throws IOException, SQLException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_ACCOUNT_NAME, pkey, name);
    }

    public void setOfficeLocation(String location) throws IOException, SQLException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_ACCOUNT_OFFICE_LOCATION, pkey, location==null?"":location);
    }

    public void setOfficePhone(String phone) throws IOException, SQLException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_ACCOUNT_OFFICE_PHONE, pkey, phone==null?"":phone);
    }

    public void setPassword(String password) throws SQLException, IOException {
        for(LinuxServerAccount lsa : getLinuxServerAccounts()) {
            if(lsa.canSetPassword()) lsa.setPassword(password);
        }
    }

    public void setShell(Shell shell) throws IOException, SQLException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_ACCOUNT_SHELL, pkey, shell.pkey);
    }

    public void setPrimaryLinuxGroup(LinuxGroup group) throws SQLException, IOException {
        getConnector().getLinuxGroupAccounts().getLinuxGroupAccount(group.getName(), pkey).setAsPrimary();
    }
    */
    // </editor-fold>
}
