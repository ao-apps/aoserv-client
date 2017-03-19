/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2013, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.net.DomainName;
import com.aoindustries.net.Port;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Multiple <code>HttpdSiteURL</code>s may be attached to a unique
 * combination of <code>HttpdSite</code> and <code>HttpdBind</code>,
 * represented by an <code>HttpdSiteBind</code>.  This allows a web
 * site to respond to several different hostnames on the same IP/port
 * combination.
 *
 * @see  HttpdSiteBind
 * @see  HttpdSite
 * @see  HttpdBind
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdSiteURL extends CachedObjectIntegerKey<HttpdSiteURL> implements Removable {

	static final int
		COLUMN_PKEY=0,
		COLUMN_HTTPD_SITE_BIND=1
	;
	static final String COLUMN_HOSTNAME_name = "hostname";
	static final String COLUMN_HTTPD_SITE_BIND_name = "httpd_site_bind";

	int httpd_site_bind;
	private DomainName hostname;
	boolean isPrimary;

	@Override
	public List<CannotRemoveReason> getCannotRemoveReasons() throws SQLException, IOException {
		List<CannotRemoveReason> reasons=new ArrayList<>();

		if(isPrimary) reasons.add(new CannotRemoveReason<>("Not allowed to remove the primary URL", this));
		if(isTestURL()) reasons.add(new CannotRemoveReason<>("Not allowed to remove the test URL", this));

		return reasons;
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_HTTPD_SITE_BIND: return httpd_site_bind;
			case 2: return hostname;
			case 3: return isPrimary;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public DomainName getHostname() {
		return hostname;
	}

	public HttpdSiteBind getHttpdSiteBind() throws SQLException, IOException {
		HttpdSiteBind obj=table.connector.getHttpdSiteBinds().get(httpd_site_bind);
		if(obj==null) throw new SQLException("Unable to find HttpdSiteBind: "+httpd_site_bind);
		return obj;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_SITE_URLS;
	}

	public String getURL() throws SQLException, IOException {
		HttpdSiteBind siteBind=getHttpdSiteBind();
		NetBind netBind=siteBind.getHttpdBind().getNetBind();
		Port port=netBind.getPort();
		StringBuilder url=new StringBuilder();
		String protocol;
		if(siteBind.getSSLCertFile()==null) {
			// If HTTP
			url.append("http://");
			protocol=Protocol.HTTP;
		} else {
			// Otherwise must be HTTPS
			url.append("https://");
			protocol=Protocol.HTTPS;
		}
		url.append(hostname);
		if(!port.equals(table.connector.getProtocols().get(protocol).getPort())) url.append(':').append(port.getPort());
		url.append('/');
		return url.toString();
	}

	public String getURLNoSlash() throws SQLException, IOException {
		HttpdSiteBind siteBind=getHttpdSiteBind();
		NetBind netBind=siteBind.getHttpdBind().getNetBind();
		Port port=netBind.getPort();
		StringBuilder url=new StringBuilder();
		String protocol;
		if(siteBind.getSSLCertFile()==null) {
			// If HTTP
			url.append("http://");
			protocol=Protocol.HTTP;
		} else {
			// Otherwise must be HTTPS
			url.append("https://");
			protocol=Protocol.HTTPS;
		}
		url.append(hostname);
		if(!port.equals(table.connector.getProtocols().get(protocol).getPort())) url.append(':').append(port.getPort());
		return url.toString();
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey=result.getInt(1);
			httpd_site_bind=result.getInt(2);
			hostname=DomainName.valueOf(result.getString(3));
			isPrimary=result.getBoolean(4);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public boolean isTestURL() throws SQLException, IOException {
		HttpdSite hs=getHttpdSiteBind().getHttpdSite();
		return hostname.toString().equalsIgnoreCase(hs.getSiteName()+"."+hs.getAOServer().getHostname());
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey=in.readCompressedInt();
			httpd_site_bind=in.readCompressedInt();
			hostname=DomainName.valueOf(in.readUTF());
			isPrimary=in.readBoolean();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.HTTPD_SITE_URLS, pkey);
	}

	public void setAsPrimary() throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_PRIMARY_HTTPD_SITE_URL, pkey);
	}

	@Override
	String toStringImpl() {
		return hostname.toString();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(httpd_site_bind);
		out.writeUTF(hostname.toString());
		out.writeBoolean(isPrimary);
	}
}
