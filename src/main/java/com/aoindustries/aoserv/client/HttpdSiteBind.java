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

import com.aoindustries.aoserv.client.util.SystemdUtil;
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.lang.ObjectUtils;
import com.aoindustries.net.DomainName;
import com.aoindustries.util.IntList;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  HttpdSite
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdSiteBind extends CachedObjectIntegerKey<HttpdSiteBind> implements Disablable {

	static final int
		COLUMN_PKEY = 0,
		COLUMN_HTTPD_SITE = 1,
		COLUMN_SSL_CERTIFICATE = 6
	;
	static final String COLUMN_HTTPD_SITE_name = "httpd_site";
	static final String COLUMN_HTTPD_BIND_name = "httpd_bind";
	static final String COLUMN_NAME_name = "name";

	int httpd_site;
	private int httpd_bind;
	private String name;
	private UnixPath access_log;
	private UnixPath error_log;
	private int certificate;
	int disable_log;
	private String predisable_config;
	private boolean isManual;
	private boolean redirect_to_primary_hostname;
	private String include_site_config;

	// Used for protocol conversion only
	private UnixPath oldSslCertFile;
	private UnixPath oldSslCertKeyFile;
	private UnixPath oldSslCertChainFile;

	@Override
	String toStringImpl() throws SQLException, IOException {
		HttpdSite site = getHttpdSite();
		HttpdBind bind = getHttpdBind();
		if(name == null) {
			return site.toStringImpl() + '|' + bind.toStringImpl();
		} else {
			return site.toStringImpl() + '|' + bind.toStringImpl() + '(' + name + ')';
		}
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_HTTPD_SITE: return httpd_site;
			case 2: return httpd_bind;
			case 3: return name;
			case 4: return access_log;
			case 5: return error_log;
			case COLUMN_SSL_CERTIFICATE: return certificate == -1 ? null : certificate;
			case 7: return disable_log == -1 ? null : disable_log;
			case 8: return predisable_config;
			case 9: return isManual;
			case 10: return redirect_to_primary_hostname;
			case 11: return include_site_config;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_SITE_BINDS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			int pos = 1;
			pkey = result.getInt(pos++);
			httpd_site = result.getInt(pos++);
			httpd_bind = result.getInt(pos++);
			name = result.getString(pos++);
			access_log = UnixPath.valueOf(result.getString(pos++));
			error_log = UnixPath.valueOf(result.getString(pos++));
			certificate = result.getInt(pos++);
			if(result.wasNull()) certificate = -1;
			disable_log = result.getInt(pos++);
			if(result.wasNull()) disable_log = -1;
			predisable_config = result.getString(pos++);
			isManual = result.getBoolean(pos++);
			redirect_to_primary_hostname = result.getBoolean(pos++);
			include_site_config = result.getString(pos++);
			oldSslCertFile = UnixPath.valueOf(result.getString(pos++));
			oldSslCertKeyFile = UnixPath.valueOf(result.getString(pos++));
			oldSslCertChainFile = UnixPath.valueOf(result.getString(pos++));
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = in.readCompressedInt();
			httpd_site = in.readCompressedInt();
			httpd_bind = in.readCompressedInt();
			name = in.readNullUTF();
			access_log = UnixPath.valueOf(in.readUTF());
			error_log = UnixPath.valueOf(in.readUTF());
			certificate = in.readCompressedInt();
			disable_log = in.readCompressedInt();
			predisable_config = in.readNullUTF();
			isManual = in.readBoolean();
			redirect_to_primary_hostname = in.readBoolean();
			include_site_config = in.readNullUTF();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(httpd_site);
		out.writeCompressedInt(httpd_bind);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_81_14) >= 0) {
			out.writeNullUTF(name);
		}
		out.writeUTF(access_log.toString());
		out.writeUTF(error_log.toString());
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_81_10) >= 0) {
			out.writeCompressedInt(certificate);
		} else {
			out.writeNullUTF(ObjectUtils.toString(oldSslCertFile));
			out.writeNullUTF(ObjectUtils.toString(oldSslCertKeyFile));
			if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_81_4) >= 0) {
				out.writeNullUTF(ObjectUtils.toString(oldSslCertChainFile));
			}
		}
		out.writeCompressedInt(disable_log);
		out.writeNullUTF(predisable_config);
		out.writeBoolean(isManual);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_19) >= 0) {
			out.writeBoolean(redirect_to_primary_hostname);
		}
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_81_10) >= 0) {
			out.writeNullUTF(include_site_config);
		}
	}

	public HttpdSite getHttpdSite() throws SQLException, IOException {
		HttpdSite obj = table.connector.getHttpdSites().get(httpd_site);
		if(obj == null) throw new SQLException("Unable to find HttpdSite: " + httpd_site);
		return obj;
	}

	public HttpdBind getHttpdBind() throws SQLException, IOException {
		HttpdBind obj = table.connector.getHttpdBinds().get(httpd_bind);
		if(obj == null) throw new SQLException("Unable to find HttpdBind: " + httpd_bind + " for HttpdSite=" + httpd_site);
		return obj;
	}

	/**
	 * Gets the name of the bind.  The default per-(site, ip, bind) has a null name.
	 * Additional binds per (site, ip, bind) will have non-empty names.
	 * The name is unique per (site, ip, bind), including only one default bind.
	 *
	 * @see #getSystemdEscapedName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the <a href="https://www.freedesktop.org/software/systemd/man/systemd.unit.html">systemd-encoded</a>
	 * name of the bind.
	 *
	 * @see #getName()
	 * @see SystemdUtil#encode(java.lang.String)
	 */
	public String getSystemdEscapedName() {
		return SystemdUtil.encode(name);
	}

	public UnixPath getAccessLog() {
		return access_log;
	}

	public UnixPath getErrorLog() {
		return error_log;
	}

	/**
	 * Gets the SSL certificate for this server.
	 *
	 * @return  the SSL certificate or {@code null} when filtered or not applicable
	 */
	public SslCertificate getCertificate() throws SQLException, IOException {
		// Make sure protocol and certificate present match
		String protocol = getHttpdBind().getNetBind().getAppProtocol().getProtocol();
		if(Protocol.HTTP.equals(protocol)) {
			if(certificate != -1) throw new SQLException("certificate non-null on " + Protocol.HTTP + " protocol for HttpdSiteBind #" + pkey);
		} else if(Protocol.HTTPS.equals(protocol)) {
			if(certificate == -1) throw new SQLException("certificate null on " + Protocol.HTTPS + " protocol for HttpdSiteBind #" + pkey);
		} else {
			throw new SQLException("Protocol is neither " + Protocol.HTTP + " nor " + Protocol.HTTPS + " for HttpdSiteBind #" + pkey + ": " + protocol);
		}
		if(certificate == -1) return null;
		// May be filtered
		return table.connector.getSslCertificates().get(certificate);
	}

	@Override
	public DisableLog getDisableLog() throws SQLException, IOException {
		if(disable_log == -1) return null;
		DisableLog obj = table.connector.getDisableLogs().get(disable_log);
		if(obj == null) throw new SQLException("Unable to find DisableLog: " + disable_log);
		return obj;
	}

	@Override
	public boolean isDisabled() {
		return disable_log != -1;
	}

	@Override
	public boolean canDisable() {
		return disable_log == -1;
	}

	@Override
	public boolean canEnable() throws SQLException, IOException {
		DisableLog dl = getDisableLog();
		if(dl == null) return false;
		else return dl.canEnable() && !getHttpdSite().isDisabled();
	}

	@Override
	public void disable(DisableLog dl) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.HTTPD_SITE_BINDS, dl.pkey, pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.HTTPD_SITE_BINDS, pkey);
	}

	public String getPredisableConfig() {
		return predisable_config;
	}

	public void setPredisableConfig(final String config) throws IOException, SQLException {
		table.connector.requestUpdate(
			true,
			AOServProtocol.CommandID.SET_HTTPD_SITE_BIND_PREDISABLE_CONFIG,
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeNullUTF(config);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code = in.readByte();
					if(code == AOServProtocol.DONE) invalidateList = AOServConnector.readInvalidateList(in);
					else {
						AOServProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: " + code);
					}
				}

				@Override
				public void afterRelease() {
					table.connector.tablesUpdated(invalidateList);
				}
			}
		);
	}

	public boolean isManual() {
		return isManual;
	}

	public void setIsManual(boolean isManual) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_HTTPD_SITE_BIND_IS_MANUAL, pkey, isManual);
	}

	public boolean getRedirectToPrimaryHostname() {
		return redirect_to_primary_hostname;
	}

	public void setRedirectToPrimaryHostname(boolean redirectToPrimaryHostname) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_HTTPD_SITE_BIND_REDIRECT_TO_PRIMARY_HOSTNAME, pkey, redirectToPrimaryHostname);
	}

	/**
	 * Controls whether this bind includes the per-site configuration file.
	 * Will be one of:
	 * <ul>
	 * <li>{@code null} - Automatic mode</li>
	 * <li>{@code "true"} - Include manually enabled</li>
	 * <li>{@code "false"} - Include manually disabled</li>
	 * <li>{@code "IfModule &lt;module_name&gt;"} - Include when a module is enabled</li>
	 * <li>{@code "IfModule !&lt;module_name&gt;"} - Include when a module is disabled</li>
	 * <li>Any future unrecognized value should be treated as equivalent to {@code null} (automatic mode)</li>
	 * </ul>
	 */
	public String getIncludeSiteConfig() {
		return include_site_config;
	}

	public List<HttpdSiteURL> getHttpdSiteURLs() throws IOException, SQLException {
		return table.connector.getHttpdSiteURLs().getHttpdSiteURLs(this);
	}

	public HttpdSiteURL getPrimaryHttpdSiteURL() throws SQLException, IOException {
		return table.connector.getHttpdSiteURLs().getPrimaryHttpdSiteURL(this);
	}

	public List<HttpdSiteURL> getAltHttpdSiteURLs() throws IOException, SQLException {
		return table.connector.getHttpdSiteURLs().getAltHttpdSiteURLs(this);
	}

	public int addHttpdSiteURL(DomainName hostname) throws IOException, SQLException {
		return table.connector.getHttpdSiteURLs().addHttpdSiteURL(this, hostname);
	}

	public List<HttpdSiteBindHeader> getHttpdSiteBindHeaders() throws IOException, SQLException {
		return table.connector.getHttpdSiteBindHeaders().getHttpdSiteBindHeaders(this);
	}

	public List<HttpdSiteBindRedirect> getHttpdSiteBindRedirects() throws IOException, SQLException {
		return table.connector.getHttpdSiteBindRedirects().getHttpdSiteBindRedirects(this);
	}
}
