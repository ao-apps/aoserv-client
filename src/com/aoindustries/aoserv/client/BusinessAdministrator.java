package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * A <code>BusinessAdministrator</code> is a username and password pair, usually
 * representing an individual or an application, that has administrative control
 * over all resources in a <code>Business</code> or any any of its child businesses.
 *
 *
 * @see  Business
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class BusinessAdministrator extends CachedObjectStringKey<BusinessAdministrator> implements PasswordProtected, Removable, Disablable {

    static final int COLUMN_USERNAME=0;
    static final String COLUMN_USERNAME_name = "username";

    /**
     * Value representing no password.
     */
    public static final String NO_PASSWORD = "*";

    private String
        password,
        name,
        title
    ;
    private long birthday;
    private boolean isPreferred;
    private boolean isPrivate;
    private long created;
    private String
        work_phone,
        home_phone,
        cell_phone,
        fax,
        email,
        address1,
        address2,
        city,
        state,
        country,
        zip
    ;
    int disable_log;
    boolean can_switch_users;

    public int arePasswordsSet() {
        return table.connector.requestBooleanQuery(AOServProtocol.CommandID.IS_BUSINESS_ADMINISTRATOR_PASSWORD_SET, pkey)?PasswordProtected.ALL:PasswordProtected.NONE;
    }

    public int addTicket(
	Business business,
	TicketType ticket_typeObj,
	String details,
	long deadline,
	TicketPriority client_priorityObj,
	TicketPriority admin_priorityObj,
	TechnologyName technologyObj,
        BusinessAdministrator assignedTo,
        String contact_emails,
        String contact_phone_numbers
    ) {
	return addTicket(
            business,
            ticket_typeObj.pkey,
            details,
            deadline,
            client_priorityObj.pkey,
            admin_priorityObj==null ? null : admin_priorityObj.pkey,
            technologyObj==null?null:technologyObj.pkey,
            assignedTo,
            contact_emails,
            contact_phone_numbers
	);
    }

    public int addTicket(
	Business business,
	String ticket_type,
	String details,
	long deadline,
	String client_priority,
	String admin_priority,
	String technology,
        BusinessAdministrator assignedTo,
        String contact_emails,
        String contact_phone_numbers
    ) {
	return table.connector.tickets.addTicket(
            business,
            this,
            ticket_type,
            details,
            deadline,
            client_priority,
            admin_priority,
            technology,
            assignedTo,
            contact_emails,
            contact_phone_numbers
        );
    }

    public boolean canDisable() {
        return disable_log==-1 && !equals(table.connector.getThisBusinessAdministrator());
    }
    
    public boolean canSwitchUsers() {
        return can_switch_users;
    }
    
    public boolean canSwitchUser(BusinessAdministrator other) {
        if(getDisableLog()!=null || other.getDisableLog()!=null) return false;
        Business business=getUsername().getPackage().getBusiness();
        Business otherBusiness=other.getUsername().getPackage().getBusiness();
        return !business.equals(otherBusiness) && business.isBusinessOrParentOf(otherBusiness);
    }

    public boolean canEnable() {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable();
    }

    public PasswordChecker.Result[] checkPassword(Locale userLocale, String password) {
	return checkPassword(userLocale, pkey, password);
    }

    /**
     * Validates a password and returns a description of the problem.  If the
     * password is valid, then <code>null</code> is returned.
     */
    public static PasswordChecker.Result[] checkPassword(Locale userLocale, String username, String password) {
	return PasswordChecker.checkPassword(userLocale, username, password, true, false);
    }

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

    /**
     * Encrypts a password.  If the password is <code>null</code>, returns <code>NO_PASSWORD</code>.
     * If the salt is <code>null</code>, a random salt will be generated.
     */
    public static String crypt(String password, String salt) {
	if(password==null || password.length()==0) return BusinessAdministrator.NO_PASSWORD;
	return salt==null || salt.length()==0?UnixCrypt.crypt(password):UnixCrypt.crypt(password, salt);
    }

    public void disable(DisableLog dl) {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.BUSINESS_ADMINISTRATORS, dl.pkey, pkey);
    }
    
    public void enable() {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.BUSINESS_ADMINISTRATORS, pkey);
    }

    public List<Action> getActions() {
        return table.connector.actions.getActions(this);
    }

    public String getAddress1() {
	return address1;
    }

    public String getAddress2() {
	return address2;
    }

    public long getBirthday() {
	return birthday;
    }

    public String getCellPhone() {
	return cell_phone;
    }

    public String getCity() {
	return city;
    }

    public Object getColumn(int i) {
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
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public CountryCode getCountry() throws SQLException {
        if(country == null) return null;
        CountryCode countryCode=table.connector.countryCodes.get(country);
        if (countryCode == null) throw new SQLException("CountryCode not found: " + country);
        return countryCode;
    }

    public long getCreated() {
	return created;
    }

    public List<Ticket> getCreatedTickets() {
        return table.connector.tickets.getCreatedTickets(this);
    }

    public List<Ticket> getClosedTickets() {
        return table.connector.tickets.getClosedTickets(this);
    }

    public DisableLog getDisableLog() {
        if(disable_log==-1) return null;
        DisableLog obj=table.connector.disableLogs.get(disable_log);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find DisableLog: "+disable_log));
        return obj;
    }

    public String getEmail() {
	return email;
    }

    public String getFax() {
	return fax;
    }

    public String getHomePhone() {
	return home_phone;
    }

    public MasterUser getMasterUser() {
	return table.connector.masterUsers.get(pkey);
    }

    public List<MonthlyCharge> getMonthlyCharges() {
	return table.connector.monthlyCharges.getMonthlyCharges(this, null);
    }

    public String getName() {
	return name;
    }

    /**
     * Gets the encrypted password for this business_administrator.  This information is only
     * available if all communication has been over secure connections.  Otherwise,
     * all passwords will be changed to <code>NO_PASSWORD</code>.
     *
     * @see  AOServConnector#isSecure
     */
    public String getPassword() {
	return password;
    }

    public String getState() {
	return state;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.BUSINESS_ADMINISTRATORS;
    }

    public List<Ticket> getTickets() {
	return table.connector.tickets.getTickets(this);
    }

    public String getTitle() {
	return title;
    }

    public List<Transaction> getTransactions() {
        return table.connector.transactions.getTransactions(this);
    }

     public Username getUsername() {
        Username usernameObject = table.connector.usernames.get(pkey);
        if (usernameObject == null) throw new WrappedException(new SQLException("Username not found: " + pkey));
        return usernameObject;
    }

    public String getWorkPhone() {
	return work_phone;
    }

    public String getZIP() {
	return zip;
    }

    public boolean isActiveAccounting() {
	MasterUser user=getMasterUser();
	return 
            user!=null
            && user.isActive()
            && user.canAccessAccounting()
	;
    }

    public boolean isActiveBankAccounting() {
	MasterUser user=getMasterUser();
	return 
            user!=null
            && user.isActive()
            && user.canAccessBankAccount()
	;
    }

    public boolean isActiveDNSAdmin() {
	MasterUser user=getMasterUser();
	return 
            user!=null
            && user.isActive()
            && user.isDNSAdmin()
	;
    }

    public boolean isActiveTableInvalidator() {
	MasterUser user=getMasterUser();
	return 
            user!=null
            && user.isActive()
            && user.canInvalidateTables()
	;
    }

    public boolean isActiveTicketAdmin() {
	MasterUser user=getMasterUser();
	return 
            user!=null
            && user.isActive()
            && user.isTicketAdmin()
	;
    }

    public boolean isActiveWebAdmin() {
	MasterUser user=getMasterUser();
	return 
            user!=null
            && user.isActive()
            && user.isWebAdmin()
	;
    }

    public boolean isPreferred() {
	return isPreferred;
    }

    public boolean isPrivate() {
	return isPrivate;
    }

    public boolean passwordMatches(String plaintext) {
	if(password.length()>=2) {
            String salt=password.substring(0,2);
            String crypted=UnixCrypt.crypt(plaintext, salt);
            return crypted.equals(password);
	}
	return false;
    }

    void initImpl(ResultSet result) throws SQLException {
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
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        AOServConnector conn=table.connector;

        if(equals(conn.getThisBusinessAdministrator())) reasons.add(new CannotRemoveReason<BusinessAdministrator>("Not allowed to remove self", this));
        
        List<Action> actions=getActions();
        if(!actions.isEmpty()) reasons.add(new CannotRemoveReason<Action>("Author of "+actions.size()+" ticket "+(actions.size()==1?"action":"actions"), actions));

        List<Ticket> tickets=getCreatedTickets();
        if(!tickets.isEmpty()) reasons.add(new CannotRemoveReason<Ticket>("Author of "+tickets.size()+' '+(tickets.size()==1?"ticket":"tickets"), tickets));
        
        tickets=getClosedTickets();
        if(!tickets.isEmpty()) reasons.add(new CannotRemoveReason<Ticket>("Closed "+tickets.size()+' '+(tickets.size()==1?"ticket":"tickets"), tickets));
        
        List<Transaction> trs=getTransactions();
        if(!trs.isEmpty()) reasons.add(new CannotRemoveReason<Transaction>("Created "+trs.size()+' '+(trs.size()==1?"transaction":"transactions"), trs));

        return reasons;
    }

    public void remove() {
	table.connector.requestUpdateIL(
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.BUSINESS_ADMINISTRATORS,
            pkey
	);
    }

    /**
     * Sets the password for this <code>BusinessAdministrator</code>.  All connections must
     * be over secure protocols for this method to work.  If the connections
     * are not secure, an <code>IOException</code> is thrown.
     */
    public void setPassword(String plaintext) {
	AOServConnector connector=table.connector;
	if(!connector.isSecure()) throw new WrappedException(new IOException("Passwords for business_administrators may only be set when using secure protocols.  Currently using the "+connector.getProtocol()+" protocol, which is not secure."));
	connector.requestUpdateIL(AOServProtocol.CommandID.SET_BUSINESS_ADMINISTRATOR_PASSWORD, pkey, plaintext);
    }

    public void setProfile(
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
	String zip
    ) {
        try {
            IntList invalidateList;
            AOServConnection connection=table.connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.CommandID.SET_BUSINESS_ADMINISTRATOR_PROFILE.ordinal());
                out.writeUTF(pkey);
                out.writeUTF(name);
                if(title!=null && title.length()==0) title=null;
                out.writeBoolean(title!=null); if(title!=null) out.writeUTF(title);
                out.writeLong(birthday);
                out.writeBoolean(isPrivate);
                out.writeUTF(workPhone);
                if(homePhone!=null && homePhone.length()==0) homePhone=null;
                out.writeBoolean(homePhone!=null); if(homePhone!=null) out.writeUTF(homePhone);
                if(cellPhone!=null && cellPhone.length()==0) cellPhone=null;
                out.writeBoolean(cellPhone!=null); if(cellPhone!=null) out.writeUTF(cellPhone);
                if(fax!=null && fax.length()==0) fax=null;
                out.writeBoolean(fax!=null); if(fax!=null) out.writeUTF(fax);
                out.writeUTF(email);
                if(address1!=null && address1.length()==0) address1=null;
                out.writeBoolean(address1!=null); if(address1!=null) out.writeUTF(address1);
                if(address2!=null && address2.length()==0) address2=null;
                out.writeBoolean(address2!=null); if(address2!=null) out.writeUTF(address2);
                if(city!=null && city.length()==0) city=null;
                out.writeBoolean(city!=null); if(city!=null) out.writeUTF(city);
                if(state!=null && state.length()==0) state=null;
                out.writeBoolean(state!=null); if(state!=null) out.writeUTF(state);
                if(country!=null && country.length()==0) country=null;
                out.writeBoolean(country!=null); if(country!=null) out.writeUTF(country);
                if(zip!=null && zip.length()==0) zip=null;
                out.writeBoolean(zip!=null); if(zip!=null) out.writeUTF(zip);
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

    public void write(CompressedDataOutputStream out, String version) throws IOException {
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
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_118)>=0) out.writeBoolean(can_switch_users);
    }

    /**
     * Determines if a name can be used as a username.  The same rules apply as for
     * Username.
     *
     * @see  Username#checkUsername
     */
    public static String checkUsername(String name, Locale locale) {
        return Username.checkUsername(name, locale);
    }
    
    /**
     * Determines if a name can be used as a username.  The same rules apply as for
     * Username.
     *
     * @deprecated  Please use <code>checkUsername(String)</code> to give users more details when the check fails.
     *
     * @see  Username#isValidUsername
     */
    public static boolean isValidUsername(String name) {
        return Username.isValidUsername(name);
    }

    public boolean canSetPassword() {
        return disable_log==-1;
    }

    public List<BusinessAdministratorPermission> getPermissions() {
        return table.connector.businessAdministratorPermissions.getPermissions(this);
    }
    
    /**
     * Checks if this business administrator has the provided permission.
     */
    public boolean hasPermission(AOServPermission permission) {
        return hasPermission(permission.getName());
    }

    /**
     * Checks if this business administrator has the provided permission.
     */
    public boolean hasPermission(AOServPermission.Permission permission) {
        return hasPermission(permission.name());
    }

    /**
     * Checks if this business administrator has the provided permission.
     */
    public boolean hasPermission(String permission) {
        return table.connector.businessAdministratorPermissions.hasPermission(this, permission);
    }
}
