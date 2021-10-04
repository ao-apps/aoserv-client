/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017, 2018, 2020, 2021  AO Industries, Inc.
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
 * @see  Device
 *
 * @author  AO Industries, Inc.
 */
public final class DeviceTable extends CachedTableIntegerKey<Device> {

	DeviceTable(AOServConnector connector) {
		super(connector, Device.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Device.COLUMN_SERVER_name+'.'+Host.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(Device.COLUMN_SERVER_name+'.'+Host.COLUMN_NAME_name, ASCENDING),
		new OrderBy(Device.COLUMN_DEVICE_ID_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public Device get(int id) throws IOException, SQLException {
		return getUniqueRow(Device.COLUMN_ID, id);
	}

	List<Device> getNetDevices(Host se) throws IOException, SQLException {
		return getIndexedRows(Device.COLUMN_SERVER, se.getPkey());
	}

	Device getNetDevice(Host se, String deviceID) throws IOException, SQLException {
		// Use the index first
		List<Device> cached=getNetDevices(se);
		int size=cached.size();
		for(int c=0;c<size;c++) {
			Device dev=cached.get(c);
			if(dev.getDeviceId_name().equals(deviceID)) return dev;
		}
		return null;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.NET_DEVICES;
	}
}
