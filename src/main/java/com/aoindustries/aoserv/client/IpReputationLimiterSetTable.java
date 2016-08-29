/*
 * Copyright 2012, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
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
