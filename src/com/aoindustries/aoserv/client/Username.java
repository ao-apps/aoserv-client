package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * Each <code>Username</code> is unique across all systems and must
 * be allocated to a <code>Business</code> before use in any of the
 * account types.
 *
 * @see  BusinessAdministrator
 * @see  LinuxAccount
 * @see  MySQLUser
 * @see  PostgresUser
 *
 * @author  AO Industries, Inc.
 */
final public class Username extends AOServObjectUserIdKey<Username> implements BeanFactory<com.aoindustries.aoserv.client.beans.Username> /* TODO: implements PasswordProtected, Removable, Disablable*/ {
	
    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final AccountingCode accounting;
    final Integer disableLog;

    public Username(UsernameService<?,?> table, UserId username, AccountingCode accounting, Integer disableLog) {
        super(table, username);
        this.accounting = accounting.intern();
        this.disableLog = disableLog;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="username", index=IndexType.PRIMARY_KEY, description="the unique username")
    public UserId getUsername() {
        return key;
    }

    static final String COLUMN_ACCOUNTING = "accounting";
    @SchemaColumn(order=1, name=COLUMN_ACCOUNTING, index=IndexType.INDEXED, description="the business that this user is part of")
    public Business getBusiness() throws RemoteException {
    	return getService().getConnector().getBusinesses().get(accounting);
    }

