/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2018, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.net.monitoring;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.net.Device;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.net.IpAddress;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;

/**
 * @see  IpAddressMonitoring
 *
 * @author  AO Industries, Inc.
 */
public final class IpAddressMonitoringTable extends CachedTableIntegerKey<IpAddressMonitoring> {

	IpAddressMonitoringTable(AOServConnector connector) {
		super(connector, IpAddressMonitoring.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(IpAddressMonitoring.COLUMN_ID_name + '.' + IpAddress.COLUMN_IP_ADDRESS_name, ASCENDING),
		new OrderBy(IpAddressMonitoring.COLUMN_ID_name + '.' + IpAddress.COLUMN_DEVICE_name+'.'+Device.COLUMN_SERVER_name+'.'+Host.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(IpAddressMonitoring.COLUMN_ID_name + '.' + IpAddress.COLUMN_DEVICE_name+'.'+Device.COLUMN_SERVER_name+'.'+Host.COLUMN_NAME_name, ASCENDING),
		new OrderBy(IpAddressMonitoring.COLUMN_ID_name + '.' + IpAddress.COLUMN_DEVICE_name+'.'+Device.COLUMN_DEVICE_ID_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public IpAddressMonitoring get(int id) throws IOException, SQLException {
		return getUniqueRow(IpAddressMonitoring.COLUMN_ID, id);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.IpAddressMonitoring;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.SET_IP_ADDRESS_MONITORING_ENABLED)) {
			if(AOSH.checkParamCount(Command.SET_IP_ADDRESS_MONITORING_ENABLED, args, 4, err)) {
				connector.getSimpleAOClient().setIPAddressMonitoringEnabled(
					AOSH.parseInetAddress(args[1], "ip_address"),
					args[2],
					args[3],
					AOSH.parseBoolean(args[4], "enabled")
				);
			}
			return true;
		}
		return false;
	}
}
