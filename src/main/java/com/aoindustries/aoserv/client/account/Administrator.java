/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoindustries.aoserv.client.account;

import com.aoapps.collections.IntList;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.util.InternUtils;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.Email;
import com.aoapps.security.HashedPassword;
import com.aoapps.security.SecurityStreamables;
import com.aoapps.sql.SQLStreamables;
import com.aoapps.sql.UnmodifiableTimestamp;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Disablable;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.billing.MonthlyCharge;
import com.aoindustries.aoserv.client.billing.Transaction;
import com.aoindustries.aoserv.client.billing.TransactionTable;
import com.aoindustries.aoserv.client.master.AdministratorPermission;
import com.aoindustries.aoserv.client.master.Permission;
import com.aoindustries.aoserv.client.password.PasswordChecker;
import com.aoindustries.aoserv.client.password.PasswordProtected;
import com.aoindustries.aoserv.client.payment.CountryCode;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.ticket.Action;
import com.aoindustries.aoserv.client.ticket.Assignment;
import com.aoindustries.aoserv.client.ticket.Ticket;
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * An {@link Administrator} is a username and password pair, usually
 * representing an individual or an application, that has administrative control
 * over all resources in an {@link Account} or any any of its child businesses.
 *
 *
 * @see  Account
 *
 * @author  AO Industries, Inc.
 */
public final class Administrator extends CachedObjectUserNameKey<Administrator> implements PasswordProtected, Removable, Disablable, Comparable<Administrator> {

	static final int COLUMN_USERNAME=0;
	static final String COLUMN_USERNAME_name = "username";

	private HashedPassword password;
	private String
		name,
		title
	;
	private long birthday;
	private boolean isPreferred;
	private boolean isPrivate;
	private UnmodifiableTimestamp created;
	private String
		work_phone,
		home_phone,
		cell_phone,
		fax;
	private Email email;
	private String
		address1,
		address2,
		city,
		state,
		country,
		zip
	;
	private int disable_log;
	private boolean can_switch_users;
	private String support_code;

	/**
	 * @deprecated  Only required for implementation, do not use directly.
	 *
	 * @see  #init(java.sql.ResultSet)
	 * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
	 */
	@Deprecated/* Java 9: (forRemoval = true) */
	public Administrator() {
		// Do nothing
	}

	@Override
	public int arePasswordsSet() throws IOException, SQLException {
		return table.getConnector().requestBooleanQuery(
			true,
			AoservProtocol.CommandID.IS_BUSINESS_ADMINISTRATOR_PASSWORD_SET,
			pkey
		) ? PasswordProtected.ALL : PasswordProtected.NONE;
	}

	@Override
	public boolean canDisable() throws SQLException, IOException {
		return disable_log==-1 && !equals(table.getConnector().getCurrentAdministrator());
	}

	public boolean canSwitchUsers() {
		return can_switch_users;
	}

	public String getSupportCode() {
		return support_code;
	}

	public boolean canSwitchUser(Administrator other) throws SQLException, IOException {
		if(isDisabled() || other.isDisabled()) return false;
		Account account = getUsername().getPackage().getAccount();
		Account otherAccount = other.getUsername().getPackage().getAccount();
		return !account.equals(otherAccount) && account.isAccountOrParentOf(otherAccount);
	}

	@Override
	public boolean canEnable() throws SQLException, IOException {
		DisableLog dl=getDisableLog();
		if(dl==null) return false;
		else return dl.canEnable();
	}

	@Override
	public List<PasswordChecker.Result> checkPassword(String password) throws IOException {
		return checkPassword(pkey, password);
	}

	/**
	 * Validates a password and returns a description of the problem.  If the
	 * password is valid, then {@code null} is returned.
	 */
	// TODO: Variant that accepts UnprotectedPassword
	public static List<PasswordChecker.Result> checkPassword(User.Name username, String password) throws IOException {
		return PasswordChecker.checkPassword(username, password, PasswordChecker.PasswordStrength.STRICT);
	}

	/**
	 * Validates a password and returns a description of the problem.  If the
	 * password is valid, then {@code null} is returned.
	 */
	/*public String checkPasswordDescribe(String password) {
	return checkPasswordDescribe(pkey, password);
	}*/

	/**
	 * Validates a password and returns a description of the problem.  If the
	 * password is valid, then {@code null} is returned.
	 */
	/*public static String checkPasswordDescribe(String username, String password) {
	return PasswordChecker.checkPasswordDescribe(username, password, true, false);
	}*/