    static final String COLUMN_DISABLE_LOG = "disable_log";
    @SchemaColumn(order=2, name=COLUMN_DISABLE_LOG, index=IndexType.INDEXED, description="indicates that the username is disabled")
    public DisableLog getDisableLog() throws RemoteException {
        if(disableLog==null) return null;
        return getService().getConnector().getDisableLogs().get(disableLog);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.Username getBean() {
        return new com.aoindustries.aoserv.client.beans.Username(key.getBean(), accounting.getBean(), disableLog);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getBusiness(),
            getDisableLog()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            AOServObjectUtils.createDependencySet(
                getBusinessAdministrator()
            ),
            getLinuxAccounts(),
            getMysqlUsers(),
            getPostgresUsers()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public BusinessAdministrator getBusinessAdministrator() throws RemoteException {
    	return getService().getConnector().getBusinessAdministrators().filterUnique(BusinessAdministrator.COLUMN_USERNAME, this);
    }

    public IndexedSet<LinuxAccount> getLinuxAccounts() throws RemoteException {
        return getService().getConnector().getLinuxAccounts().filterIndexed(LinuxAccount.COLUMN_USERNAME, this);
    }

    public IndexedSet<MySQLUser> getMysqlUsers() throws RemoteException {
        return getService().getConnector().getMysqlUsers().filterIndexed(MySQLUser.COLUMN_USERNAME, this);
    }

    public IndexedSet<PostgresUser> getPostgresUsers() throws RemoteException {
        return getService().getConnector().getPostgresUsers().filterIndexed(PostgresUser.COLUMN_USERNAME, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /*
    public void addBusinessAdministrator(
        String name,
        String title,
        long birthday,
        boolean isPrivate,
        String workPhone,
        String homePhone,
        String cellPhone,
        String fax,
        String email,
        String address1,
        String address2,
        String city,
        String state,
        String country,
        String zip,
        boolean enableEmailSupport
    ) throws IOException, SQLException {
	    getService().getConnector().getBusinessAdministrators().addBusinessAdministrator(
            this,
            name,
            title,
            birthday,
            isPrivate,
            workPhone,
            homePhone,
            cellPhone,
            fax,
            email,
            address1,
            address2,
            city,
            state,
            country,
            zip,
            enableEmailSupport
    	);
    }

    public void addLinuxAccount(
        LinuxGroup primaryGroup,
        String name,
        String office_location,
        String office_phone,
        String home_phone,
        LinuxAccountType typeObject,
        Shell shellObject
    ) throws IOException, SQLException {
        addLinuxAccount(primaryGroup.getName(), name, office_location, office_phone, home_phone, typeObject.pkey, shellObject.pkey);
    }

    public void addLinuxAccount(
        String primaryGroup,
        String name,
        String office_location,
        String office_phone,
        String home_phone,
        String type,
        String shell
    ) throws IOException, SQLException {
	    getService().getConnector().getLinuxAccounts().addLinuxAccount(
            this,
            primaryGroup,
            name,
            office_location,
            office_phone,
            home_phone,
            type,
            shell
    	);
    }

    public int addMySQLUser(MySQLServer mysqlServer, String host) throws IOException, SQLException {
    	return getService().getConnector().getMysqlUsers().addMySQLUser(pkey, mysqlServer, host);
    }

    public void addPostgresUser() throws IOException, SQLException {
        getService().getConnector().getPostgresUsers().addPostgresUser(pkey);
    }

    public int arePasswordsSet() throws IOException, SQLException {
        // Build the array of objects
        List<PasswordProtected> pps=new ArrayList<PasswordProtected>();
        BusinessAdministrator ba=getBusinessAdministrator();
        if(ba!=null) pps.add(ba);
        LinuxAccount la=getLinuxAccount();
        if(la!=null) pps.add(la);
        pps.addAll(getMySQLUsers());
        PostgresUser pu=getPostgresUser();
        if(pu!=null) pps.add(pu);
        return Username.groupPasswordsSet(pps);
    }

    public boolean canDisable() throws IOException, SQLException {
        if(disableLog!=null) return false;
        LinuxAccount la=getLinuxAccount();
        if(la!=null && la.disableLog==null) return false;
        for(MySQLUser mu : getMySQLUsers()) if(mu.disableLog==null) return false;
        PostgresUser pu=getPostgresUser();
        if(pu!=null && pu.disableLog==null) return false;
        return true;
    }
    
    public boolean canEnable() throws SQLException, IOException {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getBusiness().disableLog==null;
    }
    */
    /**
     * Checks the strength of a password as used by this <code>Username</code>.
     */
    /*
    public PasswordChecker.Result[] checkPassword(Locale userLocale, String password) throws IOException, SQLException {
        BusinessAdministrator ba=getBusinessAdministrator();
        if(ba!=null) {
            PasswordChecker.Result[] results=ba.checkPassword(userLocale, password);
            if(PasswordChecker.hasResults(userLocale, results)) return results;
    	}

        LinuxAccount la=getLinuxAccount();
    	if(la!=null) {
            PasswordChecker.Result[] results=la.checkPassword(userLocale, password);
            if(PasswordChecker.hasResults(userLocale, results)) return results;
    	}

        for(MySQLUser mu : getMySQLUsers()) {
            PasswordChecker.Result[] results=mu.checkPassword(userLocale, password);
            if(PasswordChecker.hasResults(userLocale, results)) return results;
    	}

        PostgresUser pu=getPostgresUser();
        if(pu!=null) {
            PasswordChecker.Result[] results=pu.checkPassword(userLocale, password);
            if(PasswordChecker.hasResults(userLocale, results)) return results;
    	}

        return PasswordChecker.getAllGoodResults(userLocale);
    }

    public void disable(DisableLog dl) throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.USERNAMES, dl.pkey, pkey);
    }
    
    public void enable() throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.USERNAMES, pkey);
    }

    public boolean isDisabled() {
        return disableLog!=null;
    }

    static int groupPasswordsSet(List<? extends PasswordProtected> pps) throws IOException, SQLException {
        int totalAll=0;
    	for(int c=0;c<pps.size();c++) {
            int result=pps.get(c).arePasswordsSet();
            if(result==PasswordProtected.SOME) return PasswordProtected.SOME;
            if(result==PasswordProtected.ALL) totalAll++;
        }
        return totalAll==pps.size()?PasswordProtected.ALL:totalAll==0?PasswordProtected.NONE:PasswordProtected.SOME;
    }

    public boolean isUsed() throws IOException, SQLException {
    	return
            getLinuxAccount()!=null
            || getBusinessAdministrator()!=null
            || !getMySQLUsers().isEmpty()
            || getPostgresUser()!=null
    	;
    }

    public List<CannotRemoveReason> getCannotRemoveReasons(Locale userLocale) throws SQLException, IOException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        LinuxAccount la=getLinuxAccount();
        if(la!=null) reasons.add(new CannotRemoveReason<LinuxAccount>("Used by Linux account: "+la.getUsername().getUsername(), la));
        BusinessAdministrator ba=getBusinessAdministrator();
        if(ba!=null) reasons.add(new CannotRemoveReason<BusinessAdministrator>("Used by Business Administrator: "+ba.getUsername().getUsername(), ba));
        List<MySQLUser> mus=getMySQLUsers();
        if(!mus.isEmpty()) reasons.add(new CannotRemoveReason<MySQLUser>("Used by MySQL users: "+pkey, mus));
        PostgresUser pu=getPostgresUser();
        if(pu!=null) reasons.add(new CannotRemoveReason<PostgresUser>("Used by PostgreSQL user: "+pu.getUsername().getUsername(), pu));

        return reasons;
    }

    public void remove() throws IOException, SQLException {
    	getService().getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.USERNAMES,
            pkey
    	);
    }

    public void setPassword(String password) throws SQLException, IOException {
        BusinessAdministrator ba=getBusinessAdministrator();
        if(ba!=null) ba.setPassword(password);

        LinuxAccount la=getLinuxAccount();
    	if(la!=null) la.setPassword(password);

        for(MySQLUser mu : getMySQLUsers()) mu.setPassword(password);

        PostgresUser pu=getPostgresUser();
        if(pu!=null) pu.setPassword(password);
    }

    public boolean canSetPassword() throws IOException, SQLException {
        if(disableLog!=null) return false;

        BusinessAdministrator ba=getBusinessAdministrator();
    	if(ba!=null && !ba.canSetPassword()) return false;

        LinuxAccount la=getLinuxAccount();
    	if(la!=null && !la.canSetPassword()) return false;

        List<MySQLUser> mus = getMySQLUsers();
    	for(MySQLUser mu : mus) if(!mu.canSetPassword()) return false;

        PostgresUser pu=getPostgresUser();
        if(pu!=null && !pu.canSetPassword()) return false;
        
        return ba!=null || la!=null || !mus.isEmpty() || pu!=null;
    }
     */
    // </editor-fold>
}