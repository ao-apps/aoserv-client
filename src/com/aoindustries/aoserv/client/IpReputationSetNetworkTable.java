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
 * @see  IpReputationSetNetwork
 *
 * @author  AO Industries, Inc.
 */
final public class IpReputationSetNetworkTable extends CachedTableLongKey<IpReputationSetNetwork> {

    IpReputationSetNetworkTable(AOServConnector connector) {
	super(connector, IpReputationSetNetwork.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(IpReputationSetNetwork.COLUMN_SET_name+'.'+IpReputationSet.COLUMN_IDENTIFIER_name, ASCENDING),
        new OrderBy(IpReputationSetNetwork.COLUMN_NETWORK_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public IpReputationSetNetwork get(long pkey) throws IOException, SQLException {
        return getUniqueRow(IpReputationSetNetwork.COLUMN_PKEY, pkey);
    }

    List<IpReputationSetNetwork> getNetworks(IpReputationSet set) throws IOException, SQLException {
        return getIndexedRows(IpReputationSetNetwork.COLUMN_SET, set.getPkey());
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.IP_REPUTATION_SET_NETWORKS;
    }
}
