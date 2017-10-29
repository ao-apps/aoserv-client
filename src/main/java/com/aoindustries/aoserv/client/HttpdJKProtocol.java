/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2009, 2016, 2017  AO Industries, Inc.
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
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Apache's <code>mod_jk</code> supports multiple versions of the
 * Apache JServ Protocol.  Both Apache and Tomcat must be using
 * the same protocol for communication.  The protocol is represented
 * by an <code>HttpdJKProtocol</code>.
 *
 * @see  HttpdWorker
 * @see  Protocol
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdJKProtocol extends GlobalObjectStringKey<HttpdJKProtocol> {

	static final int COLUMN_PROTOCOL=0;
	static final String COLUMN_PROTOCOL_name = "protocol";

	public static final String
		AJP12="ajp12",
		AJP13="ajp13"
	;

	@Override
	Object getColumnImpl(int i) {
		if(i==COLUMN_PROTOCOL) return pkey;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public Protocol getProtocol(AOServConnector connector) throws SQLException, IOException {
		Protocol protocol=connector.getProtocols().get(pkey);
		if(protocol==null) throw new SQLException("Unable to find Protocol: "+pkey);
		return protocol;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_JK_PROTOCOLS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getString(1);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF().intern();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey);
	}
}
