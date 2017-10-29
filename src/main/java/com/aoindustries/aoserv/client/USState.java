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
 * A <code>USState</code> represents State of the
 * United States.  This data will eventually merge
 * with <code>CountryCode</code>s to become a master
 * list of all states/providences and countries.
 *
 * @see  CountryCode
 *
 * @author  AO Industries, Inc.
 */
final public class USState extends GlobalObjectStringKey<USState> {

	static final int COLUMN_CODE=0;
	static final String COLUMN_NAME_name = "name";

	private String name;

	public String getCode() {
		return pkey;
	}

	@Override
	Object getColumnImpl(int i) {
		if(i==COLUMN_CODE) return pkey;
		if(i==1) return name;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public String getName() {
		return name;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.US_STATES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getString(1);
		name = result.getString(2);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF().intern();
		name=in.readUTF();
	}

	@Override
	String toStringImpl() {
		return name;
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey);
		out.writeUTF(name);
	}
}
