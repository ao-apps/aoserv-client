/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2018, 2019  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  BindFirewallZone
 *
 * @author  AO Industries, Inc.
 */
final public class BindFirewallZoneTable extends CachedTableIntegerKey<BindFirewallZone> {

	BindFirewallZoneTable(AOServConnector connector) {
		super(connector, BindFirewallZone.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(BindFirewallZone.COLUMN_NET_BIND_name + '.' + Bind.COLUMN_SERVER_name + '.' + Host.COLUMN_PACKAGE_name + '.' + Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(BindFirewallZone.COLUMN_NET_BIND_name + '.' + Bind.COLUMN_SERVER_name + '.' + Host.COLUMN_NAME_name, ASCENDING),
		new OrderBy(BindFirewallZone.COLUMN_NET_BIND_name + '.' + Bind.COLUMN_IP_ADDRESS_name + '.' + IpAddress.COLUMN_IP_ADDRESS_name, ASCENDING),
		new OrderBy(BindFirewallZone.COLUMN_NET_BIND_name + '.' + Bind.COLUMN_IP_ADDRESS_name + '.' + IpAddress.COLUMN_DEVICE_name + '.' + Device.COLUMN_DEVICE_ID_name, ASCENDING),
		new OrderBy(BindFirewallZone.COLUMN_NET_BIND_name + '.' + Bind.COLUMN_PORT_name, ASCENDING),
		new OrderBy(BindFirewallZone.COLUMN_FIREWALLD_ZONE_name + '.' + FirewallZone.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public BindFirewallZone get(int pkey) throws IOException, SQLException {
		return getUniqueRow(BindFirewallZone.COLUMN_PKEY, pkey);
	}

	List<BindFirewallZone> getNetBindFirewalldZones(Bind nb) throws IOException, SQLException {
		return getIndexedRows(BindFirewallZone.COLUMN_NET_BIND, nb.getId());
	}

	List<BindFirewallZone> getNetBindFirewalldZones(FirewallZone fz) throws IOException, SQLException {
		return getIndexedRows(BindFirewallZone.COLUMN_FIREWALLD_ZONE, fz.getPkey());
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.NET_BIND_FIREWALLD_ZONES;
	}
}
