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
import com.aoindustries.util.InternUtils;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>SchemaForeignKey</code> represents when a column in one
 * <code>AOServTable</code> references a column in another
 * <code>AOServTable</code>.
 *
 * @see  SchemaColumn
 *
 * @author  AO Industries, Inc.
 */
final public class SchemaForeignKey extends GlobalObjectIntegerKey<SchemaForeignKey> {

	static final int COLUMN_PKEY=0;
	static final String COLUMN_PKEY_name = "pkey";

	int
		key_column,
		foreign_column
	;
	private String since_version;
	private String last_version;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case 1: return key_column;
			case 2: return foreign_column;
			case 3: return since_version;
			case 4: return last_version;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public SchemaColumn getForeignColumn(AOServConnector connector) throws SQLException, IOException {
		SchemaColumn obj=connector.getSchemaColumns().get(foreign_column);
		if(obj==null) throw new SQLException("Unable to find SchemaColumn: "+foreign_column);
		return obj;
	}

	public SchemaColumn getKeyColumn(AOServConnector connector) throws SQLException, IOException {
		SchemaColumn obj=connector.getSchemaColumns().get(key_column);
		if(obj==null) throw new SQLException("Unable to find SchemaColumn: "+key_column);
		return obj;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.SCHEMA_FOREIGN_KEYS;
	}

	public String getSinceVersion() {
		return since_version;
	}

	public String getLastVersion() {
		return last_version;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getInt(1);
		key_column = result.getInt(2);
		foreign_column = result.getInt(3);
		since_version=result.getString(4);
		last_version=result.getString(5);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey = in.readCompressedInt();
		key_column = in.readCompressedInt();
		foreign_column = in.readCompressedInt();
		since_version=in.readUTF().intern();
		last_version=InternUtils.intern(in.readNullUTF());
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(key_column);
		out.writeCompressedInt(foreign_column);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
			out.writeBoolean(false); // is_bridge
			out.writeCompressedInt(-1); // tied_bridge
		}
		if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_101)>=0) out.writeUTF(since_version);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_104)>=0) out.writeNullUTF(last_version);
	}
}
