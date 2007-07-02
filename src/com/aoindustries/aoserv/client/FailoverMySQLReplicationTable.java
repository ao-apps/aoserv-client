package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see FailoverMySQLReplication
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverMySQLReplicationTable extends CachedTableIntegerKey<FailoverMySQLReplication> {

    FailoverMySQLReplicationTable(AOServConnector connector) {
	super(connector, FailoverMySQLReplication.class);
    }

    public FailoverMySQLReplication get(Object pkey) {
	return getUniqueRow(FailoverMySQLReplication.COLUMN_PKEY, pkey);
    }

    public FailoverMySQLReplication get(int pkey) {
	return getUniqueRow(FailoverMySQLReplication.COLUMN_PKEY, pkey);
    }

    List<FailoverMySQLReplication> getFailoverMySQLReplications(Package pk) {
        List<FailoverMySQLReplication> matches = new ArrayList<FailoverMySQLReplication>();
        List<MySQLServer> mss = pk.getMySQLServers();
        for(MySQLServer ms : mss) {
            matches.addAll(ms.getFailoverMySQLReplications());
        }
        return matches;
    }

    List<FailoverMySQLReplication> getFailoverMySQLReplications(MySQLServer mysqlServer) {
        return getIndexedRows(FailoverMySQLReplication.COLUMN_MYSQL_SERVER, mysqlServer.pkey);
    }

    List<FailoverMySQLReplication> getFailoverMySQLReplications(FailoverFileReplication replication) {
        return getIndexedRows(FailoverMySQLReplication.COLUMN_REPLICATION, replication.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.FAILOVER_MYSQL_REPLICATIONS;
    }
}