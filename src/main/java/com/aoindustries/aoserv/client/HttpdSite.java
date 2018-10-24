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
package com.aoindustries.aoserv.client;

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
 * @see  HttpdSiteBind
 * @see  HttpdBind
 * @see  HttpdServer
 * @see  HttpdStaticSite
 * @see  HttpdTomcatSite
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdSite extends CachedObjectIntegerKey<HttpdSite> implements Disablable, Removable {

	static final int
		COLUMN_PKEY = 0,
		COLUMN_AO_SERVER = 1,
		COLUMN_PACKAGE = 4
	;
	static final String COLUMN_SITE_NAME_name = "site_name";
	static final String COLUMN_AO_SERVER_name = "ao_server";

	public static final int MAX_SITE_NAME_LENGTH = 255; // Same as maximum name of a directory

	/**
	 * The site name used when an account is disabled.
	 */
	public static final String DISABLED = "disabled";

	int ao_server;
	String site_name;
	private boolean list_first;
	AccountingCode packageName;
	UserId linuxAccount;
	GroupId linuxGroup;
	private String serverAdmin;
	int disable_log;
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

	public int addHttpdSiteAuthenticatedLocation(
		String path,
		boolean isRegularExpression,
		String authName,
		UnixPath authGroupFile,
		UnixPath authUserFile,
		String require,
		String handler
	) throws IOException, SQLException {
		return table.connector.getHttpdSiteAuthenticatedLocationTable().addHttpdSiteAuthenticatedLocation(
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
		for(HttpdSiteBind hsb : getHttpdSiteBinds()) if(hsb.disable_log==-1) return false;
		return true;
	}

	@Override
	public boolean canEnable() throws SQLException, IOException {
		DisableLog dl=getDisableLog();
		if(dl==null) return false;
		else return
			dl.canEnable()
			&& getPackage().disable_log==-1
			&& getLinuxServerAccount().disable_log==-1
		;
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() {
		return Collections.emptyList();
	}

	@Override
	public void disable(DisableLog dl) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.HTTPD_SITES, dl.pkey, pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.HTTPD_SITES, pkey);
	}

	/**
	 * Gets the directory where this site is installed.
	 */
	public UnixPath getInstallDirectory() throws SQLException, IOException {
		try {
			return UnixPath.valueOf(
				getAOServer().getServer().getOperatingSystemVersion().getHttpdSitesDirectory()
				+ "/" + site_name
			);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_AO_SERVER: return ao_server;
			case 2: return site_name;
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

	@Override
	public boolean isDisabled() {
		return disable_log!=-1;
	}

	@Override
	public DisableLog getDisableLog() throws SQLException, IOException {
		if(disable_log == -1) return null;
		DisableLog obj = table.connector.getDisableLogs().get(disable_log);
		if(obj == null) throw new SQLException("Unable to find DisableLog: "+disable_log);
		return obj;
	}

	public List<HttpdSiteAuthenticatedLocation> getHttpdSiteAuthenticatedLocations() throws IOException, SQLException {
		return table.connector.getHttpdSiteAuthenticatedLocationTable().getHttpdSiteAuthenticatedLocations(this);
	}

	public List<HttpdSiteBind> getHttpdSiteBinds() throws IOException, SQLException {
		return table.connector.getHttpdSiteBinds().getHttpdSiteBinds(this);
	}

	public List<HttpdSiteBind> getHttpdSiteBinds(HttpdServer server) throws SQLException, IOException {
		return table.connector.getHttpdSiteBinds().getHttpdSiteBinds(this, server);
	}

	public HttpdStaticSite getHttpdStaticSite() throws IOException, SQLException {
		return table.connector.getHttpdStaticSites().get(pkey);
	}

	public HttpdTomcatSite getHttpdTomcatSite() throws IOException, SQLException {
		return table.connector.getHttpdTomcatSites().get(pkey);
	}

	public LinuxServerAccount getLinuxServerAccount() throws SQLException, IOException {
		// May be filtered
		LinuxAccount obj = table.connector.getLinuxAccounts().get(linuxAccount);
		if(obj == null) return null;

		LinuxServerAccount lsa = obj.getLinuxServerAccount(getAOServer());
		if(lsa == null) throw new SQLException("Unable to find LinuxServerAccount: "+linuxAccount+" on "+ao_server);
		return lsa;
	}

	public LinuxServerGroup getLinuxServerGroup() throws SQLException, IOException {
		LinuxGroup obj = table.connector.getLinuxGroups().get(linuxGroup);
		if(obj == null) throw new SQLException("Unable to find LinuxGroup: "+linuxGroup);
		LinuxServerGroup lsg = obj.getLinuxServerGroup(getAOServer());
		if(lsg == null) throw new SQLException("Unable to find LinuxServerGroup: "+linuxGroup+" on "+ao_server);
		return lsg;
	}

	public Package getPackage() throws SQLException, IOException {
		Package obj = table.connector.getPackages().get(packageName);
		if(obj == null) throw new SQLException("Unable to find Package: "+packageName);
		return obj;
	}

	public HttpdSiteURL getPrimaryHttpdSiteURL() throws SQLException, IOException {
		List<HttpdSiteBind> binds=getHttpdSiteBinds();
		if(binds.isEmpty()) return null;

		Port httpPort = table.connector.getProtocols().get(Protocol.HTTP).getPort();
		Port httpsPort = table.connector.getProtocols().get(Protocol.HTTPS).getPort();

		// Find first in null (default) HTTPS on default port, if any
		for(HttpdSiteBind bind : binds) {
			if(bind.getName() == null) {
				NetBind nb = bind.getHttpdBind().getNetBind();
				if(
					Protocol.HTTPS.equals(nb.getAppProtocol().getProtocol())
					&& nb.getPort().equals(httpsPort)
				) {
					return bind.getPrimaryHttpdSiteURL();
				}
			}
		}
		// Find first in null (default) HTTP on default port, if any
		for(HttpdSiteBind bind : binds) {
			if(bind.getName() == null) {
				NetBind nb = bind.getHttpdBind().getNetBind();
				if(
					Protocol.HTTP.equals(nb.getAppProtocol().getProtocol())
					&& nb.getPort().equals(httpPort)
				) {
					return bind.getPrimaryHttpdSiteURL();
				}
			}
		}
		// Find first in null (default) HTTPS on any port, if any
		for(HttpdSiteBind bind : binds) {
			if(bind.getName() == null) {
				NetBind nb = bind.getHttpdBind().getNetBind();
				if(Protocol.HTTPS.equals(nb.getAppProtocol().getProtocol())) {
					return bind.getPrimaryHttpdSiteURL();
				}
			}
		}
		// Find first in null (default) HTTP on any port, if any
		for(HttpdSiteBind bind : binds) {
			if(bind.getName() == null) {
				NetBind nb = bind.getHttpdBind().getNetBind();
				if(Protocol.HTTP.equals(nb.getAppProtocol().getProtocol())) {
					return bind.getPrimaryHttpdSiteURL();
				}
			}
		}
		// Find first HTTPS on default port, if any
		for(HttpdSiteBind bind : binds) {
			NetBind nb = bind.getHttpdBind().getNetBind();
			if(
				Protocol.HTTPS.equals(nb.getAppProtocol().getProtocol())
				&& nb.getPort().equals(httpsPort)
			) {
				return bind.getPrimaryHttpdSiteURL();
			}
		}
		// Find first HTTP on default port, if any
		for(HttpdSiteBind bind : binds) {
			NetBind nb = bind.getHttpdBind().getNetBind();
			if(
				Protocol.HTTP.equals(nb.getAppProtocol().getProtocol())
				&& nb.getPort().equals(httpPort)
			) {
				return bind.getPrimaryHttpdSiteURL();
			}
		}
		// Find first HTTPS on any port, if any
		for(HttpdSiteBind bind : binds) {
			NetBind nb = bind.getHttpdBind().getNetBind();
			if(Protocol.HTTPS.equals(nb.getAppProtocol().getProtocol())) {
				return bind.getPrimaryHttpdSiteURL();
			}
		}
		// Find first HTTP on any port, if any
		for(HttpdSiteBind bind : binds) {
			NetBind nb = bind.getHttpdBind().getNetBind();
			if(Protocol.HTTP.equals(nb.getAppProtocol().getProtocol())) {
				return bind.getPrimaryHttpdSiteURL();
			}
		}
		// Take first without any regard for protocols and ports
		return binds.get(0).getPrimaryHttpdSiteURL();
	}

	public AOServer getAOServer() throws SQLException, IOException {
		AOServer obj=table.connector.getAoServers().get(ao_server);
		if(obj == null) throw new SQLException("Unable to find AOServer: "+ao_server);
		return obj;
	}

	public String getServerAdmin() {
		return serverAdmin;
	}

	public String getSiteName() {
		return site_name;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_SITES;
	}

	//public void initializePasswdFile(String username, String password) {
	//    table.connector.requestUpdate(AOServProtocol.INITIALIZE_HTTPD_SITE_PASSWD_FILE, pkey, username, UnixCrypt.crypt(username, password));
	//}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			int pos = 1;
			pkey = result.getInt(pos++);
			ao_server = result.getInt(pos++);
			site_name = result.getString(pos++);
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

	public boolean isManual() {
		return isManual;
	}

	public String getAwstatsSkipFiles() {
		return awstatsSkipFiles;
	}

	public TechnologyVersion getPhpVersion() throws IOException, SQLException {
		if(phpVersion == -1) return null;
		TechnologyVersion tv = table.connector.getTechnologyVersions().get(phpVersion);
		if(tv == null) throw new SQLException("TechnologyVersion not found: " + phpVersion);
		if(!tv.name.equals(TechnologyName.PHP)) throw new SQLException("Not a PHP version: " + tv.name + " #" + tv.pkey);
		if(
			tv.getOperatingSystemVersion(table.connector).getPkey()
			!= getAOServer().getServer().operating_system_version
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
		if (len == 0 || len > MAX_SITE_NAME_LENGTH)
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

	public boolean listFirst() {
		return list_first;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = in.readCompressedInt();
			ao_server = in.readCompressedInt();
			site_name = in.readUTF();
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
	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.HTTPD_SITES, pkey);
	}

	public void setIsManual(boolean isManual) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_HTTPD_SITE_IS_MANUAL, pkey, isManual);
	}

	public void setServerAdmin(Email address) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_HTTPD_SITE_SERVER_ADMIN, pkey, address);
	}

	public void setPhpVersion(TechnologyVersion phpVersion) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_HTTPD_SITE_PHP_VERSION, pkey, phpVersion==null ? -1 : phpVersion.pkey);
	}

	public void setEnableCgi(boolean enableCgi) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_HTTPD_SITE_ENABLE_CGI, pkey, enableCgi);
	}

	public void setEnableSsi(boolean enableSsi) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_HTTPD_SITE_ENABLE_SSI, pkey, enableSsi);
	}

	public void setEnableHtaccess(boolean enableHtaccess) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_HTTPD_SITE_ENABLE_HTACCESS, pkey, enableHtaccess);
	}

	public void setEnableIndexes(boolean enableIndexes) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_HTTPD_SITE_ENABLE_INDEXES, pkey, enableIndexes);
	}

	public void setEnableFollowSymlinks(boolean enableFollowSymlinks) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_HTTPD_SITE_ENABLE_FOLLOW_SYMLINKS, pkey, enableFollowSymlinks);
	}

	public void setEnableAnonymousFtp(boolean enableAnonymousFtp) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_HTTPD_SITE_ENABLE_ANONYMOUS_FTP, pkey, enableAnonymousFtp);
	}

	public void setBlockTraceTrack(boolean blockTraceTrack) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_HTTPD_SITE_BLOCK_TRACE_TRACK, pkey, blockTraceTrack);
	}

	public void setBlockScm(boolean blockScm) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_HTTPD_SITE_BLOCK_SCM, pkey, blockScm);
	}

	public void setBlockCoreDumps(boolean blockCoreDumps) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_HTTPD_SITE_BLOCK_CORE_DUMPS, pkey, blockCoreDumps);
	}

	public void setBlockEditorBackups(boolean blockEditorBackups) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_HTTPD_SITE_BLOCK_EDITOR_BACKUPS, pkey, blockEditorBackups);
	}

	@Override
	String toStringImpl() throws SQLException, IOException {
		return site_name+" on "+getAOServer().getHostname();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(ao_server);
		out.writeUTF(site_name);
		out.writeBoolean(list_first);
		out.writeUTF(packageName.toString());
		out.writeUTF(linuxAccount.toString());
		out.writeUTF(linuxGroup.toString());
		out.writeUTF(serverAdmin);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_81_9) <= 0) {
			out.writeNullUTF(null); // contentSrc
		}
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
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
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_0_A_129) >= 0) {
			out.writeNullUTF(awstatsSkipFiles);
		}
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_78) >= 0) {
			out.writeCompressedInt(phpVersion);
		}
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_79) >= 0) {
			out.writeBoolean(enableCgi);
		}
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_80_1) >= 0) {
			out.writeBoolean(enableSsi);
			out.writeBoolean(enableHtaccess);
			out.writeBoolean(enableIndexes);
			out.writeBoolean(enableFollowSymlinks);
			out.writeBoolean(enableAnonymousFtp);
		}
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_81_6) >= 0) {
			out.writeBoolean(blockTraceTrack);
			out.writeBoolean(blockScm);
			out.writeBoolean(blockCoreDumps);
			out.writeBoolean(blockEditorBackups);
		}
	}

	public void getAWStatsFile(final String path, final String queryString, final OutputStream out) throws IOException, SQLException {
		table.connector.requestUpdate(
			false,
			AOServProtocol.CommandID.GET_AWSTATS_FILE,
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
						while((code=in.readByte())==AOServProtocol.NEXT) {
							int len=in.readShort();
							in.readFully(buff, 0, len);
							out.write(buff, 0, len);
						}
						AOServProtocol.checkResult(code, in);
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
