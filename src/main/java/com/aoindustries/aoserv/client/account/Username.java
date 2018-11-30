/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.account;

import com.aoindustries.aoserv.client.CachedObjectUserIdKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Disablable;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.LinuxAccount;
import com.aoindustries.aoserv.client.linux.LinuxAccountType;
import com.aoindustries.aoserv.client.linux.LinuxGroup;
import com.aoindustries.aoserv.client.linux.Shell;
import com.aoindustries.aoserv.client.mysql.MySQLUser;
import com.aoindustries.aoserv.client.password.PasswordChecker;
import com.aoindustries.aoserv.client.password.PasswordProtected;
import com.aoindustries.aoserv.client.postgresql.PostgresUser;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.Gecos;
import com.aoindustries.aoserv.client.validator.GroupId;
import com.aoindustries.aoserv.client.validator.MySQLUserId;
import com.aoindustries.aoserv.client.validator.PostgresUserId;
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.aoserv.client.validator.UserId;
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
final public class Username extends CachedObjectUserIdKey<Username> implements PasswordProtected, Removable, Disablable {

	static final int
		COLUMN_USERNAME=0,
		COLUMN_PACKAGE=1
	;
	static final String COLUMN_USERNAME_name = "username";

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
		table.getConnector().getBusinessAdministrators().addBusinessAdministrator(
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
		addLinuxAccount(
			primaryGroup.getName(),
			name,
			office_location,
			office_phone,
			home_phone,
			typeObject.getName(),
			shellObject.getPath()
		);
	}

	public void addLinuxAccount(
		GroupId primaryGroup,
		Gecos name,
		Gecos office_location,
		Gecos office_phone,
		Gecos home_phone,
		String type,
		UnixPath shell
	) throws IOException, SQLException {
		table.getConnector().getLinuxAccounts().addLinuxAccount(
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
		try {
			table.getConnector().getMysqlUsers().addMySQLUser(
				MySQLUserId.valueOf(pkey.toString())
			);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	public void addPostgresUser() throws IOException, SQLException {
		try {
			table.getConnector().getPostgresUsers().addPostgresUser(
				PostgresUserId.valueOf(pkey.toString())
			);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
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
		if(la!=null && !la.isDisabled()) return false;
		MySQLUser mu=getMySQLUser();
		if(mu!=null && !mu.isDisabled()) return false;
		PostgresUser pu=getPostgresUser();
		if(pu!=null && !pu.isDisabled()) return false;
		return true;
	}

	@Override
	public boolean canEnable() throws SQLException, IOException {
		DisableLog dl=getDisableLog();
		if(dl==null) return false;
		else return dl.canEnable() && !getPackage().isDisabled();
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
		table.getConnector().requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.USERNAMES, dl.getPkey(), pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.USERNAMES, pkey);
	}

	public BusinessAdministrator getBusinessAdministrator() throws IOException, SQLException {
		return table.getConnector().getBusinessAdministrators().get(pkey);
	}

	@Override
	protected Object getColumnImpl(int i) {
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
		DisableLog obj=table.getConnector().getDisableLogs().get(disable_log);
		if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
		return obj;
	}

	public LinuxAccount getLinuxAccount() throws IOException, SQLException {
		return table.getConnector().getLinuxAccounts().get(pkey);
	}

	public MySQLUser getMySQLUser() throws IOException, SQLException {
		String username = pkey.toString();
		if(MySQLUserId.validate(username).isValid()) {
			try {
				return table.getConnector().getMysqlUsers().get(MySQLUserId.valueOf(username));
			} catch(ValidationException e) {
				throw new AssertionError("Already validated", e);
			}
		} else {
			return null;
		}
	}

	public AccountingCode getPackage_name() {
		return packageName;
	}

	public Package getPackage() throws SQLException, IOException {
		Package packageObject=table.getConnector().getPackages().get(packageName);
		if (packageObject == null) throw new SQLException("Unable to find Package: " + packageName);
		return packageObject;
	}

	public PostgresUser getPostgresUser() throws IOException, SQLException {
		String username = pkey.toString();
		if(PostgresUserId.validate(username).isValid()) {
			try {
				return table.getConnector().getPostgresUsers().get(PostgresUserId.valueOf(username));
			} catch(ValidationException e) {
				throw new AssertionError("Already validated", e);
			}
		} else {
			return null;
		}
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.USERNAMES;
	}

	public UserId getUsername() {
		return pkey;
	}

	public static int groupPasswordsSet(List<? extends PasswordProtected> pps) throws IOException, SQLException {
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
			pkey = UserId.valueOf(result.getString(1));
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

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = UserId.valueOf(in.readUTF()).intern();
			packageName = AccountingCode.valueOf(in.readUTF()).intern();
			disable_log=in.readCompressedInt();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() throws SQLException, IOException {
		List<CannotRemoveReason<?>> reasons = new ArrayList<>();

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
		table.getConnector().requestUpdateIL(
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
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey.toString());
		out.writeUTF(packageName.toString());
		out.writeCompressedInt(disable_log);
	}
}
