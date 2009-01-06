package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
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