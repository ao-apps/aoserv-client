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
package com.aoindustries.aoserv.client.net;

import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Each port may be independently associated with a specific set of
 * {@link FirewalldZone Firewalld Zones}.
 *
 * @author  AO Industries, Inc.
 */
final public class NetBindFirewalldZone extends CachedObjectIntegerKey<NetBindFirewalldZone> {

	static final int
		COLUMN_PKEY = 0,
		COLUMN_NET_BIND = 1,
		COLUMN_FIREWALLD_ZONE = 2
	;
	static final String COLUMN_NET_BIND_name = "net_bind";
	static final String COLUMN_FIREWALLD_ZONE_name = "firewalld_zone";

	int net_bind;
	int firewalld_zone;

	public NetBind getNetBind() throws SQLException, IOException {
		NetBind obj = table.getConnector().getNetBinds().get(net_bind);
		if(obj == null) throw new SQLException("Unable to find NetBind: " + net_bind);
		return obj;
	}

	public FirewalldZone getFirewalldZone() throws SQLException, IOException {
		FirewalldZone obj = table.getConnector().getFirewalldZones().get(firewalld_zone);
		if(obj == null) throw new SQLException("Unable to find FirewalldZone: " + firewalld_zone);
		return obj;
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_NET_BIND: return net_bind;
			case COLUMN_FIREWALLD_ZONE: return firewalld_zone;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.NET_BIND_FIREWALLD_ZONES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getInt(1);
		net_bind = result.getInt(2);
		firewalld_zone = result.getInt(3);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey = in.readCompressedInt();
		net_bind = in.readCompressedInt();
		firewalld_zone = in.readCompressedInt();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(net_bind);
		out.writeCompressedInt(firewalld_zone);
	}
}
