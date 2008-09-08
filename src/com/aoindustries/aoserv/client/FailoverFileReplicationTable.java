package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see FailoverFileReplication
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverFileReplicationTable extends CachedTableIntegerKey<FailoverFileReplication> {

    FailoverFileReplicationTable(AOServConnector connector) {
	super(connector, FailoverFileReplication.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(FailoverFileReplication.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
        new OrderBy(FailoverFileReplication.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
        new OrderBy(FailoverFileReplication.COLUMN_BACKUP_PARTITION_name+'.'+BackupPartition.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
        new OrderBy(FailoverFileReplication.COLUMN_BACKUP_PARTITION_name+'.'+BackupPartition.COLUMN_PATH_name, ASCENDING)
    };

    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    List<FailoverFileReplication> getFailoverFileReplications(Server server) {
        return getIndexedRows(FailoverFileReplication.COLUMN_SERVER, server.pkey);
    }

    public FailoverFileReplication get(Object pkey) {
	return getUniqueRow(FailoverFileReplication.COLUMN_PKEY, pkey);
    }

    public FailoverFileReplication get(int pkey) {
	return getUniqueRow(FailoverFileReplication.COLUMN_PKEY, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.FAILOVER_FILE_REPLICATIONS;
    }
}