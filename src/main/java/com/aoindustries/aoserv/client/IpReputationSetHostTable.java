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
 * @see  IpReputationSetHost
 *
 * @author  AO Industries, Inc.
 */
final public class IpReputationSetHostTable extends CachedTableLongKey<IpReputationSetHost> {

    IpReputationSetHostTable(AOServConnector connector) {
	super(connector, IpReputationSetHost.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(IpReputationSetHost.COLUMN_SET_name+'.'+IpReputationSet.COLUMN_IDENTIFIER_name, ASCENDING),
        new OrderBy(IpReputationSetHost.COLUMN_HOST_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public IpReputationSetHost get(long pkey) throws IOException, SQLException {
        return getUniqueRow(IpReputationSetHost.COLUMN_PKEY, pkey);
    }

    List<IpReputationSetHost> getHosts(IpReputationSet set) throws IOException, SQLException {
        return getIndexedRows(IpReputationSetHost.COLUMN_SET, set.getPkey());
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.IP_REPUTATION_SET_HOSTS;
    }
}
