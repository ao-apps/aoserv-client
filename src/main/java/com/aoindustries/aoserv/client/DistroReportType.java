/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2013, 2016  AO Industries, Inc.
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
 * For AO Industries use only.
 *
 * @author  AO Industries, Inc.
 */
final public class DistroReportType extends GlobalObjectStringKey<DistroReportType> {

	static final int COLUMN_NAME=0;
	static final String COLUMN_NAME_name = "name";

	private String display;

	/**
	 * The different report types.
	 */
	public static final String
		BIG_DIRECTORY  = "BD",
		EXTRA          = "EX",
		GROUP_MISMATCH = "GR",
		HIDDEN         = "HI",
		LENGTH         = "LN",
		MD5            = "M5",
		MISSING        = "MI",
		OWNER_MISMATCH = "OW",
		NO_OWNER       = "NO",
		NO_GROUP       = "NG",
		PERMISSIONS    = "PR",
		SETUID         = "SU",
		SYMLINK        = "SY",
		TYPE           = "TY"
	;

	@Override
	String toStringImpl() {
		return display;
	}

	@Override
	Object getColumnImpl(int i) {
		if(i==COLUMN_NAME) return pkey;
		if(i==1) return display;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public String getDisplay() {
		return display;
	}

	public String getName() {
		return pkey;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.DISTRO_REPORT_TYPES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getString(1);
		display = result.getString(2);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF().intern();
		display=in.readUTF();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey);
		out.writeUTF(display);
	}
}
