/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017  AO Industries, Inc.
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
		COLUMN_PKEY=0,
		COLUMN_HTTPD_SITE=1
	;
	static final String COLUMN_HTTPD_SITE_name = "httpd_site";
	static final String COLUMN_HTTPD_BIND_name = "httpd_bind";

	int httpd_site;
	private int httpd_bind;
	private UnixPath access_log;
	private UnixPath error_log;
	private UnixPath sslCertFile;
	private UnixPath sslCertKeyFile;
	int disable_log;
	private String predisable_config;
	private boolean isManual;
	private boolean redirect_to_primary_hostname;

	public int addHttpdSiteURL(DomainName hostname) throws IOException, SQLException {
		return table.connector.getHttpdSiteURLs().addHttpdSiteURL(this, hostname);
	}

	@Override
	public boolean canDisable() {
		return disable_log==-1;
	}

	@Override
	public boolean canEnable() throws SQLException, IOException {
		DisableLog dl=getDisableLog();
		if(dl==null) return false;
		else return dl.canEnable() && getHttpdSite().disable_log==-1;
	}

	@Override
	public void disable(DisableLog dl) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.HTTPD_SITE_BINDS, dl.pkey, pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.HTTPD_SITE_BINDS, pkey);
	}

	public UnixPath getAccessLog() {
		return access_log;
	}

	public List<HttpdSiteURL> getAltHttpdSiteURLs() throws IOException, SQLException {
		return table.connector.getHttpdSiteURLs().getAltHttpdSiteURLs(this);
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_HTTPD_SITE: return httpd_site;
			case 2: return httpd_bind;
			case 3: return access_log;
			case 4: return error_log;
			case 5: return sslCertFile;
			case 6: return sslCertKeyFile;
			case 7: return disable_log == -1 ? null : disable_log;
			case 8: return predisable_config;
			case 9: return isManual;
			case 10: return redirect_to_primary_hostname;
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

	public UnixPath getErrorLog() {
		return error_log;
	}

	public HttpdBind getHttpdBind() throws SQLException, IOException {
		HttpdBind obj=table.connector.getHttpdBinds().get(httpd_bind);
		if(obj==null) throw new SQLException("Unable to find HttpdBind: "+httpd_bind+" for HttpdSite="+httpd_site);
		return obj;
	}

	public HttpdSite getHttpdSite() throws SQLException, IOException {
		HttpdSite obj=table.connector.getHttpdSites().get(httpd_site);
		if(obj==null) throw new SQLException("Unable to find HttpdSite: "+httpd_site);
		return obj;
	}

	public List<HttpdSiteURL> getHttpdSiteURLs() throws IOException, SQLException {
		return table.connector.getHttpdSiteURLs().getHttpdSiteURLs(this);
	}

	public String getPredisableConfig() {
		return predisable_config;
	}

	public HttpdSiteURL getPrimaryHttpdSiteURL() throws SQLException, IOException {
		return table.connector.getHttpdSiteURLs().getPrimaryHttpdSiteURL(this);
	}

	public UnixPath getSSLCertFile() {
		return sslCertFile;
	}

	public UnixPath getSSLCertKeyFile() {
		return sslCertKeyFile;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_SITE_BINDS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey=result.getInt(1);
			httpd_site=result.getInt(2);
			httpd_bind=result.getInt(3);
			access_log = UnixPath.valueOf(result.getString(4));
			error_log = UnixPath.valueOf(result.getString(5));
			sslCertFile = UnixPath.valueOf(result.getString(6));
			sslCertKeyFile = UnixPath.valueOf(result.getString(7));
			disable_log=result.getInt(8);
			if(result.wasNull()) disable_log=-1;
			predisable_config=result.getString(9);
			isManual=result.getBoolean(10);
			redirect_to_primary_hostname=result.getBoolean(11);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	public boolean isManual() {
		return isManual;
	}

	public boolean getRedirectToPrimaryHostname() {
		return redirect_to_primary_hostname;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey=in.readCompressedInt();
			httpd_site=in.readCompressedInt();
			httpd_bind=in.readCompressedInt();
			access_log = UnixPath.valueOf(in.readUTF());
			error_log = UnixPath.valueOf(in.readUTF());
			sslCertFile = UnixPath.valueOf(in.readNullUTF());
			sslCertKeyFile = UnixPath.valueOf(in.readNullUTF());
			disable_log=in.readCompressedInt();
			predisable_config=in.readNullUTF();
			isManual=in.readBoolean();
			redirect_to_primary_hostname=in.readBoolean();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	public void setIsManual(boolean isManual) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_HTTPD_SITE_BIND_IS_MANUAL, pkey, isManual);
	}

	public void setRedirectToPrimaryHostname(boolean redirectToPrimaryHostname) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_HTTPD_SITE_BIND_REDIRECT_TO_PRIMARY_HOSTNAME, pkey, redirectToPrimaryHostname);
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
					int code=in.readByte();
					if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
					else {
						AOServProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public void afterRelease() {
					table.connector.tablesUpdated(invalidateList);
				}
			}
		);
	}

	@Override
	String toStringImpl() throws SQLException, IOException {
		HttpdSite site=getHttpdSite();
		HttpdBind bind=getHttpdBind();
		return site.toStringImpl()+'|'+bind.toStringImpl();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(httpd_site);
		out.writeCompressedInt(httpd_bind);
		out.writeUTF(access_log.toString());
		out.writeUTF(error_log.toString());
		out.writeNullUTF(ObjectUtils.toString(sslCertFile));
		out.writeNullUTF(ObjectUtils.toString(sslCertKeyFile));
		out.writeCompressedInt(disable_log);
		out.writeNullUTF(predisable_config);
		out.writeBoolean(isManual);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_19)>=0) out.writeBoolean(redirect_to_primary_hostname);
	}
}
