/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017  AO Industries, Inc.
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

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  NetDevice
 *
 * @author  AO Industries, Inc.
 */
final public class NetDeviceTable extends CachedTableIntegerKey<NetDevice> {

	NetDeviceTable(AOServConnector connector) {
	super(connector, NetDevice.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(NetDevice.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(NetDevice.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
		new OrderBy(NetDevice.COLUMN_DEVICE_ID_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public NetDevice get(int pkey) throws IOException, SQLException {
		return getUniqueRow(NetDevice.COLUMN_PKEY, pkey);
	}

	List<NetDevice> getNetDevices(Server se) throws IOException, SQLException {
		return getIndexedRows(NetDevice.COLUMN_SERVER, se.pkey);
	}

	NetDevice getNetDevice(Server se, String deviceID) throws IOException, SQLException {
		// Use the index first
		List<NetDevice> cached=getNetDevices(se);
		int size=cached.size();
		for(int c=0;c<size;c++) {
			NetDevice dev=cached.get(c);
			if(dev.device_id.equals(deviceID)) return dev;
		}
		return null;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.NET_DEVICES;
	}
}
