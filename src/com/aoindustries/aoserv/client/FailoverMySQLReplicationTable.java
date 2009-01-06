package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(FailoverMySQLReplication.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
        new OrderBy(FailoverMySQLReplication.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
        new OrderBy(FailoverMySQLReplication.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_BACKUP_PARTITION_name+'.'+BackupPartition.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
        new OrderBy(FailoverMySQLReplication.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_BACKUP_PARTITION_name+'.'+BackupPartition.COLUMN_PATH_name, ASCENDING),
        new OrderBy(FailoverMySQLReplication.COLUMN_MYSQL_SERVER_name+'.'+MySQLServer.COLUMN_NAME_name, ASCENDING)
    };

    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
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