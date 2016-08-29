/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  IpReputationLimiter
 *
 * @author  AO Industries, Inc.
 */
final public class IpReputationLimiterTable extends CachedTableIntegerKey<IpReputationLimiter> {

    IpReputationLimiterTable(AOServConnector connector) {
	super(connector, IpReputationLimiter.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(IpReputationLimiter.COLUMN_NET_DEVICE_name+'.'+NetDevice.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
        new OrderBy(IpReputationLimiter.COLUMN_NET_DEVICE_name+'.'+NetDevice.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
        new OrderBy(IpReputationLimiter.COLUMN_NET_DEVICE_name+'.'+NetDevice.COLUMN_DEVICE_ID_name, ASCENDING),
        new OrderBy(IpReputationLimiter.COLUMN_IDENTIFIER_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public IpReputationLimiter get(int pkey) throws IOException, SQLException {
        return getUniqueRow(IpReputationLimiter.COLUMN_PKEY, pkey);
    }

    /*
    List<IpReputationLimiter> getIpReputationLimiters(NetDevice nd) throws IOException, SQLException {
        return getIndexedRows(IpReputationLimiter.COLUMN_NET_DEVICE, nd.getPkey());
    }
     */

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.IP_REPUTATION_LIMITERS;
    }
}
