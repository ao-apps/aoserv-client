package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  MasterServer
 *
 * @version  1.0a
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

    public MasterServer get(Object pkey) {
	return getUniqueRow(MasterServer.COLUMN_PKEY, pkey);
    }

    public MasterServer get(int pkey) {
	return getUniqueRow(MasterServer.COLUMN_PKEY, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MASTER_SERVERS;
    }
}