/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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

import com.aoapps.collections.IntList;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.DomainName;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.Disablable;
import com.aoindustries.aoserv.client.account.DisableLog;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.net.AppProtocol;
import com.aoindustries.aoserv.client.pki.Certificate;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.util.SystemdUtil;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * @see  Site
 *
 * @author  AO Industries, Inc.
 */
public final class VirtualHost extends CachedObjectIntegerKey<VirtualHost> implements Disablable {

	static final int
		COLUMN_PKEY = 0,
		COLUMN_HTTPD_SITE = 1,
		COLUMN_SSL_CERTIFICATE = 6
	;
	static final String COLUMN_HTTPD_SITE_name = "httpd_site";
	static final String COLUMN_HTTPD_BIND_name = "httpd_bind";
	static final String COLUMN_NAME_name = "name";

	private int httpd_site;
	private int httpd_bind;
	private String name;
	private PosixPath access_log;
	private PosixPath error_log;
	private int certificate;
	private int disable_log;
	private String predisable_config;
	private boolean isManual;
	private boolean redirect_to_primary_hostname;
	private String include_site_config;

	// Used for protocol conversion only
	private PosixPath oldSslCertFile;
	private PosixPath oldSslCertKeyFile;
	private PosixPath oldSslCertChainFile;

	@Override
	public String toStringImpl() throws SQLException, IOException {
		Site site = getHttpdSite();
		HttpdBind bind = getHttpdBind();
		if(name == null) {
			return site.toStringImpl() + '|' + bind.toStringImpl();
		} else {
			return site.toStringImpl() + '|' + bind.toStringImpl() + '(' + name + ')';
		}
	}

