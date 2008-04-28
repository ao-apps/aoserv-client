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
 * @see  AOServerDaemonHost
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class AOServerDaemonHostTable extends CachedTableIntegerKey<AOServerDaemonHost> {

    AOServerDaemonHostTable(AOServConnector connector) {
	super(connector, AOServerDaemonHost.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(AOServerDaemonHost.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
        new OrderBy(AOServerDaemonHost.COLUMN_HOST_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public AOServerDaemonHost get(Object pkey) {
	return getUniqueRow(AOServerDaemonHost.COLUMN_PKEY, pkey);
    }

    public AOServerDaemonHost get(int pkey) {
	return getUniqueRow(AOServerDaemonHost.COLUMN_PKEY, pkey);
    }

    List<AOServerDaemonHost> getAOServerDaemonHosts(AOServer aoServer) {
        return getIndexedRows(AOServerDaemonHost.COLUMN_AO_SERVER, aoServer.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.AO_SERVER_DAEMON_HOSTS;
    }
}