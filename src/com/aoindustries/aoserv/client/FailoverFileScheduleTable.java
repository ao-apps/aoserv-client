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
 * @see FailoverFileSchedule
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverFileScheduleTable extends CachedTableIntegerKey<FailoverFileSchedule> {

    FailoverFileScheduleTable(AOServConnector connector) {
	super(connector, FailoverFileSchedule.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(FailoverFileSchedule.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
        new OrderBy(FailoverFileSchedule.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
        new OrderBy(FailoverFileSchedule.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_BACKUP_PARTITION_name+'.'+BackupPartition.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
        new OrderBy(FailoverFileSchedule.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_BACKUP_PARTITION_name+'.'+BackupPartition.COLUMN_PATH_name, ASCENDING),
        new OrderBy(FailoverFileSchedule.COLUMN_HOUR_name, ASCENDING),
        new OrderBy(FailoverFileSchedule.COLUMN_MINUTE_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    List<FailoverFileSchedule> getFailoverFileSchedules(FailoverFileReplication replication) {
        return getIndexedRows(FailoverFileSchedule.COLUMN_REPLICATION, replication.pkey);
    }

    public FailoverFileSchedule get(Object pkey) {
	return getUniqueRow(FailoverFileSchedule.COLUMN_PKEY, pkey);
    }

    public FailoverFileSchedule get(int pkey) {
	return getUniqueRow(FailoverFileSchedule.COLUMN_PKEY, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.FAILOVER_FILE_SCHEDULE;
    }
}