/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2021  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.schema;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.util.InternUtils;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.GlobalObjectIntegerKey;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>SchemaForeignKey</code> represents when a column in one
 * <code>AOServTable</code> references a column in another
 * <code>AOServTable</code>.
 *
 * @see  Column
 *
 * @author  AO Industries, Inc.
 */
public final class ForeignKey extends GlobalObjectIntegerKey<ForeignKey> {

	static final int COLUMN_ID = 0;
	static final String COLUMN_ID_name = "id";

	private int column;
	private int foreignColumn;
	private String sinceVersion;
	private String lastVersion;

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_ID: return pkey;
			case 1: return column;
			case 2: return foreignColumn;
			case 3: return sinceVersion;
			case 4: return lastVersion;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public int getColumn_id() {
		return column;
	}

	public Column getColumn(AOServConnector connector) throws SQLException, IOException {
		Column obj = connector.getSchema().getColumn().get(column);
		if(obj == null) throw new SQLException("Unable to find SchemaColumn: " + column);
		return obj;
	}

	public int getForeignColumn_id() {
		return foreignColumn;
	}

	public Column getForeignColumn(AOServConnector connector) throws SQLException, IOException {
		Column obj = connector.getSchema().getColumn().get(foreignColumn);
		if(obj == null) throw new SQLException("Unable to find SchemaColumn: " + foreignColumn);
		return obj;
	}

	public String getSinceVersion_version() {
		return sinceVersion;
	}

	public AoservProtocol getSinceVersion(AOServConnector connector) throws SQLException, IOException {
		AoservProtocol obj = connector.getSchema().getAoservProtocol().get(sinceVersion);
		if(obj == null) throw new SQLException("Unable to find AOServProtocol: " + sinceVersion);
		return obj;
	}

	public String getLastVersion_version() {
		return lastVersion;
	}

	public AoservProtocol getLastVersion(AOServConnector connector) throws SQLException, IOException {
		if(lastVersion == null) return null;
		AoservProtocol obj = connector.getSchema().getAoservProtocol().get(lastVersion);
		if(obj == null) throw new SQLException("Unable to find AOServProtocol: " + lastVersion);
		return obj;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.SCHEMA_FOREIGN_KEYS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		int pos = 1;
		pkey = result.getInt(pos++);
		column = result.getInt(pos++);
		foreignColumn = result.getInt(pos++);
		sinceVersion = result.getString(pos++);
		lastVersion = result.getString(pos++);
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		pkey = in.readCompressedInt();
		column = in.readCompressedInt();
		foreignColumn = in.readCompressedInt();
		sinceVersion = in.readUTF().intern();
		lastVersion = InternUtils.intern(in.readNullUTF());
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(column);
		out.writeCompressedInt(foreignColumn);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30) <= 0) {
			out.writeBoolean(false); // is_bridge
			out.writeCompressedInt(-1); // tied_bridge
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_101) >= 0) out.writeUTF(sinceVersion);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_104) >= 0) out.writeNullUTF(lastVersion);
	}
}