	@Override
	protected Object getColumnImpl(int i) {
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
	public Table.TableID getTableID() {
		return Table.TableID.HTTPD_SITE_BINDS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			int pos = 1;
			pkey = result.getInt(pos++);
			httpd_site = result.getInt(pos++);
			httpd_bind = result.getInt(pos++);
			name = result.getString(pos++);
			access_log = PosixPath.valueOf(result.getString(pos++));
			error_log = PosixPath.valueOf(result.getString(pos++));
			certificate = result.getInt(pos++);
			if(result.wasNull()) certificate = -1;
			disable_log = result.getInt(pos++);
			if(result.wasNull()) disable_log = -1;
			predisable_config = result.getString(pos++);
			isManual = result.getBoolean(pos++);
			redirect_to_primary_hostname = result.getBoolean(pos++);
			include_site_config = result.getString(pos++);
			oldSslCertFile = PosixPath.valueOf(result.getString(pos++));
			oldSslCertKeyFile = PosixPath.valueOf(result.getString(pos++));
			oldSslCertChainFile = PosixPath.valueOf(result.getString(pos++));
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			pkey = in.readCompressedInt();
			httpd_site = in.readCompressedInt();
			httpd_bind = in.readCompressedInt();
			name = in.readNullUTF();
			access_log = PosixPath.valueOf(in.readUTF());
			error_log = PosixPath.valueOf(in.readUTF());
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
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(httpd_site);
		out.writeCompressedInt(httpd_bind);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_14) >= 0) {
			out.writeNullUTF(name);
		}
		out.writeUTF(access_log.toString());
		out.writeUTF(error_log.toString());
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_10) >= 0) {
			out.writeCompressedInt(certificate);
		} else {
			out.writeNullUTF(Objects.toString(oldSslCertFile, null));
			out.writeNullUTF(Objects.toString(oldSslCertKeyFile, null));
			if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_4) >= 0) {
				out.writeNullUTF(Objects.toString(oldSslCertChainFile, null));
			}
		}
		out.writeCompressedInt(disable_log);
		out.writeNullUTF(predisable_config);
		out.writeBoolean(isManual);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_19) >= 0) {
			out.writeBoolean(redirect_to_primary_hostname);
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_10) >= 0) {
			out.writeNullUTF(include_site_config);
		}
	}

	public Site getHttpdSite() throws SQLException, IOException {
		Site obj = table.getConnector().getWeb().getSite().get(httpd_site);
		if(obj == null) throw new SQLException("Unable to find HttpdSite: " + httpd_site);
		return obj;
	}

	public HttpdBind getHttpdBind() throws SQLException, IOException {
		HttpdBind obj = table.getConnector().getWeb().getHttpdBind().get(httpd_bind);
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

	public PosixPath getAccessLog() {
		return access_log;
	}

	public PosixPath getErrorLog() {
		return error_log;
	}

	/**
	 * Gets the SSL certificate for this server.
	 *
	 * @return  the SSL certificate or {@code null} when filtered or not applicable
	 */
	public Certificate getCertificate() throws SQLException, IOException {
		// Make sure protocol and certificate present match
		String protocol = getHttpdBind().getNetBind().getAppProtocol().getProtocol();
		if(AppProtocol.HTTP.equals(protocol)) {
			if(certificate != -1) throw new SQLException("certificate non-null on " + AppProtocol.HTTP + " protocol for HttpdSiteBind #" + pkey);
		} else if(AppProtocol.HTTPS.equals(protocol)) {
			if(certificate == -1) throw new SQLException("certificate null on " + AppProtocol.HTTPS + " protocol for HttpdSiteBind #" + pkey);
		} else {
			throw new SQLException("Protocol is neither " + AppProtocol.HTTP + " nor " + AppProtocol.HTTPS + " for HttpdSiteBind #" + pkey + ": " + protocol);
		}
		if(certificate == -1) return null;
		// May be filtered
		return table.getConnector().getPki().getCertificate().get(certificate);
	}

	@Override
	public DisableLog getDisableLog() throws SQLException, IOException {
		if(disable_log == -1) return null;
		DisableLog obj = table.getConnector().getAccount().getDisableLog().get(disable_log);
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
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.DISABLE, Table.TableID.HTTPD_SITE_BINDS, dl.getPkey(), pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.ENABLE, Table.TableID.HTTPD_SITE_BINDS, pkey);
	}

	public String getPredisableConfig() {
		return predisable_config;
	}

	public void setPredisableConfig(final String config) throws IOException, SQLException {
		table.getConnector().requestUpdate(
			true,
			AoservProtocol.CommandID.SET_HTTPD_SITE_BIND_PREDISABLE_CONFIG,
			new AOServConnector.UpdateRequest() {
				private IntList invalidateList;

				@Override
				public void writeRequest(StreamableOutput out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeNullUTF(config);
				}

				@Override
				public void readResponse(StreamableInput in) throws IOException, SQLException {
					int code = in.readByte();
					if(code == AoservProtocol.DONE) invalidateList = AOServConnector.readInvalidateList(in);
					else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: " + code);
					}
				}

				@Override
				public void afterRelease() {
					table.getConnector().tablesUpdated(invalidateList);
				}
			}
		);
	}

	public boolean isManual() {
		return isManual;
	}

	public void setIsManual(boolean isManual) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_HTTPD_SITE_BIND_IS_MANUAL, pkey, isManual);
	}

	public boolean getRedirectToPrimaryHostname() {
		return redirect_to_primary_hostname;
	}

	public void setRedirectToPrimaryHostname(boolean redirectToPrimaryHostname) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_HTTPD_SITE_BIND_REDIRECT_TO_PRIMARY_HOSTNAME, pkey, redirectToPrimaryHostname);
	}

	/**
	 * Controls whether this bind includes the per-site configuration file.
	 * Will be one of:
	 * <ul>
	 * <li>{@code null} - Automatic mode</li>
	 * <li>{@code "true"} - Include manually enabled</li>
	 * <li>{@code "false"} - Include manually disabled</li>
	 * <li>{@code "IfModule <module_name>"} - Include when a module is enabled</li>
	 * <li>{@code "IfModule !<module_name>"} - Include when a module is disabled</li>
	 * <li>Any future unrecognized value should be treated as equivalent to {@code null} (automatic mode)</li>
	 * </ul>
	 */
	public String getIncludeSiteConfig() {
		return include_site_config;
	}

	public List<VirtualHostName> getHttpdSiteURLs() throws IOException, SQLException {
		return table.getConnector().getWeb().getVirtualHostName().getHttpdSiteURLs(this);
	}

	public VirtualHostName getPrimaryHttpdSiteURL() throws SQLException, IOException {
		return table.getConnector().getWeb().getVirtualHostName().getPrimaryHttpdSiteURL(this);
	}

	public List<VirtualHostName> getAltHttpdSiteURLs() throws IOException, SQLException {
		return table.getConnector().getWeb().getVirtualHostName().getAltHttpdSiteURLs(this);
	}

	public int addHttpdSiteURL(DomainName hostname) throws IOException, SQLException {
		return table.getConnector().getWeb().getVirtualHostName().addHttpdSiteURL(this, hostname);
	}

	public List<Header> getHttpdSiteBindHeaders() throws IOException, SQLException {
		return table.getConnector().getWeb().getHeader().getHttpdSiteBindHeaders(this);
	}

	public List<RewriteRule> getRewriteRules() throws IOException, SQLException {
		return table.getConnector().getWeb().getRewriteRule().getRewriteRules(this);
	}
}
