/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2004-2009, 2016, 2017, 2018, 2020  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.net;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  TcpRedirect
 *
 * @author  AO Industries, Inc.
 */
public final class TcpRedirectTable extends CachedTableIntegerKey<TcpRedirect> {

	TcpRedirectTable(AOServConnector connector) {
		super(connector, TcpRedirect.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(TcpRedirect.COLUMN_NET_BIND_name+'.'+Bind.COLUMN_SERVER_name+'.'+Host.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(TcpRedirect.COLUMN_NET_BIND_name+'.'+Bind.COLUMN_SERVER_name+'.'+Host.COLUMN_NAME_name, ASCENDING),
		new OrderBy(TcpRedirect.COLUMN_NET_BIND_name+'.'+Bind.COLUMN_IP_ADDRESS_name+'.'+IpAddress.COLUMN_IP_ADDRESS_name, ASCENDING),
		new OrderBy(TcpRedirect.COLUMN_NET_BIND_name+'.'+Bind.COLUMN_IP_ADDRESS_name+'.'+IpAddress.COLUMN_DEVICE_name+'.'+Device.COLUMN_DEVICE_ID_name, ASCENDING),
		new OrderBy(TcpRedirect.COLUMN_NET_BIND_name+'.'+Bind.COLUMN_PORT_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public TcpRedirect get(int pkey) throws IOException, SQLException {
		return getUniqueRow(TcpRedirect.COLUMN_NET_BIND, pkey);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.NET_TCP_REDIRECTS;
	}
}
