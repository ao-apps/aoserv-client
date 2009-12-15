package com.aoindustries.aoserv.client;


/*
 * Copyright 2008-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  VirtualServer
 *
 * @author  AO Industries, Inc.
 */
final public class VirtualServerTable extends CachedTableIntegerKey<VirtualServer> {

    VirtualServerTable(AOServConnector connector) {
        super(connector, VirtualServer.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(VirtualServer.COLUMN_SERVER_name+'.'+Server.COLUMN_ACCOUNTING_name, ASCENDING),
        new OrderBy(VirtualServer.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public VirtualServer get(int server) throws IOException, SQLException {
        return getUniqueRow(VirtualServer.COLUMN_SERVER, server);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.VIRTUAL_SERVERS;
    }
}
