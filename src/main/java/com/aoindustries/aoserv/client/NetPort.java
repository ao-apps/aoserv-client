/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2013, 2016  AO Industries, Inc.
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
 * Several network resources on a <code>Server</code> require a unique
 * port.  All of the possible network ports are represented by
 * <code>NetPort</code>s.
 *
 * @author  AO Industries, Inc.
 */
final public class NetPort extends AOServObject<Integer,NetPort> {

	int port;

	NetPort(int port) {
		this.port=port;
	}

	@Override
	boolean equalsImpl(Object O) {
		return
			O instanceof NetPort
			&& ((NetPort)O).port==port
		;
	}

	@Override
	Object getColumnImpl(int i) {
		if(i==0) return port;
		if(i==1) return port>=1024;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public int getPort() {
		return port;
	}

	@Override
	public Integer getKey() {
		return port;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.NET_PORTS;
	}

	@Override
	int hashCodeImpl() {
		return port;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		throw new SQLException("Should not be read from the database, should be generated.");
	}

	public boolean isUser() {
		return port>=1024;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		throw new IOException("Should not be read from a stream, should be generated.");
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		throw new IOException("Should not be written to a stream, should be generated.");
	}
}
