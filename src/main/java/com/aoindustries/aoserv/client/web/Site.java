/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.web;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Disablable;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.account.DisableLog;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.distribution.Software;
import com.aoindustries.aoserv.client.distribution.SoftwareVersion;
import com.aoindustries.aoserv.client.linux.Group;
import com.aoindustries.aoserv.client.linux.GroupServer;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.linux.User;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.net.AppProtocol;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.GroupId;
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.net.Email;
import com.aoindustries.net.Port;
import com.aoindustries.util.BufferManager;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * An <code>HttpdSite</code> is one unique set of web content and resides in
 * its own directory under <code>/www</code>.  Each <code>HttpdSite</code>
 * has a unique name per server, and may be served simultaneously on any
 * number of <code>HttpdBind</code>s through any number of
 * <code>HttpdServer</code>s.
 * <p>
 * An <code>HttpdSite</code> only stores the information that is common to
 * all site types.  The site will always reference one, and only one, other
 * type of entry, indicating the type of site and providing the rest of the
 * information about the site.
 *
 * @see  VirtualHost
 * @see  HttpdBind
 * @see  HttpdServer
 * @see  StaticSite
 * @see  Site
 *
 * @author  AO Industries, Inc.
 */
final public class Site extends CachedObjectIntegerKey<Site> implements Disablable, Removable {

	static final int
		COLUMN_ID = 0,
		COLUMN_AO_SERVER = 1,
		COLUMN_PACKAGE = 4
	;
	public static final String COLUMN_NAME_name = "name";
	public static final String COLUMN_AO_SERVER_name = "ao_server";

	public static final int MAX_NAME_LENGTH = 255; // Same as maximum name of a directory

	/**
	 * The site name used when an account is disabled.
	 */
	public static final String DISABLED = "disabled";

	private int ao_server;
	private String name;
	private boolean list_first;
	private AccountingCode packageName;
	private UserId linuxAccount;
	private GroupId linuxGroup;
	private String serverAdmin;
	private int disable_log;
	private boolean isManual;
	private String awstatsSkipFiles;
	private int phpVersion;
	private boolean enableCgi;
	private boolean enableSsi;
	private boolean enableHtaccess;
	private boolean enableIndexes;
	private boolean enableFollowSymlinks;
	private boolean enableAnonymousFtp;
	private boolean blockTraceTrack;
	private boolean blockScm;
	private boolean blockCoreDumps;
	private boolean blockEditorBackups;

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_ID: return pkey;
			case COLUMN_AO_SERVER: return ao_server;
			case 2: return name;
			case 3: return list_first;
			case COLUMN_PACKAGE: return packageName;
			case 5: return linuxAccount;
			case 6: return linuxGroup;
			case 7: return serverAdmin;
			case 8: return disable_log==-1?null:disable_log;
			case 9: return isManual;
			case 10: return awstatsSkipFiles;
			case 11: return phpVersion==-1 ? null : phpVersion;
			case 12: return enableCgi;
			case 13: return enableSsi;
			case 14: return enableHtaccess;
			case 15: return enableIndexes;
			case 16: return enableFollowSymlinks;
			case 17: return enableAnonymousFtp;
			case 18: return blockTraceTrack;
			case 19: return blockScm;
			case 20: return blockCoreDumps;
			case 21: return blockEditorBackups;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public int getId() {
		return pkey;
	}

	public int getAoServer_server_pkey() {
		return ao_server;
	}

	public Server getAoServer() throws SQLException, IOException {
		Server obj = table.getConnector().getLinux().getAoServers().get(ao_server);
		if(obj == null) throw new SQLException("Unable to find AOServer: " + ao_server);
		return obj;
	}

	public String getName() {
		return name;
	}

	public boolean getListFirst() {
		return list_first;
	}

	public AccountingCode getPackage_name() {
		return packageName;
	}

	public Package getPackage() throws SQLException, IOException {
		Package obj = table.getConnector().getBilling().getPackages().get(packageName);
		if(obj == null) throw new SQLException("Unable to find Package: "+packageName);
		return obj;
	}

