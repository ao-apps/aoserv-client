/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.validator.FirewalldZoneName;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines a firewalld zone that exists on an {@link AOServer}.
 *
 * @author  AO Industries, Inc.
 */
public final class FirewalldZone extends CachedObjectIntegerKey<FirewalldZone> {

	/**
	 * Some Firewalld Zone names used within code.
	 */
	public static final FirewalldZoneName PUBLIC;
	static {
		try {
			PUBLIC = FirewalldZoneName.valueOf("public");
		} catch(ValidationException e) {
			throw new AssertionError("These hard-coded values are valid", e);
		}
	}

	static final int
		COLUMN_PKEY = 0,
		COLUMN_AO_SERVER = 1
	;
	static final String COLUMN_AO_SERVER_name = "ao_server";
	static final String COLUMN_NAME_name = "name";

	int aoServer;
	private FirewalldZoneName name;
	private String _short;
	private String description;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_AO_SERVER: return aoServer;
			case 2: return name;
			case 3: return _short;
			case 4: return description;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public AOServer getAOServer() throws SQLException, IOException {
		AOServer ao = table.connector.getAoServers().get(aoServer);
		if(ao == null) throw new SQLException("Unable to find AOServer: " + aoServer);
		return ao;
	}

	public FirewalldZoneName getName() {
		return name;
	}

	public String getShort() {
		return _short;
	}

	public String getDescription() {
		return description;
	}

	public List<NetBindFirewalldZone> getNetBindFirewalldZones() throws IOException, SQLException {
		return table.connector.getNetBindFirewalldZones().getNetBindFirewalldZones(this);
	}

	public List<NetBind> getNetBinds() throws IOException, SQLException {
		List<NetBindFirewalldZone> nbfzs = getNetBindFirewalldZones();
		List<NetBind> nbs = new ArrayList<>(nbfzs.size());
		for(NetBindFirewalldZone nbfz : nbfzs) {
			nbs.add(nbfz.getNetBind());
		}
		return nbs;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.FIREWALLD_ZONES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = result.getInt(1);
			aoServer = result.getInt(2);
			name = FirewalldZoneName.valueOf(result.getString(3));
			_short = result.getString(4);
			description = result.getString(5);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = in.readCompressedInt();
			aoServer = in.readCompressedInt();
			name = FirewalldZoneName.valueOf(in.readUTF()).intern();
			_short = in.readNullUTF();
			description = in.readNullUTF();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	String toStringImpl() {
		return aoServer + ":" + name;
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(aoServer);
		out.writeUTF(name.toString());
		out.writeNullUTF(_short);
		out.writeNullUTF(description);
	}
}
