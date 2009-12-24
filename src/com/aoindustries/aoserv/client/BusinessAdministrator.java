package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.Base64Coder;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.WrappedException;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * A <code>BusinessAdministrator</code> is a username and password pair, usually
 * representing an individual or an application, that has administrative control
 * over all resources in a <code>Business</code> or any any of its child businesses.
 *
 * @see  Business
 *
 * @author  AO Industries, Inc.
 */
final public class BusinessAdministrator extends AOServObjectStringKey<BusinessAdministrator> /* TODO: implements PasswordProtected, Removable, Disablable, Comparable<BusinessAdministrator> */ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * Value representing no password.
     */
    public static final String NO_PASSWORD = "*";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Password Encryption">
    /**
     * Encrypts a password.  If the password is <code>null</code>, returns <code>NO_PASSWORD</code>.
     * If the salt is <code>null</code>, a random salt will be generated.
     *
     * @deprecated  Please use hash instead
     * @see #hash(String)
     */
    public static String crypt(String password, String salt) {
        if(password==null || password.length()==0) return BusinessAdministrator.NO_PASSWORD;
        return salt==null || salt.length()==0?com.aoindustries.util.UnixCrypt.crypt(password):com.aoindustries.util.UnixCrypt.crypt(password, salt);
    }

    /**
     * Performs a one-way hash of the plaintext value using SHA-1.
     *
     * @exception  WrappedException  if any problem occurs.
     */
    public static String hash(String plaintext) throws WrappedException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(plaintext.getBytes("UTF-8"));
            return new String(Base64Coder.encode(md.digest()));
        } catch(NoSuchAlgorithmException err) {
            throw new WrappedException(err);
        } catch(UnsupportedEncodingException err) {
            throw new WrappedException(err);
        }
    }

    @SuppressWarnings("deprecation")
    public static boolean passwordMatches(String plaintext, String ciphertext) {
        if(!NO_PASSWORD.equals(ciphertext)) {
            // Try hash first
            String hashed = hash(plaintext);
            if(hashed.equals(ciphertext)) return true;
            // Try old crypt next
            if(ciphertext.length()>=2) {
                String salt=ciphertext.substring(0,2);
                String crypted=com.aoindustries.util.UnixCrypt.crypt(plaintext, salt);
                return crypted.equals(ciphertext);
            }
        }
    	return false;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private String password;
    final private String name;
    final private String title;
    final private Date birthday;
    final private boolean isPreferred;
    final private boolean isPrivate;
    final private Timestamp created;
    final private String work_phone;
    final private String home_phone;
    final private String cell_phone;
    final private String fax;
    final private String email;
    final private String address1;
    final private String address2;
    final private String city;
    final private String state;
    final private String country;
    final private String zip;
    final private int disable_log;
    final private boolean can_switch_users;
    final private String support_code;

    public BusinessAdministrator(
        BusinessAdministratorService<?,?> service,
        String username,
        String password,
        String name,
        String title,
        Date birthday,
        boolean isPreferred,
        boolean isPrivate,
        Timestamp created,
        String work_phone,
        String home_phone,
        String cell_phone,
        String fax,
        String email,
        String address1,
        String address2,
        String city,
        String state,
        String country,
        String zip,
        int disable_log,
        boolean can_switch_users,
        String support_code
    ) {
        super(service, username);
        this.password = password;
        this.name = name;
        this.title = title;
        this.birthday = birthday;
        this.isPreferred = isPreferred;
        this.isPrivate = isPrivate;
        this.created = created;
        this.work_phone = work_phone;
        this.home_phone = home_phone;
        this.cell_phone = cell_phone;
        this.fax = fax;
        this.email = email;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.country = StringUtility.intern(country);
        this.zip = zip;
        this.disable_log = disable_log;
        this.can_switch_users = can_switch_users;
        this.support_code = support_code;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Columns">
    /* TODO
    @SchemaColumn(order=0, name="username", unique=true, description="the unique identifier for this admin")
    public Username getUsername() throws SQLException, IOException {
        Username usernameObject = service.connector.getUsernames().get(pkey);
        if (usernameObject == null) throw new SQLException("Username not found: " + pkey);
        return usernameObject;
    }*/

    @SchemaColumn(order=1, name="password", description="the encrypted password for this admin")
    public String getPassword() {
    	return password;
    }

    @SchemaColumn(order=2, name="name", description="the name of this admin")
    public String getName() {
    	return name;
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
    	return created;
    }

    @SchemaColumn(order=8, name="work_phone", description="the work phone number (if different than business)")
    public String getWorkPhone() {
    	return work_phone;
    }

    @SchemaColumn(order=9, name="home_phone", description="the home phone number")
    public String getHomePhone() {
    	return home_phone;
    }

    @SchemaColumn(order=10, name="cell_phone", description="the cellular phone number")
    public String getCellPhone() {
    	return cell_phone;
    }

    @SchemaColumn(order=11, name="fax", description="the fax number (if different than business)")
    public String getFax() {
    	return fax;
    }

    @SchemaColumn(order=12, name="email", description="the email address")
    public String getEmail() {
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

    /* TODO
    @SchemaColumn(order=17, name="country", description="the country (if different than business)")
    public CountryCode getCountry() throws SQLException, IOException {
        if(country == null) return null;
        CountryCode countryCode=service.connector.getCountryCodes().get(country);
        if (countryCode == null) throw new SQLException("CountryCode not found: " + country);
        return countryCode;
    }*/

    @SchemaColumn(order=18, name="zip", description="the zip code (if different than business)")
    public String getZIP() {
        return zip;
    }

    @SchemaColumn(order=19, name="disable_log", description="indicates that this account is disabled")
    public DisableLog getDisableLog() throws RemoteException {
        if(disable_log==-1) return null;
        DisableLog obj = getService().getConnector().getDisableLogs().get(disable_log);
        if(obj==null) throw new RemoteException("Unable to find DisableLog: "+disable_log);
        return obj;
    }

    @SchemaColumn(order=20, name="can_switch_users", description="allows this person to switch users to any subaccounts")
    public boolean canSwitchUsers() {
        return can_switch_users;
    }

    @SchemaColumn(order=21, name="support_code", unique=true, description="used to authenticate for email-based supprt")
    public String getSupportCode() {
        return support_code;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public int arePasswordsSet() throws IOException, SQLException {
        return service.connector.requestBooleanQuery(true, AOServProtocol.CommandID.IS_BUSINESS_ADMINISTRATOR_PASSWORD_SET, pkey)?PasswordProtected.ALL:PasswordProtected.NONE;
    }

    public boolean canDisable() throws SQLException, IOException {
        return disable_log==-1 && !equals(service.connector.getThisBusinessAdministrator());
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

    public PasswordChecker.Result[] checkPassword(Locale userLocale, String password) throws IOException {
        return checkPassword(userLocale, pkey, password);
    }
    */

    /**
     * Validates a password and returns a description of the problem.  If the
     * password is valid, then <code>null</code> is returned.
     */
    /* TODO
    public static PasswordChecker.Result[] checkPassword(Locale userLocale, String username, String password) throws IOException {
	return PasswordChecker.checkPassword(userLocale, username, password, true, false);
    }*/

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

    public List<TicketAction> getTicketActions() throws IOException, SQLException {
        return service.connector.getTicketActions().getIndexedRows(TicketAction.COLUMN_ADMINISTRATOR, pkey);
    }

    public List<TicketAction> getTicketActionsByOldAssignedTo() throws IOException, SQLException {
        return service.connector.getTicketActions().getIndexedRows(TicketAction.COLUMN_OLD_ASSIGNED_TO, pkey);
    }

    public List<TicketAction> getTicketActionsByNewAssignedTo() throws IOException, SQLException {
        return service.connector.getTicketActions().getIndexedRows(TicketAction.COLUMN_NEW_ASSIGNED_TO, pkey);
    }

    public List<TicketAssignment> getTicketAssignments() throws IOException, SQLException {
        return service.connector.getTicketAssignments().getTicketAssignments(this);
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_USERNAME: return pkey;
            case 1: return password;
            case 2: return name;
            case 3: return title;
            case 4: return birthday==-1?null:new java.sql.Date(birthday);
            case 5: return isPreferred?Boolean.TRUE:Boolean.FALSE;
            case 6: return isPrivate?Boolean.TRUE:Boolean.FALSE;
            case 7: return new java.sql.Date(created);
            case 8: return work_phone;
            case 9: return home_phone;
            case 10: return cell_phone;
            case 11: return fax;
            case 12: return email;
            case 13: return address1;
            case 14: return address2;
            case 15: return city;
            case 16: return state;
            case 17: return country;
            case 18: return zip;
            case 19: return disable_log==-1?null:Integer.valueOf(disable_log);
            case 20: return can_switch_users?Boolean.TRUE:Boolean.FALSE;
            case 21: return support_code;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public List<Ticket> getCreatedTickets() throws IOException, SQLException {
        return service.connector.getTickets().getIndexedRows(Ticket.COLUMN_CREATED_BY, pkey);
    }

    public boolean isDisabled() {
        return disable_log!=-1;
    }

    public MasterUser getMasterUser() throws IOException, SQLException {
    	return service.connector.getMasterUsers().get(pkey);
    }

    public List<MonthlyCharge> getMonthlyCharges() throws IOException, SQLException {
    	return service.connector.getMonthlyCharges().getMonthlyCharges(this);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.BUSINESS_ADMINISTRATORS;
    }

    public List<Transaction> getTransactions() throws IOException, SQLException {
        return service.connector.getTransactions().getTransactions(this);
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

    public boolean passwordMatches(String plaintext) {
        return passwordMatches(plaintext, password);
    }

    public void init(ResultSet result) throws SQLException {
        pkey = result.getString(1);
        password = result.getString(2).trim();
        name = result.getString(3);
        title = result.getString(4);
        String S=result.getString(5);
        birthday = S==null?-1:SQLUtility.getDate(S.substring(0,10)).getTime();
        isPreferred = result.getBoolean(6);
        isPrivate = result.getBoolean(7);
        created = result.getTimestamp(8).getTime();
        work_phone = result.getString(9);
        home_phone = result.getString(10);
        cell_phone = result.getString(11);
        fax = result.getString(12);
        email = result.getString(13);
        address1 = result.getString(14);
        address2 = result.getString(15);
        city = result.getString(16);
        state = result.getString(17);
        country = result.getString(18);
        zip = result.getString(19);
        disable_log=result.getInt(20);
        if(result.wasNull()) disable_log=-1;
        can_switch_users=result.getBoolean(21);
        support_code = result.getString(22);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readUTF().intern();
        password=in.readUTF();
        name=in.readUTF();
        title=in.readNullUTF();
        birthday=in.readLong();
        isPreferred=in.readBoolean();
        isPrivate=in.readBoolean();
        created=in.readLong();
        work_phone=in.readUTF();
        home_phone=in.readNullUTF();
        cell_phone=in.readNullUTF();
        fax=in.readNullUTF();
        email=in.readUTF();
        address1=in.readNullUTF();
        address2=in.readNullUTF();
        city=in.readNullUTF();
        state=StringUtility.intern(in.readNullUTF());
        country=StringUtility.intern(in.readNullUTF());
        zip=in.readNullUTF();
        disable_log=in.readCompressedInt();
        can_switch_users=in.readBoolean();
        support_code = in.readNullUTF();
    }

    public List<? extends AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
            getUsername()
        );
    }

    public List<? extends AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
            createDependencyList(
                getMasterUser()
            ),
            getBusinessesByCreatedBy(),
            getPermissions(),
            getCreditCardsByCreatedBy(),
            getCreditCardTransactionsByAuthorizationUsername(),
            getCreditCardTransactionsByCaptureUsername(),
            getCreditCardTransactionsByVoidUsername(),
            getDisableLogsByDisabledBy(),
            getMonthlyCharges(),
            getMonthlyChargesByCreatedBy(),
            getResources(),
            getCreatedTickets(),
            getCompletedSignupRequests(),
            getTicketActions(),
            getTicketActionsByOldAssignedTo(),
            getTicketActionsByNewAssignedTo(),
            getTicketAssignments(),
            getTransactions()
        );
    }

    public List<CannotRemoveReason> getCannotRemoveReasons(Locale userLocale) throws SQLException, IOException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        AOServConnector conn=table.connector;

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
        table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.BUSINESS_ADMINISTRATORS,
            pkey
        );
    }
    */

    /**
     * Sets the password for this <code>BusinessAdministrator</code>.  All connections must
     * be over secure protocols for this method to work.  If the connections
     * are not secure, an <code>IOException</code> is thrown.
     */
    /* TODO
    public void setPassword(String plaintext) throws IOException, SQLException {
        AOServConnector connector=table.connector;
        if(!connector.isSecure()) throw new IOException("Passwords for business_administrators may only be set when using secure protocols.  Currently using the "+connector.getProtocol()+" protocol, which is not secure.");
    	connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_BUSINESS_ADMINISTRATOR_PASSWORD, pkey, plaintext);
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
        table.connector.requestUpdate(
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
                    table.connector.tablesUpdated(invalidateList);
                }
            }
        );
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(pkey);
        out.writeUTF(password);
        out.writeUTF(name);
        out.writeNullUTF(title);
        out.writeLong(birthday);
        out.writeBoolean(isPreferred);
        out.writeBoolean(isPrivate);
        out.writeLong(created);
        out.writeUTF(work_phone);
        out.writeNullUTF(home_phone);
        out.writeNullUTF(cell_phone);
        out.writeNullUTF(fax);
        out.writeUTF(email);
        out.writeNullUTF(address1);
        out.writeNullUTF(address2);
        out.writeNullUTF(city);
        out.writeNullUTF(state);
        out.writeNullUTF(country);
        out.writeNullUTF(zip);
        out.writeCompressedInt(disable_log);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_118)>=0) out.writeBoolean(can_switch_users);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_44)>=0) out.writeNullUTF(support_code);
    }
    */

    /**
     * Determines if a name can be used as a username.  The same rules apply as for
     * Username.
     *
     * @see  Username#checkUsername
     */
    /* TODO
    public static String checkUsername(String name, Locale locale) {
        return Username.checkUsername(name, locale);
    }*/

    /**
     * Determines if a name can be used as a username.  The same rules apply as for
     * Username.
     *
     * @deprecated  Please use <code>checkUsername(String)</code> to give users more details when the check fails.
     *
     * @see  Username#isValidUsername
     */
    /* TODO
    public static boolean isValidUsername(String name) {
        return Username.isValidUsername(name);
    }

    public boolean canSetPassword() {
        return disable_log==-1;
    }

    public List<BusinessAdministratorPermission> getPermissions() throws IOException, SQLException {
        return table.connector.getBusinessAdministratorPermissions().getPermissions(this);
    }*/

    /**
     * Checks if this business administrator has the provided permission.
     */
    /* TODO
    public boolean hasPermission(AOServPermission permission) throws IOException, SQLException {
        return hasPermission(permission.getName());
    }*/

    /**
     * Checks if this business administrator has the provided permission.
     */
    /* TODO
    public boolean hasPermission(AOServPermission.Permission permission) throws IOException, SQLException {
        return hasPermission(permission.name());
    }*/

    /**
     * Checks if this business administrator has the provided permission.
     */
    /* TODO
    public boolean hasPermission(String permission) throws IOException, SQLException {
        return table.connector.getBusinessAdministratorPermissions().hasPermission(this, permission);
    }*/

    /* TODO
    public List<Resource> getResources() throws IOException, SQLException {
        return table.connector.getResources().getIndexedRows(Resource.COLUMN_CREATED_BY, pkey);
    }

    public List<Business> getBusinessesByCreatedBy() throws IOException, SQLException {
        return table.connector.getBusinesses().getIndexedRows(Business.COLUMN_CREATED_BY, pkey);
    }

    public List<CreditCard> getCreditCardsByCreatedBy() throws IOException, SQLException {
        return table.connector.getCreditCards().getIndexedRows(CreditCard.COLUMN_CREATED_BY, pkey);
    }

    public List<CreditCardTransaction> getCreditCardTransactionsByCreditCardCreatedBy() throws IOException, SQLException {
        return table.connector.getCreditCardTransactions().getIndexedRows(CreditCardTransaction.COLUMN_CREDIT_CARD_CREATED_BY, pkey);
    }

    public List<CreditCardTransaction> getCreditCardTransactionsByAuthorizationUsername() throws IOException, SQLException {
        return table.connector.getCreditCardTransactions().getIndexedRows(CreditCardTransaction.COLUMN_AUTHORIZATION_USERNAME, pkey);
    }

    public List<CreditCardTransaction> getCreditCardTransactionsByCaptureUsername() throws IOException, SQLException {
        return table.connector.getCreditCardTransactions().getIndexedRows(CreditCardTransaction.COLUMN_CAPTURE_USERNAME, pkey);
    }

    public List<CreditCardTransaction> getCreditCardTransactionsByVoidUsername() throws IOException, SQLException {
        return table.connector.getCreditCardTransactions().getIndexedRows(CreditCardTransaction.COLUMN_VOID_USERNAME, pkey);
    }

    public List<DisableLog> getDisableLogsByDisabledBy() throws IOException, SQLException {
        return table.connector.getDisableLogs().getIndexedRows(DisableLog.COLUMN_DISABLED_BY, pkey);
    }

    public List<MonthlyCharge> getMonthlyChargesByCreatedBy() throws IOException, SQLException {
        return table.connector.getMonthlyCharges().getMonthlyChargesByCreatedBy(this);
    }

    public List<SignupRequest> getCompletedSignupRequests() throws IOException, SQLException {
        return table.connector.getSignupRequests().getIndexedRows(SignupRequest.COLUMN_COMPLETED_BY, pkey);
    }*/
    // </editor-fold>
}