	public UserId getLinuxAccount_username() {
		return linuxAccount;
	}

	public UserServer getLinuxServerAccount() throws SQLException, IOException {
		// May be filtered
		User obj = table.getConnector().getLinux().getLinuxAccounts().get(linuxAccount);
		if(obj == null) return null;

		UserServer lsa = obj.getLinuxServerAccount(getAoServer());
		if(lsa == null) throw new SQLException("Unable to find LinuxServerAccount: "+linuxAccount+" on "+ao_server);
		return lsa;
	}

	public GroupId getLinuxGroup_name() {
		return linuxGroup;
	}

	public GroupServer getLinuxServerGroup() throws SQLException, IOException {
		Group obj = table.getConnector().getLinux().getLinuxGroups().get(linuxGroup);
		if(obj == null) throw new SQLException("Unable to find LinuxGroup: "+linuxGroup);
		GroupServer lsg = obj.getLinuxServerGroup(getAoServer());
		if(lsg == null) throw new SQLException("Unable to find LinuxServerGroup: "+linuxGroup+" on "+ao_server);
		return lsg;
	}

	public String getServerAdmin() {
		return serverAdmin;
	}

	public Integer getDisableLog_pkey() {
		return disable_log == -1 ? null : disable_log;
	}

	@Override
	public DisableLog getDisableLog() throws SQLException, IOException {
		if(disable_log == -1) return null;
		DisableLog obj = table.getConnector().getAccount().getDisableLogs().get(disable_log);
		if(obj == null) throw new SQLException("Unable to find DisableLog: "+disable_log);
		return obj;
	}

	public boolean isManual() {
		return isManual;
	}

	public String getAwstatsSkipFiles() {
		return awstatsSkipFiles;
	}

	public Integer getPhpVersion_pkey() {
		return phpVersion == -1 ? null : phpVersion;
	}

	public SoftwareVersion getPhpVersion() throws IOException, SQLException {
		if(phpVersion == -1) return null;
		SoftwareVersion tv = table.getConnector().getDistribution().getTechnologyVersions().get(phpVersion);
		if(tv == null) throw new SQLException("TechnologyVersion not found: " + phpVersion);
		if(!tv.getTechnologyName_name().equals(Software.PHP)) throw new SQLException("Not a PHP version: " + tv.getTechnologyName_name() + " #" + tv.getPkey());
		if(
			tv.getOperatingSystemVersion(table.getConnector()).getPkey()
			!= getAoServer().getServer().getOperatingSystemVersion_id()
		) {
			throw new SQLException("php/operating system version mismatch on HttpdSite: #" + pkey);
		}
		return tv;
	}

	public boolean getEnableCgi() {
		return enableCgi;
	}

	public boolean getEnableSsi() {
		return enableSsi;
	}

	public boolean getEnableHtaccess() {
		return enableHtaccess;
	}

	public boolean getEnableIndexes() {
		return enableIndexes;
	}

	public boolean getEnableFollowSymlinks() {
		return enableFollowSymlinks;
	}

	public boolean getEnableAnonymousFtp() {
		return enableAnonymousFtp;
	}

	/**
	 * Enables the blocking of <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.8">TRACE</a>
	 * and TRACK <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html">HTTP methods</a>.
	 */
	public boolean getBlockTraceTrack() {
		return blockTraceTrack;
	}

	/**
	 * Enables the blocking of URL
	 * patterns associated with source control management systems.  It is possible for SCM
	 * files, such as <code>CVS/Root</code> and <code>.git/config</code> to have authentication
	 * credentials.  Leave this enabled when pulling web root content directly from revision
	 * control systems.  Currently has protections for <a href="http://savannah.nongnu.org/projects/cvs">CVS</a>,
	 * <a href="https://subversion.apache.org/">Subversion</a>, and
	 * <a href="https://git-scm.com/">Git</a>.
	 */
	public boolean getBlockScm() {
		return blockScm;
	}

