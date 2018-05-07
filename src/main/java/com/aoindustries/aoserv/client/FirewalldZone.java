/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2018  AO Industries, Inc.
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
import com.aoindustries.util.InternUtils;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines a firewalld zone that exists on a {@link Server}.
 *
 * @author  AO Industries, Inc.
 */
public final class FirewalldZone extends CachedObjectIntegerKey<FirewalldZone> {

	/**
	 * Some Firewalld Zone names used within code.
	 */
	public static final FirewalldZoneName
		DMZ,
		EXTERNAL,
		HOME,
		INTERNAL,
		PUBLIC,
		WORK
	;
	static {
		try {
			DMZ = FirewalldZoneName.valueOf("dmz");
			EXTERNAL = FirewalldZoneName.valueOf("external");
			HOME = FirewalldZoneName.valueOf("home");
			INTERNAL = FirewalldZoneName.valueOf("internal");
			PUBLIC = FirewalldZoneName.valueOf("public");
			WORK = FirewalldZoneName.valueOf("work");
		} catch(ValidationException e) {
			throw new AssertionError("These hard-coded values are valid", e);
		}
	}

	static final int
		COLUMN_PKEY = 0,
		COLUMN_SERVER = 1
	;
	static final String COLUMN_SERVER_name = "server";
	static final String COLUMN_NAME_name = "name";

	int server;
	private FirewalldZoneName name;
	private String _short;
	private String description;
	private boolean fail2ban;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_SERVER: return server;
			case 2: return name;
			case 3: return _short;
			case 4: return description;
			case 5: return fail2ban;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public Server getServer() throws SQLException, IOException {
		Server se = table.connector.getServers().get(server);
		if(se == null) throw new SQLException("Unable to find Server: " + server);
		return se;
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

	public boolean getFail2ban() {
		return fail2ban;
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
			pkey        = result.getInt(1);
			server      = result.getInt(2);
			name        = FirewalldZoneName.valueOf(result.getString(3));
			_short      = result.getString(4);
			description = result.getString(5);
			fail2ban    = result.getBoolean(6);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = in.readCompressedInt();
			server = in.readCompressedInt();
			name = FirewalldZoneName.valueOf(in.readUTF()).intern();
			_short = InternUtils.intern(in.readNullUTF());
			description = InternUtils.intern(in.readNullUTF());
			fail2ban = in.readBoolean();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	String toStringImpl() {
		return server + ":" + name;
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(server);
		out.writeUTF(name.toString());
		out.writeNullUTF(_short);
		out.writeNullUTF(description);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_81_9) >= 0) {
			out.writeBoolean(fail2ban);
		}
	}
}
