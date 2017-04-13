/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017  AO Industries, Inc.
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
 * An <code>Architecture</code> is a simple wrapper for the type
 * of computer architecture used in a server.
 *
 * @see  Server
 *
 * @author  AO Industries, Inc.
 */
final public class Architecture extends GlobalObjectStringKey<Architecture> {

	static final int COLUMN_NAME=0;
	static final String COLUMN_NAME_name = "name";

	public static final String
		ALPHA="alpha",
		ARM="arm",
		I386="i386",
		I486="i486",
		I586="i586",
		I686="i686",
		I686_AND_X86_64="i686,x86_64",
		M68K="m68k",
		MIPS="mips",
		PPC="ppc",
		SPARC="sparc",
		X86_64="x86_64"
	;

	public static final String DEFAULT_ARCHITECTURE=I686;

	private int bits;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_NAME: return pkey;
			case 1: return bits;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public String getName() {
		return pkey;
	}

	public int getBits() {
		return bits;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.ARCHITECTURES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getString(1);
		bits=result.getInt(2);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF().intern();
		bits=in.readCompressedInt();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_108)>=0) out.writeCompressedInt(bits);
	}
}