	/**
	 * Added <code>httpd_sites.block_core_dumps</code> column, which enables blocking of core dumps.
	 * Core dumps could potentially expose sensitive information and have predictable filename patterns.
	 */
	public boolean getBlockCoreDumps() {
		return blockCoreDumps;
	}

	/**
	 * Enables blocking filename patterns
	 * associated with editor automatic backups.  Without this protection, it is possible for source code
	 * to be leaked by accessing the URL associated with the automatic backups.  Currently has
	 * protections for <a href="https://www.gnu.org/software/emacs/">Emacs</a> and
	 * <a href="http://www.vim.org/">Vim</a>.
	 */
	public boolean getBlockEditorBackups() {
		return blockEditorBackups;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			int pos = 1;
			pkey = result.getInt(pos++);
			ao_server = result.getInt(pos++);
			name = result.getString(pos++);
			list_first = result.getBoolean(pos++);
			packageName = AccountingCode.valueOf(result.getString(pos++));
			linuxAccount = UserId.valueOf(result.getString(pos++));
			linuxGroup = GroupId.valueOf(result.getString(pos++));
			serverAdmin = result.getString(pos++);
			disable_log = result.getInt(pos++);
			if(result.wasNull()) disable_log=-1;
			isManual = result.getBoolean(pos++);
			awstatsSkipFiles = result.getString(pos++);
			phpVersion = result.getInt(pos++);
			if(result.wasNull()) phpVersion = -1;
			enableCgi = result.getBoolean(pos++);
			enableSsi = result.getBoolean(pos++);
			enableHtaccess = result.getBoolean(pos++);
			enableIndexes = result.getBoolean(pos++);
			enableFollowSymlinks = result.getBoolean(pos++);
			enableAnonymousFtp = result.getBoolean(pos++);
			blockTraceTrack = result.getBoolean(pos++);
			blockScm = result.getBoolean(pos++);
			blockCoreDumps = result.getBoolean(pos++);
			blockEditorBackups = result.getBoolean(pos++);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = in.readCompressedInt();
			ao_server = in.readCompressedInt();
			name = in.readUTF();
			list_first = in.readBoolean();
			packageName = AccountingCode.valueOf(in.readUTF()).intern();
			linuxAccount = UserId.valueOf(in.readUTF()).intern();
			linuxGroup = GroupId.valueOf(in.readUTF()).intern();
			serverAdmin = in.readUTF();
			disable_log = in.readCompressedInt();
			isManual = in.readBoolean();
			awstatsSkipFiles = in.readNullUTF();
			phpVersion = in.readCompressedInt();
			enableCgi = in.readBoolean();
			enableSsi = in.readBoolean();
			enableHtaccess = in.readBoolean();
			enableIndexes = in.readBoolean();
			enableFollowSymlinks = in.readBoolean();
			enableAnonymousFtp = in.readBoolean();
			blockTraceTrack = in.readBoolean();
			blockScm = in.readBoolean();
			blockCoreDumps = in.readBoolean();
			blockEditorBackups = in.readBoolean();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(ao_server);
		out.writeUTF(name);
		out.writeBoolean(list_first);
		out.writeUTF(packageName.toString());
		out.writeUTF(linuxAccount.toString());
		out.writeUTF(linuxGroup.toString());
		out.writeUTF(serverAdmin);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_9) <= 0) {
			out.writeNullUTF(null); // contentSrc
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30)<=0) {
			out.writeShort(0);
			out.writeShort(7);
			out.writeShort(0);
			out.writeShort(7);
			out.writeShort(0);
			out.writeShort(7);
			out.writeShort(0);
			out.writeShort(7);
		}
		out.writeCompressedInt(disable_log);
		out.writeBoolean(isManual);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_129) >= 0) {
			out.writeNullUTF(awstatsSkipFiles);
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_78) >= 0) {
			out.writeCompressedInt(phpVersion);
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_79) >= 0) {
			out.writeBoolean(enableCgi);
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_80_1) >= 0) {
			out.writeBoolean(enableSsi);
			out.writeBoolean(enableHtaccess);
			out.writeBoolean(enableIndexes);
			out.writeBoolean(enableFollowSymlinks);
			out.writeBoolean(enableAnonymousFtp);
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_6) >= 0) {
			out.writeBoolean(blockTraceTrack);
			out.writeBoolean(blockScm);
			out.writeBoolean(blockCoreDumps);
			out.writeBoolean(blockEditorBackups);
		}
	}

	public int addHttpdSiteAuthenticatedLocation(
		String path,
		boolean isRegularExpression,
		String authName,
		UnixPath authGroupFile,
		UnixPath authUserFile,
		String require,
		String handler
	) throws IOException, SQLException {
		return table.getConnector().getWeb().getHttpdSiteAuthenticatedLocationTable().addHttpdSiteAuthenticatedLocation(
			this,
			path,
			isRegularExpression,
			authName,
			authGroupFile,
			authUserFile,
			require,
			handler
		);
	}

	@Override
	public boolean canDisable() throws IOException, SQLException {
		if(disable_log!=-1) return false;
		for(VirtualHost hsb : getHttpdSiteBinds()) if(!hsb.isDisabled()) return false;
		return true;
	}

	@Override
	public boolean canEnable() throws SQLException, IOException {
		DisableLog dl=getDisableLog();
		if(dl==null) return false;
		else return
			dl.canEnable()
			&& !getPackage().isDisabled()
			&& !getLinuxServerAccount().isDisabled()
		;
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() {
		return Collections.emptyList();
	}

	@Override
	public void disable(DisableLog dl) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.DISABLE, Table.TableID.HTTPD_SITES, dl.getPkey(), pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.ENABLE, Table.TableID.HTTPD_SITES, pkey);
	}

	/**
	 * Gets the directory where this site is installed.
	 */
	public UnixPath getInstallDirectory() throws SQLException, IOException {
		try {
			return UnixPath.valueOf(
				getAoServer().getServer().getOperatingSystemVersion().getHttpdSitesDirectory()
				+ "/" + name
			);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public boolean isDisabled() {
		return disable_log!=-1;
	}

	public List<Location> getHttpdSiteAuthenticatedLocations() throws IOException, SQLException {
		return table.getConnector().getWeb().getHttpdSiteAuthenticatedLocationTable().getHttpdSiteAuthenticatedLocations(this);
	}

	public List<VirtualHost> getHttpdSiteBinds() throws IOException, SQLException {
		return table.getConnector().getWeb().getHttpdSiteBinds().getHttpdSiteBinds(this);
	}

	public List<VirtualHost> getHttpdSiteBinds(HttpdServer server) throws SQLException, IOException {
		return table.getConnector().getWeb().getHttpdSiteBinds().getHttpdSiteBinds(this, server);
	}

	public StaticSite getHttpdStaticSite() throws IOException, SQLException {
		return table.getConnector().getWeb().getHttpdStaticSites().get(pkey);
	}

	public com.aoindustries.aoserv.client.web.tomcat.Site getHttpdTomcatSite() throws IOException, SQLException {
		return table.getConnector().getWeb_tomcat().getHttpdTomcatSites().get(pkey);
	}

	public VirtualHostName getPrimaryHttpdSiteURL() throws SQLException, IOException {
		List<VirtualHost> binds=getHttpdSiteBinds();
		if(binds.isEmpty()) return null;

		Port httpPort = table.getConnector().getNet().getProtocols().get(AppProtocol.HTTP).getPort();
		Port httpsPort = table.getConnector().getNet().getProtocols().get(AppProtocol.HTTPS).getPort();

		// Find first in null (default) HTTPS on default port, if any
		for(VirtualHost bind : binds) {
			if(bind.getName() == null) {
				Bind nb = bind.getHttpdBind().getNetBind();
				if(
					AppProtocol.HTTPS.equals(nb.getAppProtocol().getProtocol())
					&& nb.getPort().equals(httpsPort)
				) {
					return bind.getPrimaryHttpdSiteURL();
				}
			}
		}
		// Find first in null (default) HTTP on default port, if any
		for(VirtualHost bind : binds) {
			if(bind.getName() == null) {
				Bind nb = bind.getHttpdBind().getNetBind();
				if(
					AppProtocol.HTTP.equals(nb.getAppProtocol().getProtocol())
					&& nb.getPort().equals(httpPort)
				) {
					return bind.getPrimaryHttpdSiteURL();
				}
			}
		}
		// Find first in null (default) HTTPS on any port, if any
		for(VirtualHost bind : binds) {
			if(bind.getName() == null) {
				Bind nb = bind.getHttpdBind().getNetBind();
				if(AppProtocol.HTTPS.equals(nb.getAppProtocol().getProtocol())) {
					return bind.getPrimaryHttpdSiteURL();
				}
			}
		}
		// Find first in null (default) HTTP on any port, if any
		for(VirtualHost bind : binds) {
			if(bind.getName() == null) {
				Bind nb = bind.getHttpdBind().getNetBind();
				if(AppProtocol.HTTP.equals(nb.getAppProtocol().getProtocol())) {
					return bind.getPrimaryHttpdSiteURL();
				}
			}
		}
		// Find first HTTPS on default port, if any
		for(VirtualHost bind : binds) {
			Bind nb = bind.getHttpdBind().getNetBind();
			if(
				AppProtocol.HTTPS.equals(nb.getAppProtocol().getProtocol())
				&& nb.getPort().equals(httpsPort)
			) {
				return bind.getPrimaryHttpdSiteURL();
			}
		}
		// Find first HTTP on default port, if any
		for(VirtualHost bind : binds) {
			Bind nb = bind.getHttpdBind().getNetBind();
			if(
				AppProtocol.HTTP.equals(nb.getAppProtocol().getProtocol())
				&& nb.getPort().equals(httpPort)
			) {
				return bind.getPrimaryHttpdSiteURL();
			}
		}
		// Find first HTTPS on any port, if any
		for(VirtualHost bind : binds) {
			Bind nb = bind.getHttpdBind().getNetBind();
			if(AppProtocol.HTTPS.equals(nb.getAppProtocol().getProtocol())) {
				return bind.getPrimaryHttpdSiteURL();
			}
		}
		// Find first HTTP on any port, if any
		for(VirtualHost bind : binds) {
			Bind nb = bind.getHttpdBind().getNetBind();
			if(AppProtocol.HTTP.equals(nb.getAppProtocol().getProtocol())) {
				return bind.getPrimaryHttpdSiteURL();
			}
		}
		// Take first without any regard for protocols and ports
		return binds.get(0).getPrimaryHttpdSiteURL();
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.HTTPD_SITES;
	}

	//public void initializePasswdFile(String username, String password) {
	//    table.getConnector().requestUpdate(AOServProtocol.INITIALIZE_HTTPD_SITE_PASSWD_FILE, pkey, username, UnixCrypt.crypt(username, password));
	//}

	/**
	 * Checks the format of the name of the site, as used in the <code>/www</code>
	 * directory.  The site name must be 255 characters or less, and comprised of
	 * only <code>a-z</code>, <code>0-9</code>, <code>.</code> or <code>-</code>.  The first
	 * character must be <code>a-z</code> or <code>0-9</code>.
	 * <p>
	 * Note: This matches the check constraint on the httpd_sites table.
	 * Note: This matches keepWwwDirs in HttpdSiteManager.
	 * </p>
	 * TODO: Self-validating type for site names
	 */
	public static boolean isValidSiteName(String name) {
		// These are the other files/directories that may exist under /www.  To avoid
		// potential conflicts, these may not be used as site names.
		if(
			   DISABLED.equals(name) // Provided by aoserv-httpd-site-disabled package
			// CentOS 5 only
			|| "cache".equals(name) // nginx only?
			|| "fastcgi".equals(name)
			|| "error".equals(name)
			|| "icons".equals(name)
			// CentOS 7
			|| "cgi-bin".equals(name)
			|| "html".equals(name)
			|| "mrtg".equals(name)
			// Other filesystem patterns
			|| "lost+found".equals(name)
			|| "aquota.group".equals(name)
			|| "aquota.user".equals(name)
		) return false;

		int len = name.length();
		if (len == 0 || len > MAX_NAME_LENGTH)
				return false;
		// The first character must be [a-z] or [0-9]
		char ch = name.charAt(0);
		if (
			(ch < 'a' || ch > 'z')
			&& (ch<'0' || ch>'9')
		) return false;
		// The rest may have additional characters
		for (int c = 1; c < len; c++) {
			ch = name.charAt(c);
			if(
				(ch < 'a' || ch > 'z')
				&& (ch < '0' || ch > '9')
				&& ch != '.'
				&& ch != '-'
			) return false;
		}
		return true;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.REMOVE, Table.TableID.HTTPD_SITES, pkey);
	}

	public void setIsManual(boolean isManual) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_HTTPD_SITE_IS_MANUAL, pkey, isManual);
	}

	public void setServerAdmin(Email address) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_HTTPD_SITE_SERVER_ADMIN, pkey, address);
	}

	public void setPhpVersion(SoftwareVersion phpVersion) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_HTTPD_SITE_PHP_VERSION, pkey, phpVersion==null ? -1 : phpVersion.getPkey());
	}

	public void setEnableCgi(boolean enableCgi) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_HTTPD_SITE_ENABLE_CGI, pkey, enableCgi);
	}

	public void setEnableSsi(boolean enableSsi) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_HTTPD_SITE_ENABLE_SSI, pkey, enableSsi);
	}

	public void setEnableHtaccess(boolean enableHtaccess) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_HTTPD_SITE_ENABLE_HTACCESS, pkey, enableHtaccess);
	}

	public void setEnableIndexes(boolean enableIndexes) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_HTTPD_SITE_ENABLE_INDEXES, pkey, enableIndexes);
	}

	public void setEnableFollowSymlinks(boolean enableFollowSymlinks) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_HTTPD_SITE_ENABLE_FOLLOW_SYMLINKS, pkey, enableFollowSymlinks);
	}

	public void setEnableAnonymousFtp(boolean enableAnonymousFtp) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_HTTPD_SITE_ENABLE_ANONYMOUS_FTP, pkey, enableAnonymousFtp);
	}

	public void setBlockTraceTrack(boolean blockTraceTrack) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_HTTPD_SITE_BLOCK_TRACE_TRACK, pkey, blockTraceTrack);
	}

	public void setBlockScm(boolean blockScm) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_HTTPD_SITE_BLOCK_SCM, pkey, blockScm);
	}

	public void setBlockCoreDumps(boolean blockCoreDumps) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_HTTPD_SITE_BLOCK_CORE_DUMPS, pkey, blockCoreDumps);
	}

	public void setBlockEditorBackups(boolean blockEditorBackups) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_HTTPD_SITE_BLOCK_EDITOR_BACKUPS, pkey, blockEditorBackups);
	}

	@Override
	public String toStringImpl() throws SQLException, IOException {
		return name+" on "+getAoServer().getHostname();
	}

	public void getAWStatsFile(final String path, final String queryString, final OutputStream out) throws IOException, SQLException {
		table.getConnector().requestUpdate(false,
			AoservProtocol.CommandID.GET_AWSTATS_FILE,
			new AOServConnector.UpdateRequest() {
				@Override
				public void writeRequest(CompressedDataOutputStream masterOut) throws IOException {
					masterOut.writeCompressedInt(pkey);
					masterOut.writeUTF(path);
					masterOut.writeUTF(queryString==null ? "" : queryString);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					byte[] buff=BufferManager.getBytes();
					try {
						int code;
						while((code=in.readByte())==AoservProtocol.NEXT) {
							int len=in.readShort();
							in.readFully(buff, 0, len);
							out.write(buff, 0, len);
						}
						AoservProtocol.checkResult(code, in);
					} finally {
						BufferManager.release(buff, false);
					}
				}

				@Override
				public void afterRelease() {
				}
			}
		);
	}
}
