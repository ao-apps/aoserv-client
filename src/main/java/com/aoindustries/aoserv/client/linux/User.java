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
package com.aoindustries.aoserv.client.linux;

import com.aoindustries.aoserv.client.CachedObjectUserIdKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Disablable;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.account.DisableLog;
import com.aoindustries.aoserv.client.account.Username;
import com.aoindustries.aoserv.client.ftp.GuestUser;
import com.aoindustries.aoserv.client.password.PasswordChecker;
import com.aoindustries.aoserv.client.password.PasswordProtected;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.validator.Gecos;
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.aoserv.client.web.Site;
import com.aoindustries.aoserv.client.web.tomcat.SharedTomcat;
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
final public class User extends CachedObjectUserIdKey<User> implements PasswordProtected, Removable, Disablable {

	static final int COLUMN_USERNAME=0;
	static final String COLUMN_USERNAME_name = "username";

	/**
	 * Some commonly used system and application account usernames.
	 */
	public static final UserId
		ADM,
		AOADMIN,
		AOSERV_JILTER,
		AOSERV_XEN_MIGRATION,
		APACHE,
		AVAHI_AUTOIPD,
		AWSTATS,
		BIN,
		BIRD,
		CHRONY,
		CLAMSCAN,
		CLAMUPDATE,
		CYRUS,
		DAEMON,
		DBUS,
		DHCPD,
		EMAILMON,
		FTP,
		FTPMON,
		GAMES,
		HALT,
		INTERBASE,
		LP,
		MAIL,
		MAILNULL,
		MEMCACHED,
		MYSQL,
		NAMED,
		NFSNOBODY,
		NGINX,
		NOBODY,
		OPERATOR,
		POLKITD,
		POSTGRES,
		ROOT,
		RPC,
		RPCUSER,
		SASLAUTH,
		SHUTDOWN,
		SMMSP,
		SSHD,
		SYNC,
		SYSTEMD_BUS_PROXY,
		SYSTEMD_NETWORK,
		TCPDUMP,
		TSS,
		UNBOUND
	;

	/**
	 * @deprecated  User httpd no longer used.
	 */
	@Deprecated
	public static final UserId HTTPD;
	static {
		try {
			ADM = UserId.valueOf("adm");
			AOADMIN = UserId.valueOf("aoadmin");
			AOSERV_JILTER = UserId.valueOf("aoserv-jilter");
			AOSERV_XEN_MIGRATION = UserId.valueOf("aoserv-xen-migration");
			APACHE = UserId.valueOf("apache");
			AVAHI_AUTOIPD = UserId.valueOf("avahi-autoipd");
			AWSTATS = UserId.valueOf("awstats");
			BIN = UserId.valueOf("bin");
			BIRD = UserId.valueOf("bird");
			CHRONY = UserId.valueOf("chrony");
			CLAMSCAN = UserId.valueOf("clamscan");
			CLAMUPDATE = UserId.valueOf("clamupdate");
			CYRUS = UserId.valueOf("cyrus");
			DAEMON = UserId.valueOf("daemon");
			DBUS = UserId.valueOf("dbus");
			DHCPD = UserId.valueOf("dhcpd");
			EMAILMON = UserId.valueOf("emailmon");
			FTP = UserId.valueOf("ftp");
			FTPMON = UserId.valueOf("ftpmon");
			GAMES = UserId.valueOf("games");
			HALT = UserId.valueOf("halt");
			INTERBASE = UserId.valueOf("interbase");
			LP = UserId.valueOf("lp");
			MAIL = UserId.valueOf("mail");
			MAILNULL = UserId.valueOf("mailnull");
			MEMCACHED = UserId.valueOf("memcached");
			MYSQL = UserId.valueOf("mysql");
			NAMED = UserId.valueOf("named");
			NFSNOBODY = UserId.valueOf("nfsnobody");
			NGINX = UserId.valueOf("nginx");
			NOBODY = UserId.valueOf("nobody");
			OPERATOR = UserId.valueOf("operator");
			POLKITD = UserId.valueOf("polkitd");
			POSTGRES = UserId.valueOf("postgres");
			ROOT = UserId.valueOf("root");
			RPC = UserId.valueOf("rpc");
			RPCUSER = UserId.valueOf("rpcuser");
			SASLAUTH = UserId.valueOf("saslauth");
			SHUTDOWN = UserId.valueOf("shutdown");
			SMMSP = UserId.valueOf("smmsp");
			SSHD = UserId.valueOf("sshd");
			SYNC = UserId.valueOf("sync");
			SYSTEMD_BUS_PROXY = UserId.valueOf("systemd-bus-proxy");
			SYSTEMD_NETWORK = UserId.valueOf("systemd-network");
			TCPDUMP = UserId.valueOf("tcpdump");
			TSS = UserId.valueOf("tss");
			UNBOUND = UserId.valueOf("unbound");
			// Now unused
			HTTPD = UserId.valueOf("httpd");
		} catch(ValidationException e) {
			throw new AssertionError("These hard-coded values are valid", e);
		}
	}

