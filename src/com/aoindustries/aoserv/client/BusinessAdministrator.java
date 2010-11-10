/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.Email;
import com.aoindustries.aoserv.client.validator.HashedPassword;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.io.IOException;
import java.rmi.RemoteException;
import java.security.Principal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

/**
 * A <code>BusinessAdministrator</code> is a username and password pair, usually
 * representing an individual or an application, that has administrative control
 * over all resources in a <code>Business</code> or any any of its child businesses.
 *
 * @see  Business
 *
 * @author  AO Industries, Inc.
 */
final public class BusinessAdministrator extends AOServObjectUserIdKey<BusinessAdministrator> implements BeanFactory<com.aoindustries.aoserv.client.beans.BusinessAdministrator>, Principal /* TODO: implements PasswordProtected, Removable, Disablable, Comparable<BusinessAdministrator> */ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private HashedPassword password;
    private String fullName;
    private String title;
    final private Date birthday;
    final private boolean isPreferred;
    final private boolean isPrivate;
    final private long created;
    private String workPhone;
    private String homePhone;
    private String cellPhone;
    private String fax;
    private Email email;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String country;
    private String zip;
    final private Integer disableLog;
    final private boolean canSwitchUsers;
    private String supportCode;

    public BusinessAdministrator(
        BusinessAdministratorService<?,?> service,
        UserId username,
        HashedPassword password,
        String fullName,
        String title,
        Date birthday,
        boolean isPreferred,
        boolean isPrivate,
        long created,
        String workPhone,
        String homePhone,
        String cellPhone,
        String fax,
        Email email,
        String address1,
        String address2,
        String city,
        String state,
        String country,
        String zip,
        Integer disableLog,
        boolean canSwitchUsers,
        String supportCode
    ) {
        super(service, username);
        this.password = password;
        this.fullName = fullName;
        this.title = title;
        this.birthday = birthday;
        this.isPreferred = isPreferred;
        this.isPrivate = isPrivate;
        this.created = created;
        this.workPhone = workPhone;
        this.homePhone = homePhone;
        this.cellPhone = cellPhone;
        this.fax = fax;
        this.email = email;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zip = zip;
        this.disableLog = disableLog;
        this.canSwitchUsers = canSwitchUsers;
        this.supportCode = supportCode;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        fullName = intern(fullName);
        title = intern(title);
        workPhone = intern(workPhone);
        homePhone = intern(homePhone);
        cellPhone = intern(cellPhone);
        fax = intern(fax);
        email = intern(email);
        address1 = intern(address1);
        address2 = intern(address2);
        city = intern(city);
        state = intern(state);
        country = intern(country);
        zip = intern(zip);
        supportCode = intern(supportCode);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    static final String COLUMN_USERNAME = "username";
    @SchemaColumn(order=0, name=COLUMN_USERNAME, index=IndexType.PRIMARY_KEY, description="the unique identifier for this admin")
    public Username getUsername() throws RemoteException {
        return getService().getConnector().getUsernames().get(getKey());
    }
    public UserId getUserId() {
        return getKey();
    }

    @SchemaColumn(order=1, name="password", description="the encrypted password for this admin")
    public HashedPassword getPassword() {
    	return password;
    }

    @SchemaColumn(order=2, name="full_name", description="the name of this admin")
    public String getFullName() {
    	return fullName;
    }

    @SchemaColumn(order=3, name="title", description="the admins title within their organization")
    public String getTitle() {
        return title;
    }

    @SchemaColumn(order=4, name="birthday", description="the admins birthday")
    public Date getBirthday() {
    	return birthday;
    }

    @SchemaColumn(order=5, name="is_preferred", description="if true, customers is preferred")
    public boolean isPreferred() {
    	return isPreferred;
    }

    @SchemaColumn(order=6, name="private", description="indicates if the admin should not be listed in publicly available lists")
    public boolean isPrivate() {
        return isPrivate;
    }

    @SchemaColumn(order=7, name="created", description="the time the admin entry was created")
    public Timestamp getCreated() {
    	return new Timestamp(created);
    }

    @SchemaColumn(order=8, name="work_phone", description="the work phone number (if different than business)")
    public String getWorkPhone() {
    	return workPhone;
    }

    @SchemaColumn(order=9, name="home_phone", description="the home phone number")
    public String getHomePhone() {
    	return homePhone;
    }

    @SchemaColumn(order=10, name="cell_phone", description="the cellular phone number")
    public String getCellPhone() {
    	return cellPhone;
    }

    @SchemaColumn(order=11, name="fax", description="the fax number (if different than business)")
    public String getFax() {
    	return fax;
    }

    @SchemaColumn(order=12, name="email", description="the email address")
    public Email getEmail() {
    	return email;
    }

    @SchemaColumn(order=13, name="address1", description="the street address (if different than business)")
    public String getAddress1() {
        return address1;
    }

    @SchemaColumn(order=14, name="address2", description="the street address (if different than business)")
    public String getAddress2() {
    	return address2;
    }

    @SchemaColumn(order=15, name="city", description="the city (if different than business)")
    public String getCity() {
    	return city;
    }

    @SchemaColumn(order=16, name="state", description="the state (if different than business)")
    public String getState() {
    	return state;
    }

    static final String COLUMN_COUNTRY="country";
    @SchemaColumn(order=17, name=COLUMN_COUNTRY, index=IndexType.INDEXED, description="the country (if different than business)")
    public CountryCode getCountry() throws RemoteException {
        if(country == null) return null;
        CountryCode countryCode=getService().getConnector().getCountryCodes().get(country);
        if(countryCode == null) throw new RemoteException("CountryCode not found: " + country);
        return countryCode;
    }

    @SchemaColumn(order=18, name="zip", description="the zip code (if different than business)")
    public String getZIP() {
        return zip;
    }

    static final String COLUMN_DISABLE_LOG = "disable_log";
    @SchemaColumn(order=19, name=COLUMN_DISABLE_LOG, index=IndexType.INDEXED, description="indicates that this account is disabled")
    public DisableLog getDisableLog() throws RemoteException {
        if(disableLog==null) return null;
        return getService().getConnector().getDisableLogs().get(disableLog);
    }

    @SchemaColumn(order=20, name="can_switch_users", description="allows this person to switch users to any subaccounts")
    public boolean canSwitchUsers() {
        return canSwitchUsers;
    }

    @SchemaColumn(order=21, name="support_code", index=IndexType.UNIQUE, description="used to authenticate for email-based supprt")
    public String getSupportCode() {
        return supportCode;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    @Override
    public com.aoindustries.aoserv.client.beans.BusinessAdministrator getBean() {
        return new com.aoindustries.aoserv.client.beans.BusinessAdministrator(getBean(getKey()), getBean(password), fullName, title, birthday, isPreferred, isPrivate, getCreated(), workPhone, homePhone, cellPhone, fax, getBean(email), address1, address2, city, state, country, zip, disableLog, canSwitchUsers, supportCode);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getUsername());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getCountry());
        // Caused loop in dependency DAG: AOServObjectUtils.addDependencySet(unionSet, getDisableLog());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getMasterUser());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getBusinessAdministratorRoles());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getCreditCardsByCreatedBy());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getCreditCardTransactionsByCreditCardCreatedBy());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getCreditCardTransactionsByAuthorizationUsername());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getCreditCardTransactionsByCaptureUsername());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getCreditCardTransactionsByVoidUsername());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getDisableLogs());
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getMonthlyCharges());
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getMonthlyChargesByCreatedBy());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getBusinessesByCreatedBy());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getResources());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTicketsByCreatedBy());
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getCompletedSignupRequests());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTicketActions());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTicketActionsByOldAssignedTo());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTicketActionsByNewAssignedTo());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTicketAssignments());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTransactions());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<Business> getBusinessesByCreatedBy() throws RemoteException {
        return getService().getConnector().getBusinesses().filterIndexed(Business.COLUMN_CREATED_BY, this);
    }

    public IndexedSet<BusinessAdministratorRole> getBusinessAdministratorRoles() throws RemoteException {
        return getService().getConnector().getBusinessAdministratorRoles().filterIndexed(BusinessAdministratorRole.COLUMN_USERNAME, this);
    }

    public IndexedSet<CreditCard> getCreditCardsByCreatedBy() throws RemoteException {
        return getService().getConnector().getCreditCards().filterIndexed(CreditCard.COLUMN_CREATED_BY, this);
    }

    public IndexedSet<CreditCardTransaction> getCreditCardTransactionsByCreditCardCreatedBy() throws RemoteException {
        return getService().getConnector().getCreditCardTransactions().filterIndexed(CreditCardTransaction.COLUMN_CREDIT_CARD_CREATED_BY, this);
    }

    public IndexedSet<CreditCardTransaction> getCreditCardTransactionsByAuthorizationUsername() throws RemoteException {
        return getService().getConnector().getCreditCardTransactions().filterIndexed(CreditCardTransaction.COLUMN_AUTHORIZATION_USERNAME, this);
    }

    public IndexedSet<CreditCardTransaction> getCreditCardTransactionsByCaptureUsername() throws RemoteException {
        return getService().getConnector().getCreditCardTransactions().filterIndexed(CreditCardTransaction.COLUMN_CAPTURE_USERNAME, this);
    }

    public IndexedSet<CreditCardTransaction> getCreditCardTransactionsByVoidUsername() throws RemoteException {
        return getService().getConnector().getCreditCardTransactions().filterIndexed(CreditCardTransaction.COLUMN_VOID_USERNAME, this);
    }

    public IndexedSet<DisableLog> getDisableLogs() throws RemoteException {
        return getService().getConnector().getDisableLogs().filterIndexed(DisableLog.COLUMN_DISABLED_BY, this);
    }

    public MasterUser getMasterUser() throws RemoteException {
    	return getService().getConnector().getMasterUsers().filterUnique(MasterUser.COLUMN_USERNAME, this);
    }

    public IndexedSet<Resource> getResources() throws RemoteException {
        return getService().getConnector().getResources().filterIndexed(Resource.COLUMN_CREATED_BY, this);
    }

    public IndexedSet<TicketAction> getTicketActions() throws RemoteException {
        return getService().getConnector().getTicketActions().filterIndexed(TicketAction.COLUMN_ADMINISTRATOR, this);
    }

    public IndexedSet<TicketAction> getTicketActionsByOldAssignedTo() throws RemoteException {
        return getService().getConnector().getTicketActions().filterIndexed(TicketAction.COLUMN_OLD_ASSIGNED_TO, this);
    }

    public IndexedSet<TicketAction> getTicketActionsByNewAssignedTo() throws RemoteException {
        return getService().getConnector().getTicketActions().filterIndexed(TicketAction.COLUMN_NEW_ASSIGNED_TO, this);
    }

    public IndexedSet<TicketAssignment> getTicketAssignments() throws RemoteException {
        return getService().getConnector().getTicketAssignments().filterIndexed(TicketAssignment.COLUMN_ADMINISTRATOR, this);
    }

    public IndexedSet<Ticket> getTicketsByCreatedBy() throws RemoteException {
        return getService().getConnector().getTickets().filterIndexed(Ticket.COLUMN_CREATED_BY, this);
    }

    public IndexedSet<Transaction> getTransactions() throws RemoteException {
        return getService().getConnector().getTransactions().filterIndexed(Transaction.COLUMN_USERNAME, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Commands">
    /**
     * Validates a password and returns a description of the problem.  If the
     * password is valid, then <code>null</code> is returned.
     */
    public static List<PasswordChecker.Result> checkPassword(UserId username, String password) throws IOException {
        return PasswordChecker.checkPassword(username, password, PasswordChecker.PasswordStrength.STRICT);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Permissions">
    /**
     * A ticket administrator is part of a business that is also a reseller
     * and has the edit_ticket permission.
     */
    public boolean isTicketAdmin() throws RemoteException {
        Brand br = getUsername().getBusiness().getBrand();
        if(br==null) return false;
        return
            br.getReseller()!=null
            && hasPermission(AOServPermission.Permission.edit_ticket)
        ;
    }

    /**
     * Checks if this business administrator has the provided permission.
     */
    public boolean hasPermission(AOServPermission permission) throws RemoteException {
        for(BusinessAdministratorRole role : getBusinessAdministratorRoles()) {
            if(role.getRole().getAoservRolePermissions().filterUnique(AOServRolePermission.COLUMN_PERMISSION, permission)!=null) return true;
        }
        return false;
    }

    /**
     * Checks if this business administrator has the provided permission.
     */
    public boolean hasPermission(AOServPermission.Permission permission) throws RemoteException {
        return hasPermission(getService().getConnector().getAoservPermissions().get(permission.name()));
    }

    /**
     * Checks if this business administrator has all of the permissions.
     *
     * @param permissions a non-null, non-empty set of permissions.
     */
    public boolean hasPermissions(Set<AOServPermission.Permission> permissions) throws RemoteException {
        if(permissions==null) throw new IllegalArgumentException("permissions==null");
        if(permissions.isEmpty()) throw new IllegalArgumentException("permissions is empty");
        for(AOServPermission.Permission permission : permissions) {
            if(!hasPermission(permission)) return false;
        }
        return true;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Principal">
    /**
     * Gets the unique name of this Principal.
     */
    @Override
    public String getName() {
        return getKey().toString();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Access Control">
    public boolean canAccessAoServer(AOServer server) throws RemoteException {
        return canAccessServer(server.getServer());
    }

    public boolean canAccessBusiness(Business business) throws RemoteException {
        return canAccessBusiness(business.getAccounting());
    }

    public boolean canAccessBusiness(AccountingCode accounting) throws RemoteException {
        MasterUser mu = getMasterUser();
        if(mu!=null) {
            IndexedSet<MasterServer> mss = mu.getMasterServers();
            if(mss.isEmpty()) {
                // Unrestricted master
                return true;
            } else {
                // Restricted by server
                Business business = getService().getConnector().getBusinesses().filterUnique(Business.COLUMN_ACCOUNTING, accounting);
                if(business!=null) {
                    IndexedSet<Server> businessServers = business.getServers();
                    // There is usually only one MasterServer - iterate by it first
                    for(MasterServer ms : mss) if(businessServers.contains(ms.getServer())) return true;
                }
                return false;
            }
        } else {
            // Regular user
            Business business = getService().getConnector().getBusinesses().filterUnique(Business.COLUMN_ACCOUNTING, accounting);
            return business!=null && getUsername().getBusiness().isBusinessOrParentOf(business);
        }
    }

    public boolean canAccessLinuxAccount(LinuxAccount linuxAccount) throws RemoteException {
        return canAccessLinuxAccount(linuxAccount.getAoServerResource().getResource().getPkey());
    }

    public boolean canAccessLinuxAccount(int aoServerResource) throws RemoteException {
        MasterUser mu = getMasterUser();
        if(mu!=null) {
            IndexedSet<MasterServer> mss = mu.getMasterServers();
            if(mss.isEmpty()) {
                // Unrestricted master
                return true;
            } else {
                // Restricted by server
                LinuxAccount la = getService().getConnector().getLinuxAccounts().filterUnique(LinuxAccount.COLUMN_AO_SERVER_RESOURCE, aoServerResource);
                return la!=null && canAccessAoServer(la.getAoServerResource().getAoServer());
            }
        } else {
            // Regular user
            LinuxAccount la = getService().getConnector().getLinuxAccounts().filterUnique(LinuxAccount.COLUMN_AO_SERVER_RESOURCE, aoServerResource);
            return la!=null && canAccessUsername(la.getUserId());
        }
    }

    public boolean canAccessMySQLUser(MySQLUser mysqlUser) throws RemoteException {
        return canAccessMySQLUser(mysqlUser.getAoServerResource().getResource().getPkey());
    }

    public boolean canAccessMySQLUser(int mysqlUser) throws RemoteException {
        MasterUser mu = getMasterUser();
        if(mu!=null) {
            IndexedSet<MasterServer> mss = mu.getMasterServers();
            if(mss.isEmpty()) {
                // Unrestricted master
                return true;
            } else {
                // Restricted by server
                MySQLUser mysqlUserObj = getService().getConnector().getMysqlUsers().filterUnique(MySQLUser.COLUMN_AO_SERVER_RESOURCE, mysqlUser);
                return mysqlUserObj!=null && canAccessAoServer(mysqlUserObj.getAoServerResource().getAoServer());
            }
        } else {
            // Regular user
            MySQLUser mysqlUserObj = getService().getConnector().getMysqlUsers().filterUnique(MySQLUser.COLUMN_AO_SERVER_RESOURCE, mysqlUser);
            return mysqlUserObj!=null && canAccessUsername(mysqlUserObj.getUsername().getUsername());
        }
    }

    public boolean canAccessPostgresUser(PostgresUser postgresUser) throws RemoteException {
        return canAccessPostgresUser(postgresUser.getAoServerResource().getResource().getPkey());
    }

    public boolean canAccessPostgresUser(int postgresUser) throws RemoteException {
        MasterUser mu = getMasterUser();
        if(mu!=null) {
            IndexedSet<MasterServer> mss = mu.getMasterServers();
            if(mss.isEmpty()) {
                // Unrestricted master
                return true;
            } else {
                // Restricted by server
                PostgresUser postgresUserObj = getService().getConnector().getPostgresUsers().filterUnique(PostgresUser.COLUMN_AO_SERVER_RESOURCE, postgresUser);
                return postgresUserObj!=null && canAccessAoServer(postgresUserObj.getAoServerResource().getAoServer());
            }
        } else {
            // Regular user
            PostgresUser postgresUserObj = getService().getConnector().getPostgresUsers().filterUnique(PostgresUser.COLUMN_AO_SERVER_RESOURCE, postgresUser);
            return postgresUserObj!=null && canAccessUsername(postgresUserObj.getUsername().getUsername());
        }
    }

    public boolean canAccessServer(Server server) throws RemoteException {
        return canAccessServer(server.getPkey());
    }

    public boolean canAccessServer(int server) throws RemoteException {
        MasterUser mu = getMasterUser();
        if(mu!=null) {
            IndexedSet<MasterServer> mss = mu.getMasterServers();
            if(mss.isEmpty()) {
                // Unrestricted master
                return true;
            } else {
                // Restricted by server
                for(MasterServer ms : mss) if(ms.getServer().key==server) return true;
                return false;
            }
        } else {
            // Regular user
            Server serverObj = getService().getConnector().getServers().filterUnique(Server.COLUMN_PKEY, server);
            return serverObj!=null && getUsername().getBusiness().getServers().contains(serverObj);
        }
    }

    public boolean canAccessUsername(Username username) throws RemoteException {
        return canAccessUsername(username.getUsername());
    }

    public boolean canAccessUsername(UserId username) throws RemoteException {
        Username un = getService().getConnector().getUsernames().filterUnique(Username.COLUMN_USERNAME, username);
        return un!=null && canAccessBusiness(un.getBusiness());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public int arePasswordsSet() throws IOException, SQLException {
        return service.connector.requestBooleanQuery(true, AOServProtocol.CommandID.IS_BUSINESS_ADMINISTRATOR_PASSWORD_SET, pkey)?PasswordProtected.ALL:PasswordProtected.NONE;
    }

    public boolean canDisable() throws SQLException, IOException {
        return disableLog==null && !equals(getService().getConnector().getThisBusinessAdministrator());
    }

    public boolean canSwitchUser(BusinessAdministrator other) throws SQLException, IOException {
        if(isDisabled() || other.isDisabled()) return false;
        Business business=getUsername().getBusiness();
        Business otherBusiness=other.getUsername().getBusiness();
        return !business.equals(otherBusiness) && business.isBusinessOrParentOf(otherBusiness);
    }

    public boolean canEnable() throws SQLException, IOException {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable();
    }

    public PasswordChecker.Result[] checkPassword(String password) throws IOException {
        return checkPassword(pkey, password);
    }
    */

    /**
     * Validates a password and returns a description of the problem.  If the
     * password is valid, then <code>null</code> is returned.
     */
    /*public String checkPasswordDescribe(String password) {
	return checkPasswordDescribe(pkey, password);
    }*/

    /**
     * Validates a password and returns a description of the problem.  If the
     * password is valid, then <code>null</code> is returned.
     */
    /*public static String checkPasswordDescribe(String username, String password) {
	return PasswordChecker.checkPasswordDescribe(username, password, true, false);
    }*/

    /* TODO
    public void disable(DisableLog dl) throws IOException, SQLException {
        service.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.BUSINESS_ADMINISTRATORS, dl.pkey, pkey);
    }

    public void enable() throws IOException, SQLException {
        service.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.BUSINESS_ADMINISTRATORS, pkey);
    }

    public boolean isDisabled() {
        return disableLog!=null;
    }

    public List<MonthlyCharge> getMonthlyCharges() throws IOException, SQLException {
    	return getService().getConnector().getMonthlyCharges().getMonthlyCharges(this);
    }

    public boolean isActiveAccounting() throws IOException, SQLException {
	MasterUser user=getMasterUser();
	return
            user!=null
            && user.isActive()
            && user.canAccessAccounting()
	;
    }

    public boolean isActiveBankAccounting() throws IOException, SQLException {
	MasterUser user=getMasterUser();
	return
            user!=null
            && user.isActive()
            && user.canAccessBankAccount()
	;
    }

    public boolean isActiveDNSAdmin() throws IOException, SQLException {
	MasterUser user=getMasterUser();
	return
            user!=null
            && user.isActive()
            && user.isDNSAdmin()
	;
    }

    public boolean isActiveTableInvalidator() throws IOException, SQLException {
	MasterUser user=getMasterUser();
	return
            user!=null
            && user.isActive()
            && user.canInvalidateTables()
	;
    }

    public boolean isActiveWebAdmin() throws IOException, SQLException {
	MasterUser user=getMasterUser();
	return
            user!=null
            && user.isActive()
            && user.isWebAdmin()
	;
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() throws SQLException, IOException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        AOServConnector<?,?> conn=getService().getConnector();

        if(equals(conn.getThisBusinessAdministrator())) reasons.add(new CannotRemoveReason<BusinessAdministrator>("Not allowed to remove self", this));

        List<TicketAction> actions=getTicketActions();
        if(!actions.isEmpty()) reasons.add(new CannotRemoveReason<TicketAction>("Author of "+actions.size()+" ticket "+(actions.size()==1?"action":"actions"), actions));

        List<Ticket> tickets=getCreatedTickets();
        if(!tickets.isEmpty()) reasons.add(new CannotRemoveReason<Ticket>("Author of "+tickets.size()+' '+(tickets.size()==1?"ticket":"tickets"), tickets));

        List<Transaction> trs=getTransactions();
        if(!trs.isEmpty()) reasons.add(new CannotRemoveReason<Transaction>("Created "+trs.size()+' '+(trs.size()==1?"transaction":"transactions"), trs));

        return reasons;
    }

    public void remove() throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.BUSINESS_ADMINISTRATORS,
            pkey
        );
    }

    public void setProfile(
        final String name,
        String title,
        final long birthday,
        final boolean isPrivate,
        final String workPhone,
        String homePhone,
        String cellPhone,
        String fax,
        final String email,
        String address1,
        String address2,
        String city,
        String state,
        String country,
        String zip
    ) throws IOException, SQLException {
        if(title!=null && title.length()==0) title=null;
        final String finalTitle = title;
        if(homePhone!=null && homePhone.length()==0) homePhone=null;
        final String finalHomePhone = homePhone;
        if(cellPhone!=null && cellPhone.length()==0) cellPhone=null;
        final String finalCellPhone = cellPhone;
        if(fax!=null && fax.length()==0) fax=null;
        final String finalFax = fax;
        if(address1!=null && address1.length()==0) address1=null;
        final String finalAddress1 = address1;
        if(address2!=null && address2.length()==0) address2=null;
        final String finalAddress2 = address2;
        if(city!=null && city.length()==0) city=null;
        final String finalCity = city;
        if(state!=null && state.length()==0) state=null;
        final String finalState = state;
        if(country!=null && country.length()==0) country=null;
        final String finalCountry = country;
        if(zip!=null && zip.length()==0) zip=null;
        final String finalZip = zip;
        getService().getConnector().requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.SET_BUSINESS_ADMINISTRATOR_PROFILE.ordinal());
                    out.writeUTF(pkey);
                    out.writeUTF(name);
                    out.writeBoolean(finalTitle!=null); if(finalTitle!=null) out.writeUTF(finalTitle);
                    out.writeLong(birthday);
                    out.writeBoolean(isPrivate);
                    out.writeUTF(workPhone);
                    out.writeBoolean(finalHomePhone!=null); if(finalHomePhone!=null) out.writeUTF(finalHomePhone);
                    out.writeBoolean(finalCellPhone!=null); if(finalCellPhone!=null) out.writeUTF(finalCellPhone);
                    out.writeBoolean(finalFax!=null); if(finalFax!=null) out.writeUTF(finalFax);
                    out.writeUTF(email);
                    out.writeBoolean(finalAddress1!=null); if(finalAddress1!=null) out.writeUTF(finalAddress1);
                    out.writeBoolean(finalAddress2!=null); if(finalAddress2!=null) out.writeUTF(finalAddress2);
                    out.writeBoolean(finalCity!=null); if(finalCity!=null) out.writeUTF(finalCity);
                    out.writeBoolean(finalState!=null); if(finalState!=null) out.writeUTF(finalState);
                    out.writeBoolean(finalCountry!=null); if(finalCountry!=null) out.writeUTF(finalCountry);
                    out.writeBoolean(finalZip!=null); if(finalZip!=null) out.writeUTF(finalZip);
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
    */

    /**
     * Determines if a name can be used as a username.  The same rules apply as for
     * Username.
     *
     * @see  Username#checkUsername
     */
    /* TODO
    public static String checkUsername(String name) {
        return Username.checkUsername(name);
    }*/

    /**
     * Checks if this business administrator has the provided permission.
     */
    /* TODO
    public boolean hasPermission(String permission) throws IOException, SQLException {
        return getService().getConnector().getBusinessAdministratorPermissions().hasPermission(this, permission);
    }

    public List<MonthlyCharge> getMonthlyChargesByCreatedBy() throws IOException, SQLException {
        return getService().getConnector().getMonthlyCharges().getMonthlyChargesByCreatedBy(this);
    }

    public List<SignupRequest> getCompletedSignupRequests() throws IOException, SQLException {
        return getService().getConnector().getSignupRequests().getIndexedRows(SignupRequest.COLUMN_COMPLETED_BY, pkey);
    }*/
    // </editor-fold>
}