	@Override
	public void disable(DisableLog dl) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.DISABLE, Table.TableID.BUSINESS_ADMINISTRATORS, dl.getPkey(), pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.ENABLE, Table.TableID.BUSINESS_ADMINISTRATORS, pkey);
	}

	public List<Action> getTicketActions() throws IOException, SQLException {
		return table.getConnector().getTicket().getAction().getActions(this);
	}

	public List<Assignment> getTicketAssignments() throws IOException, SQLException {
		return table.getConnector().getTicket().getAssignment().getTicketAssignments(this);
	}

	public String getAddress1() {
		return address1;
	}

	public String getAddress2() {
		return address2;
	}

	public Date getBirthday() {
		return birthday==-1 ? null : new Date(birthday);
	}

	public String getCellPhone() {
		return cell_phone;
	}

	public String getCity() {
		return city;
	}

	@Override
	@SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_USERNAME: return pkey;
			case 1: return password;
			case 2: return name;
			case 3: return title;
			case 4: return getBirthday();
			case 5: return isPreferred;
			case 6: return isPrivate;
			case 7: return created;
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
			case 19: return disable_log == -1 ? null : disable_log;
			case 20: return can_switch_users;
			case 21: return support_code;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public CountryCode getCountry() throws SQLException, IOException {
		if(country == null) return null;
		CountryCode countryCode=table.getConnector().getPayment().getCountryCode().get(country);
		if (countryCode == null) throw new SQLException("CountryCode not found: " + country);
		return countryCode;
	}

	@SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
	public UnmodifiableTimestamp getCreated() {
		return created;
	}

	public List<Ticket> getCreatedTickets() throws IOException, SQLException {
		return table.getConnector().getTicket().getTicket().getCreatedTickets(this);
	}

	@Override
	public boolean isDisabled() {
		return disable_log!=-1;
	}

	@Override
	public DisableLog getDisableLog() throws SQLException, IOException {
		if(disable_log==-1) return null;
		DisableLog obj=table.getConnector().getAccount().getDisableLog().get(disable_log);
		if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
		return obj;
	}

	public Email getEmail() {
		return email;
	}

	public String getFax() {
		return fax;
	}

	public String getHomePhone() {
		return home_phone;
	}

	public com.aoindustries.aoserv.client.master.User getMasterUser() throws IOException, SQLException {
		return table.getConnector().getMaster().getUser().get(pkey);
	}

	public List<MonthlyCharge> getMonthlyCharges() throws IOException, SQLException {
		return table.getConnector().getBilling().getMonthlyCharge().getMonthlyCharges(this, null, null);
	}

	public String getName() {
		return name;
	}

	/**
	 * Gets the hashed password for this business_administrator.  This information is only
	 * available if all communication has been over secure connections.  Otherwise,
	 * all passwords will be changed to <code>NO_PASSWORD</code>.
	 *
	 * @see  AOServConnector#isSecure
	 */
	public HashedPassword getPassword() {
		return password;
	}

	public String getState() {
		return state;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.BUSINESS_ADMINISTRATORS;
	}

	public String getTitle() {
		return title;
	}

	/**
	 * @deprecated  Please use {@link TransactionTable#getTransactions(com.aoindustries.aoserv.client.account.Administrator)} directly
	 */
	@Deprecated
	public List<Transaction> getTransactions() throws IOException, SQLException {
		return table.getConnector().getBilling().getTransaction().getTransactions(this);
	}

	public User.Name getUsername_userId() {
		return pkey;
	}

	public User getUsername() throws SQLException, IOException {
		User usernameObject = table.getConnector().getAccount().getUser().get(pkey);
		if (usernameObject == null) throw new SQLException("Username not found: " + pkey);
		return usernameObject;
	}

	public String getWorkPhone() {
		return work_phone;
	}

	public String getZIP() {
		return zip;
	}

	public boolean isActiveAccounting() throws IOException, SQLException {
		com.aoindustries.aoserv.client.master.User user=getMasterUser();
		return
			user!=null
			&& user.isActive()
			&& user.canAccessAccounting()
		;
	}

	public boolean isActiveBankAccounting() throws IOException, SQLException {
		com.aoindustries.aoserv.client.master.User user=getMasterUser();
		return
			user!=null
			&& user.isActive()
			&& user.canAccessBankAccount()
		;
	}

	public boolean isActiveDNSAdmin() throws IOException, SQLException {
		com.aoindustries.aoserv.client.master.User user=getMasterUser();
		return
			user!=null
			&& user.isActive()
			&& user.isDNSAdmin()
		;
	}

	public boolean isActiveTableInvalidator() throws IOException, SQLException {
		com.aoindustries.aoserv.client.master.User user=getMasterUser();
		return
			user!=null
			&& user.isActive()
			&& user.canInvalidateTables()
		;
	}

	public boolean isActiveWebAdmin() throws IOException, SQLException {
		com.aoindustries.aoserv.client.master.User user=getMasterUser();
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

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = User.Name.valueOf(result.getString("username"));
			password = HashedPassword.valueOf(
				HashedPassword.Algorithm.findAlgorithm(result.getString("password.algorithm")),
				result.getBytes("password.salt"),
				result.getInt  ("password.iterations"),
				result.getBytes("password.hash")
			);
			name = result.getString("name");
			title = result.getString("title");
			Date d = result.getDate("birthday");
			birthday = (d == null) ? -1 : d.getTime();
			isPreferred = result.getBoolean("is_preferred");
			isPrivate = result.getBoolean("private");
			created = UnmodifiableTimestamp.valueOf(result.getTimestamp("created"));
			work_phone = result.getString("work_phone");
			home_phone = result.getString("home_phone");
			cell_phone = result.getString("cell_phone");
			fax = result.getString("fax");
			email = USE_SQL_DATA ? result.getObject("email", Email.class) : Email.valueOf(result.getString("email"));
			address1 = result.getString("address1");
			address2 = result.getString("address2");
			city = result.getString("city");
			state = result.getString("state");
			country = result.getString("country");
			zip = result.getString("zip");
			disable_log=result.getInt("disable_log");
			if(result.wasNull()) disable_log=-1;
			can_switch_users=result.getBoolean("can_switch_users");
			support_code = result.getString("support_code");
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			pkey = User.Name.valueOf(in.readUTF()).intern();
			password = SecurityStreamables.readHashedPassword(in);
			name = in.readUTF();
			title = in.readNullUTF();
			birthday = in.readLong();
			isPreferred = in.readBoolean();
			isPrivate = in.readBoolean();
			created = SQLStreamables.readUnmodifiableTimestamp(in);
			work_phone = in.readUTF();
			home_phone = in.readNullUTF();
			cell_phone = in.readNullUTF();
			fax = in.readNullUTF();
			email = Email.valueOf(in.readUTF());
			address1 = in.readNullUTF();
			address2 = in.readNullUTF();
			city = in.readNullUTF();
			state = InternUtils.intern(in.readNullUTF());
			country = InternUtils.intern(in.readNullUTF());
			zip = in.readNullUTF();
			disable_log = in.readCompressedInt();
			can_switch_users = in.readBoolean();
			support_code = in.readNullUTF();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() throws SQLException, IOException {
		List<CannotRemoveReason<?>> reasons=new ArrayList<>();

		AOServConnector conn=table.getConnector();

		if(equals(conn.getCurrentAdministrator())) reasons.add(new CannotRemoveReason<>("Not allowed to remove self", this));

		List<Action> actions=getTicketActions();
		if(!actions.isEmpty()) reasons.add(new CannotRemoveReason<>("Author of "+actions.size()+" ticket "+(actions.size()==1?"action":"actions"), actions));

		List<Ticket> tickets=getCreatedTickets();
		if(!tickets.isEmpty()) reasons.add(new CannotRemoveReason<>("Author of "+tickets.size()+' '+(tickets.size()==1?"ticket":"tickets"), tickets));

		List<Transaction> trs=getTransactions();
		if(!trs.isEmpty()) reasons.add(new CannotRemoveReason<>("Created "+trs.size()+' '+(trs.size()==1?"transaction":"transactions"), trs));

		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(
			true,
			AoservProtocol.CommandID.REMOVE,
			Table.TableID.BUSINESS_ADMINISTRATORS,
			pkey
		);
	}

	/**
	 * Sets the password for this {@link Administrator}.  All connections must
	 * be over secure protocols for this method to work.  If the connections
	 * are not secure, an <code>IOException</code> is thrown.
	 */
	@Override
	public void setPassword(String plaintext) throws IOException, SQLException {
		AOServConnector connector=table.getConnector();
		if(!connector.isSecure()) throw new IOException("Passwords for business_administrators may only be set when using secure protocols.  Currently using the "+connector.getProtocol()+" protocol, which is not secure.");
		connector.requestUpdateIL(true, AoservProtocol.CommandID.SET_BUSINESS_ADMINISTRATOR_PASSWORD, pkey, plaintext);
	}

	public void setProfile(
		final String name,
		String title,
		final Date birthday,
		final boolean isPrivate,
		final String workPhone,
		String homePhone,
		String cellPhone,
		String fax,
		final Email email,
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
		table.getConnector().requestUpdate(
			true,
			AoservProtocol.CommandID.SET_BUSINESS_ADMINISTRATOR_PROFILE,
			new AOServConnector.UpdateRequest() {
				private IntList invalidateList;

				@Override
				public void writeRequest(StreamableOutput out) throws IOException {
					out.writeUTF(pkey.toString());
					out.writeUTF(name);
					out.writeBoolean(finalTitle!=null); if(finalTitle!=null) out.writeUTF(finalTitle);
					out.writeLong(birthday==null ? -1 : birthday.getTime());
					out.writeBoolean(isPrivate);
					out.writeUTF(workPhone);
					out.writeBoolean(finalHomePhone!=null); if(finalHomePhone!=null) out.writeUTF(finalHomePhone);
					out.writeBoolean(finalCellPhone!=null); if(finalCellPhone!=null) out.writeUTF(finalCellPhone);
					out.writeBoolean(finalFax!=null); if(finalFax!=null) out.writeUTF(finalFax);
					out.writeUTF(email.toString());
					out.writeBoolean(finalAddress1!=null); if(finalAddress1!=null) out.writeUTF(finalAddress1);
					out.writeBoolean(finalAddress2!=null); if(finalAddress2!=null) out.writeUTF(finalAddress2);
					out.writeBoolean(finalCity!=null); if(finalCity!=null) out.writeUTF(finalCity);
					out.writeBoolean(finalState!=null); if(finalState!=null) out.writeUTF(finalState);
					out.writeBoolean(finalCountry!=null); if(finalCountry!=null) out.writeUTF(finalCountry);
					out.writeBoolean(finalZip!=null); if(finalZip!=null) out.writeUTF(finalZip);
				}

				@Override
				public void readResponse(StreamableInput in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
					else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public void afterRelease() {
					table.getConnector().tablesUpdated(invalidateList);
				}
			}
		);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey.toString());
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_68) <= 0) {
			if(password == null) {
				out.writeUTF(HashedPassword.NO_PASSWORD_VALUE);
			} else {
				HashedPassword.Algorithm algorithm = password.getAlgorithm();
				if(algorithm == HashedPassword.Algorithm.CRYPT || algorithm == HashedPassword.Algorithm.SHA_1) {
					out.writeUTF(password.toString());
				} else {
					// Newer algorithm unknown
					out.writeUTF("*");
				}
			}
		} else if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_2) <= 0) {
			if(password == null) {
				out.writeNullUTF(null);
			} else {
				HashedPassword.Algorithm algorithm = password.getAlgorithm();
				if(algorithm == HashedPassword.Algorithm.CRYPT || algorithm == HashedPassword.Algorithm.SHA_1) {
					out.writeNullUTF(password.toString());
				} else {
					// Newer algorithm unknown
					out.writeNullUTF("*");
				}
			}
		} else {
			SecurityStreamables.writeHashedPassword(password, out);
		}
		out.writeUTF(name);
		out.writeNullUTF(title);
		out.writeLong(birthday);
		out.writeBoolean(isPreferred);
		out.writeBoolean(isPrivate);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
			out.writeLong(created.getTime());
		} else {
			SQLStreamables.writeTimestamp(created, out);
		}
		out.writeUTF(work_phone);
		out.writeNullUTF(home_phone);
		out.writeNullUTF(cell_phone);
		out.writeNullUTF(fax);
		out.writeUTF(email.toString());
		out.writeNullUTF(address1);
		out.writeNullUTF(address2);
		out.writeNullUTF(city);
		out.writeNullUTF(state);
		out.writeNullUTF(country);
		out.writeNullUTF(zip);
		out.writeCompressedInt(disable_log);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_118)>=0) out.writeBoolean(can_switch_users);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_44)>=0) out.writeNullUTF(support_code);
	}

	@Override
	public boolean canSetPassword() {
		return disable_log==-1;
	}

	public List<AdministratorPermission> getPermissions() throws IOException, SQLException {
		return table.getConnector().getMaster().getAdministratorPermission().getPermissions(this);
	}

	/**
	 * Checks if this business administrator has the provided permission.
	 */
	public boolean hasPermission(Permission permission) throws IOException, SQLException {
		return hasPermission(permission.getName());
	}

	/**
	 * Checks if this business administrator has the provided permission.
	 */
	public boolean hasPermission(Permission.Name permission) throws IOException, SQLException {
		return hasPermission(permission.name());
	}

	/**
	 * Checks if this business administrator has the provided permission.
	 */
	public boolean hasPermission(String permission) throws IOException, SQLException {
		return table.getConnector().getMaster().getAdministratorPermission().hasPermission(this, permission);
	}

	/**
	 * Sorts by username.
	 *
	 * TODO: Consider handling comparisons at the AOServTable and making all
	 * AOServObject's comparable.  We could then return things as sets where
	 * appropriate.  Maybe have getMap, getList, getSet, and getSortedSet
	 * as appropriate?
	 */
	@Override
	public int compareTo(Administrator o) {
		return pkey.compareTo(o.pkey);
	}
}