	public static final String NO_PASSWORD_CONFIG_VALUE="!!";

	private Gecos name;
	private Gecos office_location;
	private Gecos office_phone;
	private Gecos home_phone;
	private String type;
	private UnixPath shell;
	private long created;
	int disable_log;

	public void addFTPGuestUser() throws IOException, SQLException {
		table.getConnector().getFtp().getFtpGuestUsers().addFTPGuestUser(pkey);
	}

	public void addLinuxGroup(Group group) throws IOException, SQLException {
		table.getConnector().getLinux().getLinuxGroupAccounts().addLinuxGroupAccount(group, this);
	}

	public int addLinuxServerAccount(Server aoServer, UnixPath home) throws IOException, SQLException {
		return table.getConnector().getLinux().getLinuxServerAccounts().addLinuxServerAccount(this, aoServer, home);
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
		for(UserServer lsa : getLinuxServerAccounts()) if(!lsa.isDisabled()) return false;

		return true;
	}

	@Override
	public boolean canEnable() throws SQLException, IOException {
		DisableLog dl=getDisableLog();
		if(dl==null) return false;
		else return dl.canEnable() && !getUsername().isDisabled();
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
		return PasswordChecker.checkPassword(username, password, UserType.getPasswordStrength(type));
	}

	@Override
	public void disable(DisableLog dl) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.DISABLE, Table.TableID.LINUX_ACCOUNTS, dl.getPkey(), pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.ENABLE, Table.TableID.LINUX_ACCOUNTS, pkey);
	}

	@Override
	protected Object getColumnImpl(int i) {
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
		DisableLog obj=table.getConnector().getAccount().getDisableLogs().get(disable_log);
		if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
		return obj;
	}

	public GuestUser getFTPGuestUser() throws IOException, SQLException {
		return table.getConnector().getFtp().getFtpGuestUsers().get(pkey);
	}

	public Gecos getHomePhone() {
		return home_phone;
	}

	public List<Group> getLinuxGroups() throws IOException, SQLException {
		return table.getConnector().getLinux().getLinuxGroupAccounts().getLinuxGroups(this);
	}

	public UserServer getLinuxServerAccount(Server aoServer) throws IOException, SQLException {
		return table.getConnector().getLinux().getLinuxServerAccounts().getLinuxServerAccount(aoServer, pkey);
	}

	public List<UserServer> getLinuxServerAccounts() throws IOException, SQLException {
		return table.getConnector().getLinux().getLinuxServerAccounts().getLinuxServerAccounts(this);
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

	public Group getPrimaryGroup() throws IOException, SQLException {
		return table.getConnector().getLinux().getLinuxGroupAccounts().getPrimaryGroup(this);
	}

	public Shell getShell() throws SQLException, IOException {
		Shell shellObject = table.getConnector().getLinux().getShells().get(shell);
		if (shellObject == null) throw new SQLException("Unable to find Shell: " + shell);
		return shellObject;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.LINUX_ACCOUNTS;
	}

	public UserType getType() throws IOException, SQLException {
		UserType typeObject = table.getConnector().getLinux().getLinuxAccountTypes().get(type);
		if (typeObject == null) throw new IllegalArgumentException(new SQLException("Unable to find LinuxAccountType: " + type));
		return typeObject;
	}

	public UserId getUsername_id() {
		return pkey;
	}

	public Username getUsername() throws SQLException, IOException {
		Username usernameObject = table.getConnector().getAccount().getUsernames().get(pkey);
		if (usernameObject == null) throw new SQLException("Unable to find Username: " + pkey);
		return usernameObject;
	}

	public List<UnixPath> getValidHomeDirectories(Server ao) throws SQLException, IOException {
		return getValidHomeDirectories(pkey, ao);
	}

	public static List<UnixPath> getValidHomeDirectories(UserId username, Server ao) throws SQLException, IOException {
		try {
			List<UnixPath> dirs=new ArrayList<>();
			if(username != null) {
				dirs.add(UserServer.getDefaultHomeDirectory(username));
				dirs.add(UserServer.getHashedHomeDirectory(username));
			}
			List<Site> hss=ao.getHttpdSites();
			int hsslen=hss.size();
			for(int c=0;c<hsslen;c++) {
				Site hs=hss.get(c);
				UnixPath siteDir=hs.getInstallDirectory();
				dirs.add(siteDir);
				if(hs.getHttpdTomcatSite()!=null) {
					dirs.add(UnixPath.valueOf(siteDir.toString() + "/webapps"));
				}
			}

			List<SharedTomcat> hsts=ao.getHttpdSharedTomcats();
			int hstslen=hsts.size();
			for(int c=0;c<hstslen;c++) {
				SharedTomcat hst=hsts.get(c);
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
		for(UserServer lsa : getLinuxServerAccounts()) {
			reasons.addAll(lsa.getCannotRemoveReasons());
		}

		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true,
			AoservProtocol.CommandID.REMOVE,
			Table.TableID.LINUX_ACCOUNTS,
			pkey
		);
	}

	public void removeLinuxGroup(Group group) throws IOException, SQLException {
		for(GroupUser lga : table.getConnector().getLinux().getLinuxGroupAccounts().getLinuxGroupAccounts(group.getName(), pkey)) {
			lga.remove();
		}
	}

	public void setHomePhone(Gecos phone) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_LINUX_ACCOUNT_HOME_PHONE, pkey, phone==null?"":phone.toString());
	}

	public void setName(Gecos name) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true,
			AoservProtocol.CommandID.SET_LINUX_ACCOUNT_NAME,
			pkey,
			name==null ? "" : name.toString()
		);
	}

	public void setOfficeLocation(Gecos location) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_LINUX_ACCOUNT_OFFICE_LOCATION, pkey, location==null?"":location.toString());
	}

	public void setOfficePhone(Gecos phone) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_LINUX_ACCOUNT_OFFICE_PHONE, pkey, phone==null?"":phone.toString());
	}

	@Override
	public void setPassword(String password) throws SQLException, IOException {
		for(UserServer lsa : getLinuxServerAccounts()) {
			if(lsa.canSetPassword()) lsa.setPassword(password);
		}
	}

	public void setShell(Shell shell) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_LINUX_ACCOUNT_SHELL, pkey, shell.getPath());
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey.toString());
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_80_1) < 0) {
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

	public void setPrimaryLinuxGroup(Group group) throws SQLException, IOException {
		List<GroupUser> lgas = table.getConnector().getLinux().getLinuxGroupAccounts().getLinuxGroupAccounts(group.getName(), pkey);
		if(lgas.isEmpty()) throw new SQLException("Unable to find LinuxGroupAccount for username="+pkey+" and group="+group.getName());
		if(lgas.size() > 1) throw new SQLException("Found more than one LinuxGroupAccount for username="+pkey+" and group="+group.getName());
		lgas.get(0).setAsPrimary();
	}
}
