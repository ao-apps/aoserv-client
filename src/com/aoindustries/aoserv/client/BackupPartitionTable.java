package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  BackupPartitionTable
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class BackupPartitionTable extends CachedTableIntegerKey<BackupPartition> {

    BackupPartitionTable(AOServConnector connector) {
	super(connector, BackupPartition.class);
    }

    public BackupPartition get(Object pkey) {
        return get(((Integer)pkey).intValue());
    }

    public BackupPartition get(int pkey) {
	return getUniqueRow(BackupPartition.COLUMN_PKEY, pkey);
    }

    List<BackupPartition> getBackupPartitions(AOServer ao) {
        return getIndexedRows(BackupPartition.COLUMN_AO_SERVER, ao.pkey);
    }

    BackupPartition getBackupPartitionForDevice(AOServer ao, String device) {
        // Use index first
        List<BackupPartition> cached=getBackupPartitions(ao);
	int size=cached.size();
        for(int c=0;c<size;c++) {
            BackupPartition bp=cached.get(c);
            if(bp.device.equals(device)) return bp;
        }
	return null;
    }

    BackupPartition getBackupPartitionForPath(AOServer ao, String path) {
        // Use index first
        List<BackupPartition> cached=getBackupPartitions(ao);
	int size=cached.size();
        for(int c=0;c<size;c++) {
            BackupPartition bp=cached.get(c);
            if(bp.path.equals(path)) return bp;
        }
	return null;
    }

    int getTableID() {
	return SchemaTable.BACKUP_PARTITIONS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.GET_BACKUP_PARTITION_TOTAL_SIZE)) {
            if(AOSH.checkParamCount(AOSHCommand.GET_BACKUP_PARTITION_TOTAL_SIZE, args, 2, err)) {
                long size=connector.simpleAOClient.getBackupPartitionTotalSize(
                    args[1],
                    args[2]
                );
                if(size==-1) out.println("Server unavailable");
                else out.println(size);
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.GET_BACKUP_PARTITION_USED_SIZE)) {
            if(AOSH.checkParamCount(AOSHCommand.GET_BACKUP_PARTITION_USED_SIZE, args, 2, err)) {
                long size=connector.simpleAOClient.getBackupPartitionUsedSize(
                    args[1],
                    args[2]
                );
                if(size==-1) out.println("Server unavailable");
                else out.println(size);
                out.flush();
            }
            return true;
	}
	return false;
    }
}