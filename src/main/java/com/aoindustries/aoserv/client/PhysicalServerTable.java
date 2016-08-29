package com.aoindustries.aoserv.client;


/*
 * Copyright 2008-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  PhysicalServer
 *
 * @author  AO Industries, Inc.
 */
final public class PhysicalServerTable extends CachedTableIntegerKey<PhysicalServer> {

    PhysicalServerTable(AOServConnector connector) {
	super(connector, PhysicalServer.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(PhysicalServer.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
        new OrderBy(PhysicalServer.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public PhysicalServer get(int server) throws IOException, SQLException {
        return getUniqueRow(PhysicalServer.COLUMN_SERVER, server);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.PHYSICAL_SERVERS;
    }
}
