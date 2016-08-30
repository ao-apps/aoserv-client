/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2000-2013, 2016  AO Industries, Inc.
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
 * Several resources on a <code>Server</code> require a server-wide
 * unique identifier.  All of the possible identifiers are represented
 * by <code>LinuxID</code>s.
 *
 * @see  LinuxServerAccount
 * @see  LinuxServerGroup
 * @see  PostgresServerUser
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxID extends AOServObject<Integer,LinuxID> {

	int id;

	LinuxID(int id) {
		this.id=id;
	}

	@Override
	boolean equalsImpl(Object O) {
		return
			O instanceof LinuxID
			&& ((LinuxID)O).id==id
		;
	}

	@Override
	Object getColumnImpl(int i) {
		if(i==0) return id;
		if(i==1) return isSystem();
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public int getID() {
		return id;
	}

	@Override
	public Integer getKey() {
		return id;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.LINUX_IDS;
	}

	@Override
	int hashCodeImpl() {
		return id;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		throw new SQLException("Should not be read from the database, should be generated.");
	}

	public boolean isSystem() {
		return id < 1000 || id==65534 || id==65535;
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
