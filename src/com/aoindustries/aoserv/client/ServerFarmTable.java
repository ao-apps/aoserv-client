package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * For AO Industries use only.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class ServerFarmTable extends CachedTableStringKey<ServerFarm> {

    ServerFarmTable(AOServConnector connector) {
	super(connector, ServerFarm.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(ServerFarm.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public ServerFarm get(Object name) {
	return getUniqueRow(ServerFarm.COLUMN_NAME, name);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SERVER_FARMS;
    }
}