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

import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Each <code>HttpdServer</code> may listen for network connections on
 * multiple <code>NetBind</code>s.  An <code>HttpdBind</code> ties
 * <code>HttpdServer</code>s to <code>NetBinds</code>.
 *
 * @see  HttpdServer
 * @see  Bind
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdBind extends CachedObjectIntegerKey<HttpdBind> {

	static final int
		COLUMN_NET_BIND=0,
		COLUMN_HTTPD_SERVER=1
	;
	static final String COLUMN_NET_BIND_name = "net_bind";

	int httpd_server;

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_NET_BIND: return pkey;
			case COLUMN_HTTPD_SERVER: return httpd_server;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public int getHttpdServer_pkey() {
		return httpd_server;
	}

	public HttpdServer getHttpdServer() throws SQLException, IOException {
		HttpdServer obj=table.getConnector().getWeb().getHttpdServer().get(httpd_server);
		if(obj==null) throw new SQLException("Unable to find HttpdServer: "+httpd_server);
		return obj;
	}

	public Bind getNetBind() throws SQLException, IOException {
		Bind obj=table.getConnector().getNet().getBind().get(pkey);
		if(obj==null) throw new SQLException("Unable to find NetBind: "+pkey);
		return obj;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.HTTPD_BINDS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		httpd_server=result.getInt(2);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		httpd_server=in.readCompressedInt();
	}

	@Override
	public String toStringImpl() throws SQLException, IOException {
		HttpdServer server=getHttpdServer();
		Bind bind=getNetBind();
		return server.toStringImpl()+'|'+bind.toStringImpl();
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(httpd_server);
	}
}
