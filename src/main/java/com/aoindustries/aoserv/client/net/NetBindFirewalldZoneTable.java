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

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  NetBindFirewalldZone
 *
 * @author  AO Industries, Inc.
 */
final public class NetBindFirewalldZoneTable extends CachedTableIntegerKey<NetBindFirewalldZone> {

	public NetBindFirewalldZoneTable(AOServConnector connector) {
		super(connector, NetBindFirewalldZone.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(NetBindFirewalldZone.COLUMN_NET_BIND_name + '.' + NetBind.COLUMN_SERVER_name + '.' + Server.COLUMN_PACKAGE_name + '.' + Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(NetBindFirewalldZone.COLUMN_NET_BIND_name + '.' + NetBind.COLUMN_SERVER_name + '.' + Server.COLUMN_NAME_name, ASCENDING),
		new OrderBy(NetBindFirewalldZone.COLUMN_NET_BIND_name + '.' + NetBind.COLUMN_IP_ADDRESS_name + '.' + IPAddress.COLUMN_IP_ADDRESS_name, ASCENDING),
		new OrderBy(NetBindFirewalldZone.COLUMN_NET_BIND_name + '.' + NetBind.COLUMN_IP_ADDRESS_name + '.' + IPAddress.COLUMN_DEVICE_name + '.' + NetDevice.COLUMN_DEVICE_ID_name, ASCENDING),
		new OrderBy(NetBindFirewalldZone.COLUMN_NET_BIND_name + '.' + NetBind.COLUMN_PORT_name, ASCENDING),
		new OrderBy(NetBindFirewalldZone.COLUMN_FIREWALLD_ZONE_name + '.' + FirewalldZone.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public NetBindFirewalldZone get(int pkey) throws IOException, SQLException {
		return getUniqueRow(NetBindFirewalldZone.COLUMN_PKEY, pkey);
	}

	// TODO: Call from NetBind
	List<NetBindFirewalldZone> getNetBindFirewalldZones(NetBind nb) throws IOException, SQLException {
		return getIndexedRows(NetBindFirewalldZone.COLUMN_NET_BIND, nb.getId());
	}

	// TODO: Call from FirewalldZone
	List<NetBindFirewalldZone> getNetBindFirewalldZones(FirewalldZone fz) throws IOException, SQLException {
		return getIndexedRows(NetBindFirewalldZone.COLUMN_FIREWALLD_ZONE, fz.getPkey());
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.NET_BIND_FIREWALLD_ZONES;
	}
}
