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

import com.aoindustries.aoserv.client.validator.Gecos;
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.lang.ObjectUtils;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * One user may have shell, FTP, and/or email access to any number
 * of servers.  However, some of the information is common across
 * all machines, and that set of information is contained in a
 * <code>LinuxAccount</code>.
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxAccount extends CachedObjectUserIdKey<LinuxAccount> implements PasswordProtected, Removable, Disablable {

	static final int COLUMN_USERNAME=0;
	static final String COLUMN_USERNAME_name = "username";

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
		INTERBASE,
		MAIL,
		NOBODY,
		OPERATOR,
		POSTGRES,
		ROOT
	;

	/**
	 * @deprecated  User httpd no longer used.
	 */
	@Deprecated
	public static final UserId HTTPD;
	static {
		try {
			APACHE = UserId.valueOf("apache");
			AWSTATS = UserId.valueOf("awstats");
			BIN = UserId.valueOf("bin");
			CYRUS = UserId.valueOf("cyrus");
			EMAILMON = UserId.valueOf("emailmon");
			FTP = UserId.valueOf("ftp");
			FTPMON = UserId.valueOf("ftpmon");
			INTERBASE = UserId.valueOf("interbase");
			MAIL = UserId.valueOf("mail");
			NOBODY = UserId.valueOf("nobody");
			OPERATOR = UserId.valueOf("operator");
			POSTGRES = UserId.valueOf("postgres");
			ROOT = UserId.valueOf("root");
			// Now unused
			HTTPD = UserId.valueOf("httpd");
		} catch(ValidationException e) {
			throw new AssertionError("These hard-coded values are valid", e);
		}
	}

	public static final String NO_PASSWORD_CONFIG_VALUE="!!";

	/**
	 * The max value for automatic uid selection in useradd.
	 *
	 * @see  AOServer#getUidMin()
	 */
	public static final int UID_MAX = 60000;

	private Gecos name;
	private Gecos office_location;
	private Gecos office_phone;
	private Gecos home_phone;
	private String type;
	private UnixPath shell;
	private long created;
	int disable_log;

	public void addFTPGuestUser() throws IOException, SQLException {
		table.connector.getFtpGuestUsers().addFTPGuestUser(pkey);
	}

	public void addLinuxGroup(LinuxGroup group) throws IOException, SQLException {
		table.connector.getLinuxGroupAccounts().addLinuxGroupAccount(group, this);
	}

	public int addLinuxServerAccount(AOServer aoServer, UnixPath home) throws IOException, SQLException {
		return table.connector.getLinuxServerAccounts().addLinuxServerAccount(this, aoServer, home);
	}

	@Override
	public int arePasswordsSet() throws IOException, SQLException {
		return Username.groupPasswordsSet(getLinuxServerAccounts());
	}

	@Override
	public boolean canDisable() throws IOException, SQLException {
		// Already disabled
		if(disable_log!=-1) return false;

		// linux_server_accounts
		for(LinuxServerAccount lsa : getLinuxServerAccounts()) if(lsa.disable_log==-1) return false;

		return true;
	}

	@Override
	public boolean canEnable() throws SQLException, IOException {
		DisableLog dl=getDisableLog();
		if(dl==null) return false;
		else return dl.canEnable() && getUsername().disable_log==-1;
	}

	@Override
	public List<PasswordChecker.Result> checkPassword(String password) throws IOException {
		return checkPassword(pkey, type, password);
	}

	/**
	 * Checks the strength of a password as required for this
	 * <code>LinuxAccount</code>.  The strength requirement
	 * depends on the <code>LinuxAccountType</code>.
	 *
	 * @see  LinuxAccountType#enforceStrongPassword(String)
	 * @see  PasswordChecker#checkPassword(String,String,boolean,boolean)
	 */
	public static List<PasswordChecker.Result> checkPassword(UserId username, String type, String password) throws IOException {
		return PasswordChecker.checkPassword(username, password, LinuxAccountType.getPasswordStrength(type));
	}

	@Override
	public void disable(DisableLog dl) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.LINUX_ACCOUNTS, dl.pkey, pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.LINUX_ACCOUNTS, pkey);
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_USERNAME: return pkey;
			case 1: return name;
			case 2: return office_location;
			case 3: return office_phone;
			case 4: return home_phone;
			case 5: return type;
			case 6: return shell;
			case 7: return getCreated();
			case 8: return disable_log==-1?null:disable_log;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public Timestamp getCreated() {
		return new Timestamp(created);
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

	public FTPGuestUser getFTPGuestUser() throws IOException, SQLException {
		return table.connector.getFtpGuestUsers().get(pkey);
	}

	public Gecos getHomePhone() {
		return home_phone;
	}

	public List<LinuxGroup> getLinuxGroups() throws IOException, SQLException {
		return table.connector.getLinuxGroupAccounts().getLinuxGroups(this);
	}

	public LinuxServerAccount getLinuxServerAccount(AOServer aoServer) throws IOException, SQLException {
		return table.connector.getLinuxServerAccounts().getLinuxServerAccount(aoServer, pkey);
	}

	public List<LinuxServerAccount> getLinuxServerAccounts() throws IOException, SQLException {
		return table.connector.getLinuxServerAccounts().getLinuxServerAccounts(this);
	}

	public Gecos getName() {
		return name;
	}

	public Gecos getOfficeLocation() {
		return office_location;
	}

	public Gecos getOfficePhone() {
		return office_phone;
	}

	public LinuxGroup getPrimaryGroup() throws IOException, SQLException {
		return table.connector.getLinuxGroupAccounts().getPrimaryGroup(this);
	}

	public Shell getShell() throws SQLException, IOException {
		Shell shellObject = table.connector.getShells().get(shell);
		if (shellObject == null) throw new SQLException("Unable to find Shell: " + shell);
		return shellObject;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.LINUX_ACCOUNTS;
	}

	public LinuxAccountType getType() throws IOException, SQLException {
		LinuxAccountType typeObject = table.connector.getLinuxAccountTypes().get(type);
		if (typeObject == null) throw new IllegalArgumentException(new SQLException("Unable to find LinuxAccountType: " + type));
		return typeObject;
	}

	public Username getUsername() throws SQLException, IOException {
		Username usernameObject = table.connector.getUsernames().get(pkey);
		if (usernameObject == null) throw new SQLException("Unable to find Username: " + pkey);
		return usernameObject;
	}

	public List<UnixPath> getValidHomeDirectories(AOServer ao) throws SQLException, IOException {
		return getValidHomeDirectories(pkey, ao);
	}

	public static List<UnixPath> getValidHomeDirectories(UserId username, AOServer ao) throws SQLException, IOException {
		try {
			List<UnixPath> dirs=new ArrayList<>();
			if(username!=null) dirs.add(LinuxServerAccount.getDefaultHomeDirectory(username));

			List<HttpdSite> hss=ao.getHttpdSites();
			int hsslen=hss.size();
			for(int c=0;c<hsslen;c++) {
				HttpdSite hs=hss.get(c);
				UnixPath siteDir=hs.getInstallDirectory();
				dirs.add(siteDir);
				if(hs.getHttpdTomcatSite()!=null) {
					dirs.add(UnixPath.valueOf(siteDir.toString() + "/webapps"));
				}
			}

			List<HttpdSharedTomcat> hsts=ao.getHttpdSharedTomcats();
			int hstslen=hsts.size();
			for(int c=0;c<hstslen;c++) {
				HttpdSharedTomcat hst=hsts.get(c);
				dirs.add(
					UnixPath.valueOf(
						hst.getAOServer().getServer().getOperatingSystemVersion().getHttpdSharedTomcatsDirectory().toString()
						+ '/' + hst.getName()
					)
				);
			}
			return dirs;
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = UserId.valueOf(result.getString(1));
			name = Gecos.valueOf(result.getString(2));
			office_location = Gecos.valueOf(result.getString(3));
			office_phone = Gecos.valueOf(result.getString(4));
			home_phone = Gecos.valueOf(result.getString(5));
			type = result.getString(6);
			shell = UnixPath.valueOf(result.getString(7));
			created = result.getTimestamp(8).getTime();
			disable_log=result.getInt(9);
			if(result.wasNull()) disable_log=-1;
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = UserId.valueOf(in.readUTF()).intern();
			name = Gecos.valueOf(in.readNullUTF());
			office_location = Gecos.valueOf(in.readNullUTF());
			office_phone = Gecos.valueOf(in.readNullUTF());
			home_phone = Gecos.valueOf(in.readNullUTF());
			type = in.readUTF().intern();
			shell = UnixPath.valueOf(in.readUTF()).intern();
			created = in.readLong();
			disable_log = in.readCompressedInt();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() throws SQLException, IOException {
		List<CannotRemoveReason<?>> reasons=new ArrayList<>();

		// All LinuxServerAccounts must be removable
		for(LinuxServerAccount lsa : getLinuxServerAccounts()) {
			reasons.addAll(lsa.getCannotRemoveReasons());
		}

		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.REMOVE,
			SchemaTable.TableID.LINUX_ACCOUNTS,
			pkey
		);
	}

	public void removeLinuxGroup(LinuxGroup group) throws IOException, SQLException {
		table.connector.getLinuxGroupAccounts().getLinuxGroupAccount(group.pkey, pkey).remove();
	}

	public void setHomePhone(Gecos phone) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_ACCOUNT_HOME_PHONE, pkey, phone==null?"":phone.toString());
	}

	public void setName(Gecos name) throws IOException, SQLException {
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.SET_LINUX_ACCOUNT_NAME,
			pkey,
			name==null ? "" : name.toString()
		);
	}

	public void setOfficeLocation(Gecos location) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_ACCOUNT_OFFICE_LOCATION, pkey, location==null?"":location.toString());
	}

	public void setOfficePhone(Gecos phone) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_ACCOUNT_OFFICE_PHONE, pkey, phone==null?"":phone.toString());
	}

	@Override
	public void setPassword(String password) throws SQLException, IOException {
		for(LinuxServerAccount lsa : getLinuxServerAccounts()) {
			if(lsa.canSetPassword()) lsa.setPassword(password);
		}
	}

	public void setShell(Shell shell) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_ACCOUNT_SHELL, pkey, shell.pkey);
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey.toString());
		if(version.compareTo(AOServProtocol.Version.VERSION_1_80_1_SNAPSHOT) < 0) {
			// Older clients require name, use "*" as name when none set
			out.writeUTF(name==null ? "*" : name.toString());
		} else {
			out.writeNullUTF(ObjectUtils.toString(name));
		}
		out.writeNullUTF(ObjectUtils.toString(office_location));
		out.writeNullUTF(ObjectUtils.toString(office_phone));
		out.writeNullUTF(ObjectUtils.toString(home_phone));
		out.writeUTF(type);
		out.writeUTF(shell.toString());
		out.writeLong(created);
		out.writeCompressedInt(disable_log);
	}

	@Override
	public boolean canSetPassword() throws IOException, SQLException {
		return disable_log==-1 && getType().canSetPassword();
	}

	public void setPrimaryLinuxGroup(LinuxGroup group) throws SQLException, IOException {
		LinuxGroupAccount lga=table.connector.getLinuxGroupAccounts().getLinuxGroupAccount(group.getName(), pkey);
		if(lga==null) throw new SQLException("Unable to find LinuxGroupAccount for username="+pkey+" and group="+group.getName());
		lga.setAsPrimary();
	}
}
