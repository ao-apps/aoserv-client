package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;

/**
 * @see  MasterServer
 *
 * @author  AO Industries, Inc.
 */
final public class MasterServerTable extends CachedTableIntegerKey<MasterServer> {

    MasterServerTable(AOServConnector connector) {
	super(connector, MasterServer.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(MasterServer.COLUMN_USERNAME_name, ASCENDING),
        new OrderBy(MasterServer.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
        new OrderBy(MasterServer.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public MasterServer get(int pkey) throws IOException, SQLException {
        return getUniqueRow(MasterServer.COLUMN_PKEY, pkey);
    }

    public SchemaTable.TableID getTableID() {
    	return SchemaTable.TableID.MASTER_SERVERS;
    }
}