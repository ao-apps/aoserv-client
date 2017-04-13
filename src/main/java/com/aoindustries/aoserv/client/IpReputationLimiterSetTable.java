/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2012, 2016, 2017  AO Industries, Inc.
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
 * @see  IpReputationLimiterSet
 *
 * @author  AO Industries, Inc.
 */
final public class IpReputationLimiterSetTable extends CachedTableIntegerKey<IpReputationLimiterSet> {

	IpReputationLimiterSetTable(AOServConnector connector) {
		super(connector, IpReputationLimiterSet.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(IpReputationLimiterSet.COLUMN_LIMITER_name+'.'+IpReputationLimiter.COLUMN_NET_DEVICE_name+'.'+NetDevice.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(IpReputationLimiterSet.COLUMN_LIMITER_name+'.'+IpReputationLimiter.COLUMN_NET_DEVICE_name+'.'+NetDevice.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
		new OrderBy(IpReputationLimiterSet.COLUMN_LIMITER_name+'.'+IpReputationLimiter.COLUMN_NET_DEVICE_name+'.'+NetDevice.COLUMN_DEVICE_ID_name, ASCENDING),
		new OrderBy(IpReputationLimiterSet.COLUMN_LIMITER_name+'.'+IpReputationLimiter.COLUMN_IDENTIFIER_name, ASCENDING),
		new OrderBy(IpReputationLimiterSet.COLUMN_SORT_ORDER_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public IpReputationLimiterSet get(int pkey) throws IOException, SQLException {
		return getUniqueRow(IpReputationLimiterSet.COLUMN_PKEY, pkey);
	}

	List<IpReputationLimiterSet> getSets(IpReputationLimiter limiter) throws IOException, SQLException {
		return getIndexedRows(IpReputationLimiterSet.COLUMN_LIMITER, limiter.getPkey());
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.IP_REPUTATION_LIMITER_SETS;
	}
}
