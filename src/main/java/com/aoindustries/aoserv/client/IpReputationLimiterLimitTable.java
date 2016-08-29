/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  IpReputationLimiterLimit
 *
 * @author  AO Industries, Inc.
 */
final public class IpReputationLimiterLimitTable extends CachedTableIntegerKey<IpReputationLimiterLimit> {

    IpReputationLimiterLimitTable(AOServConnector connector) {
	super(connector, IpReputationLimiterLimit.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(IpReputationLimiterLimit.COLUMN_LIMITER_name+'.'+IpReputationLimiter.COLUMN_NET_DEVICE_name+'.'+NetDevice.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
        new OrderBy(IpReputationLimiterLimit.COLUMN_LIMITER_name+'.'+IpReputationLimiter.COLUMN_NET_DEVICE_name+'.'+NetDevice.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
        new OrderBy(IpReputationLimiterLimit.COLUMN_LIMITER_name+'.'+IpReputationLimiter.COLUMN_NET_DEVICE_name+'.'+NetDevice.COLUMN_DEVICE_ID_name, ASCENDING),
        new OrderBy(IpReputationLimiterLimit.COLUMN_LIMITER_name+'.'+IpReputationLimiter.COLUMN_IDENTIFIER_name, ASCENDING),
        new OrderBy(IpReputationLimiterLimit.COLUMN_CLASS_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public IpReputationLimiterLimit get(int pkey) throws IOException, SQLException {
        return getUniqueRow(IpReputationLimiterLimit.COLUMN_PKEY, pkey);
    }

    List<IpReputationLimiterLimit> getLimits(IpReputationLimiter limiter) throws IOException, SQLException {
        return getIndexedRows(IpReputationLimiterLimit.COLUMN_LIMITER, limiter.getPkey());
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.IP_REPUTATION_LIMITER_LIMITS;
    }
}
