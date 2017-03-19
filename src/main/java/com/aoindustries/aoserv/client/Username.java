/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2000-2013, 2016, 2017  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client;

import static com.aoindustries.aoserv.client.ApplicationResources.accessor;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.Gecos;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Each <code>Username</code> is unique across all systems and must
 * be allocated to a <code>Package</code> before use in any of the
 * account types.
 *
 * @see  BusinessAdministrator
 * @see  LinuxAccount
 * @see  MySQLUser
 * @see  PostgresUser
 *
 * @author  AO Industries, Inc.
 */
final public class Username extends CachedObjectStringKey<Username> implements PasswordProtected, Removable, Disablable {

	static final int
		COLUMN_USERNAME=0,
		COLUMN_PACKAGE=1
	;
	static final String COLUMN_USERNAME_name = "username";

	public static final int MAX_LENGTH = 255;

	AccountingCode packageName;
	int disable_log;

	public void addBusinessAdministrator(
		String name,
		String title,
		Date birthday,
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
		table.connector.getBusinessAdministrators().addBusinessAdministrator(
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
		Gecos name,
		Gecos office_location,
		Gecos office_phone,
		Gecos home_phone,
		LinuxAccountType typeObject,
		Shell shellObject
	) throws IOException, SQLException {
		addLinuxAccount(primaryGroup.getName(), name, office_location, office_phone, home_phone, typeObject.pkey, shellObject.pkey);
	}

	public void addLinuxAccount(
		String primaryGroup,
		Gecos name,
		Gecos office_location,
		Gecos office_phone,
		Gecos home_phone,
		String type,
		String shell
	) throws IOException, SQLException {
		table.connector.getLinuxAccounts().addLinuxAccount(
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

	public void addMySQLUser() throws IOException, SQLException {
		table.connector.getMysqlUsers().addMySQLUser(pkey);
	}

	public void addPostgresUser() throws IOException, SQLException {
		table.connector.getPostgresUsers().addPostgresUser(pkey);
	}

	@Override
	public int arePasswordsSet() throws IOException, SQLException {
		// Build the array of objects
		List<PasswordProtected> pps=new ArrayList<>();
		BusinessAdministrator ba=getBusinessAdministrator();
		if(ba!=null) pps.add(ba);
		LinuxAccount la=getLinuxAccount();
		if(la!=null) pps.add(la);
		MySQLUser mu=getMySQLUser();
		if(mu!=null) pps.add(mu);
		PostgresUser pu=getPostgresUser();
		if(pu!=null) pps.add(pu);
		return Username.groupPasswordsSet(pps);
	}

	@Override
	public boolean canDisable() throws IOException, SQLException {
		if(disable_log!=-1) return false;
		LinuxAccount la=getLinuxAccount();
		if(la!=null && la.disable_log==-1) return false;
		MySQLUser mu=getMySQLUser();
		if(mu!=null && mu.disable_log==-1) return false;
		PostgresUser pu=getPostgresUser();
		if(pu!=null && pu.disable_log==-1) return false;
		return true;
	}

	@Override
	public boolean canEnable() throws SQLException, IOException {
		DisableLog dl=getDisableLog();
		if(dl==null) return false;
		else return dl.canEnable() && getPackage().disable_log==-1;
	}

	/**
	 * Checks the strength of a password as used by this <code>Username</code>.
	 */
	@Override
	public List<PasswordChecker.Result> checkPassword(String password) throws IOException, SQLException {
		BusinessAdministrator ba=getBusinessAdministrator();
		if(ba!=null) {
			List<PasswordChecker.Result> results=ba.checkPassword(password);
			if(PasswordChecker.hasResults(results)) return results;
		}

		LinuxAccount la=getLinuxAccount();
		if(la!=null) {
			List<PasswordChecker.Result> results=la.checkPassword(password);
			if(PasswordChecker.hasResults(results)) return results;
		}

		MySQLUser mu=getMySQLUser();
		if(mu!=null) {
			List<PasswordChecker.Result> results=mu.checkPassword(password);
			if(PasswordChecker.hasResults(results)) return results;
		}

		PostgresUser pu=getPostgresUser();
		if(pu!=null) {
			List<PasswordChecker.Result> results=pu.checkPassword(password);
			if(PasswordChecker.hasResults(results)) return results;
		}

		return PasswordChecker.getAllGoodResults();
	}

	@Override
	public void disable(DisableLog dl) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.USERNAMES, dl.pkey, pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.USERNAMES, pkey);
	}

	public BusinessAdministrator getBusinessAdministrator() throws IOException, SQLException {
		return table.connector.getBusinessAdministrators().get(pkey);
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_USERNAME: return pkey;
			case COLUMN_PACKAGE: return packageName;
			case 2: return disable_log==-1?null:disable_log;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	@Override
	public boolean isDisabled() {
		return disable_log!=-1;
	}

	@Override
	public DisableLog getDisableLog() throws SQLException, IOException {
		if(disable_log==-1) return null;
		DisableLog obj=table.connector.getDisableLogs().get(disable_log);
		if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
		return obj;
	}

	public LinuxAccount getLinuxAccount() throws IOException, SQLException {
		return table.connector.getLinuxAccounts().get(pkey);
	}

	public MySQLUser getMySQLUser() throws IOException, SQLException {
		return table.connector.getMysqlUsers().get(pkey);
	}

	public Package getPackage() throws SQLException, IOException {
		Package packageObject=table.connector.getPackages().get(packageName);
		if (packageObject == null) throw new SQLException("Unable to find Package: " + packageName);
		return packageObject;
	}

	public PostgresUser getPostgresUser() throws IOException, SQLException {
		return table.connector.getPostgresUsers().get(pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.USERNAMES;
	}

	public String getUsername() {
		return pkey;
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

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = result.getString(1);
			packageName = AccountingCode.valueOf(result.getString(2));
			disable_log=result.getInt(3);
			if(result.wasNull()) disable_log=-1;
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	public boolean isUsed() throws IOException, SQLException {
		return
			getLinuxAccount()!=null
			|| getBusinessAdministrator()!=null
			|| getMySQLUser()!=null
			|| getPostgresUser()!=null
		;
	}

	/**
	 * Determines if a name can be used as a username.  A name is valid if
	 * it is between 1 and 255 characters in length and uses only ASCII 0x21
	 * through 0x7f, excluding the following characters:
	 * <code>space , : ( ) [ ] ' " | & ; A-Z \ /</code>
	 *
	 * @return  <code>null</code> if the username is valid or a locale-specific reason why it is not valid
	 */
	public static String checkUsername(String username) {
		int len = username.length();
		if(len==0) return accessor.getMessage("Username.checkUsername.noUsername");
		if(len > MAX_LENGTH) return accessor.getMessage("Username.checkUsername.tooLong");

		// The first character must be [a-z]
		char ch = username.charAt(0);
		if (ch < 'a' || ch > 'z') return accessor.getMessage("Username.checkUsername.startAToZ");

		// The rest may have additional characters
		for (int c = 1; c < len; c++) {
			ch = username.charAt(c);
			if(ch==' ') return accessor.getMessage("Username.checkUsername.noSpace");
			if(ch<=0x21 || ch>0x7f) return accessor.getMessage("Username.checkUsername.specialCharacter");
			if(ch>='A' && ch<='Z') return accessor.getMessage("Username.checkUsername.noCapital");
			if(ch==',') return accessor.getMessage("Username.checkUsername.comma");
			if(ch==':') return accessor.getMessage("Username.checkUsername.colon");
			if(ch=='(') return accessor.getMessage("Username.checkUsername.leftParen");
			if(ch==')') return accessor.getMessage("Username.checkUsername.rightParen");
			if(ch=='[') return accessor.getMessage("Username.checkUsername.leftSquare");
			if(ch==']') return accessor.getMessage("Username.checkUsername.rightSquare");
			if(ch=='\'') return accessor.getMessage("Username.checkUsername.apostrophe");
			if(ch=='"') return accessor.getMessage("Username.checkUsername.quote");
			if(ch=='|') return accessor.getMessage("Username.checkUsername.verticalBar");
			if(ch=='&') return accessor.getMessage("Username.checkUsername.ampersand");
			if(ch==';') return accessor.getMessage("Username.checkUsername.semicolon");
			if(ch=='\\') return accessor.getMessage("Username.checkUsername.backslash");
			if(ch=='/') return accessor.getMessage("Username.checkUsername.slash");
		}

		// More strict at sign control is required for user@domain structure in Cyrus virtdomains.
		int atPos = username.indexOf('@');
		if(atPos!=-1) {
			if(atPos==0) return accessor.getMessage("Username.checkUsername.startWithAt");
			if(atPos==(len-1)) return accessor.getMessage("Username.checkUsername.endWithAt");
			int atPos2 = username.indexOf('@', atPos+1);
			if(atPos2!=-1) return accessor.getMessage("Username.checkUsername.onlyOneAt");
			if(username.startsWith("cyrus@")) return accessor.getMessage("Username.checkUsername.startWithCyrusAt");
			if(username.endsWith("@default")) return accessor.getMessage("Username.checkUsername.endWithAtDefault");
		}

		return null;
	}

	/**
	 * Determines if a name can be used as a username.  A name is valid if
	 * it is between 1 and 255 characters in length and uses only ASCII 0x21
	 * through 0x7f, excluding the following characters:
	 * <code>space , : ( ) [ ] ' " | & ; A-Z \ /</code>
	 *
	 * @deprecated  Please use <code>checkUsername(String)</code> instead to provide user with specific problems.
	 */
	public static boolean isValidUsername(String username) {
		return checkUsername(username)==null;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey=in.readUTF().intern();
			packageName = AccountingCode.valueOf(in.readUTF()).intern();
			disable_log=in.readCompressedInt();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<CannotRemoveReason> getCannotRemoveReasons() throws SQLException, IOException {
		List<CannotRemoveReason> reasons=new ArrayList<>();

		LinuxAccount la=getLinuxAccount();
		if(la!=null) reasons.add(new CannotRemoveReason<>("Used by Linux account: "+la.getUsername().getUsername(), la));
		BusinessAdministrator ba=getBusinessAdministrator();
		if(ba!=null) reasons.add(new CannotRemoveReason<>("Used by Business Administrator: "+ba.getUsername().getUsername(), ba));
		MySQLUser mu=getMySQLUser();
		if(mu!=null) reasons.add(new CannotRemoveReason<>("Used by MySQL user: "+mu.getUsername().getUsername(), mu));
		PostgresUser pu=getPostgresUser();
		if(pu!=null) reasons.add(new CannotRemoveReason<>("Used by PostgreSQL user: "+pu.getUsername().getUsername(), pu));

		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.REMOVE,
			SchemaTable.TableID.USERNAMES,
			pkey
		);
	}

	@Override
	public void setPassword(String password) throws SQLException, IOException {
		BusinessAdministrator ba=getBusinessAdministrator();
		if(ba!=null) ba.setPassword(password);

		LinuxAccount la=getLinuxAccount();
		if(la!=null) la.setPassword(password);

		MySQLUser mu=getMySQLUser();
		if(mu!=null) mu.setPassword(password);

		PostgresUser pu=getPostgresUser();
		if(pu!=null) pu.setPassword(password);
	}

	@Override
	public boolean canSetPassword() throws IOException, SQLException {
		if(disable_log!=-1) return false;

		BusinessAdministrator ba=getBusinessAdministrator();
		if(ba!=null && !ba.canSetPassword()) return false;

		LinuxAccount la=getLinuxAccount();
		if(la!=null && !la.canSetPassword()) return false;

		MySQLUser mu=getMySQLUser();
		if(mu!=null && !mu.canSetPassword()) return false;

		PostgresUser pu=getPostgresUser();
		if(pu!=null && !pu.canSetPassword()) return false;

		return ba!=null || la!=null || mu!=null || pu!=null;
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey);
		out.writeUTF(packageName.toString());
		out.writeCompressedInt(disable_log);
	}
}
