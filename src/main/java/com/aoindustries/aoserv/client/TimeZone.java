/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2006-2009, 2016, 2017  AO Industries, Inc.
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
 * All of the time zones on a server.
 *
 * @author  AO Industries, Inc.
 */
final public class TimeZone extends GlobalObjectStringKey<TimeZone> {

	static final int COLUMN_NAME=0;
	static final String COLUMN_NAME_name = "name";

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_NAME: return pkey;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	/**
	 * Gets the unique name for this time zone.
	 */
	public String getName() {
		return pkey;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.TIME_ZONES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getString(1);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF().intern();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey);
	}

	private java.util.TimeZone timeZone;

	/**
	 * Gets the Java TimeZone for this TimeZone.
	 * 
	 * Not synchronized because double initialization is acceptable.
	 */
	public java.util.TimeZone getTimeZone() {
		if(timeZone==null) {
			String[] ids = java.util.TimeZone.getAvailableIDs();
			boolean found = false;
			for(String id : ids) {
				if(id.equals(pkey)) {
					found = true;
					break;
				}
			}
			if(!found) throw new IllegalArgumentException("TimeZone not found: "+pkey);
			timeZone = java.util.TimeZone.getTimeZone(pkey);
		}
		return timeZone;
	}
}
