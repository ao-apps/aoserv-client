package com.aoindustries.aoserv.client;


/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;

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

    public ServerFarm get(String name) throws IOException, SQLException {
        return getUniqueRow(ServerFarm.COLUMN_NAME, name);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SERVER_FARMS;
    }
}