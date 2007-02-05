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

    public ServerFarm get(Object name) {
	return getUniqueRow(ServerFarm.COLUMN_NAME, name);
    }

    int getTableID() {
	return SchemaTable.SERVER_FARMS;
    }
}