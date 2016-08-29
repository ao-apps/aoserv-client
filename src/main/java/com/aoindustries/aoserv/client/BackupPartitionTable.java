/*
 * Copyright 2002-2012, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.TerminalWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  BackupPartitionTable
 *
 * @author  AO Industries, Inc.
 */
final public class BackupPartitionTable extends CachedTableIntegerKey<BackupPartition> {

	BackupPartitionTable(AOServConnector connector) {
		super(connector, BackupPartition.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(BackupPartition.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(BackupPartition.COLUMN_PATH_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public BackupPartition get(int pkey) throws IOException, SQLException {
		return getUniqueRow(BackupPartition.COLUMN_PKEY, pkey);
	}

	List<BackupPartition> getBackupPartitions(AOServer ao) throws IOException, SQLException {
		return getIndexedRows(BackupPartition.COLUMN_AO_SERVER, ao.pkey);
	}

	BackupPartition getBackupPartitionForPath(AOServer ao, String path) throws IOException, SQLException {
		// Use index first
		List<BackupPartition> cached=getBackupPartitions(ao);
		int size=cached.size();
		for(int c=0;c<size;c++) {
			BackupPartition bp=cached.get(c);
			if(bp.path.equals(path)) return bp;
		}
		return null;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.BACKUP_PARTITIONS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.GET_BACKUP_PARTITION_TOTAL_SIZE)) {
			if(AOSH.checkParamCount(AOSHCommand.GET_BACKUP_PARTITION_TOTAL_SIZE, args, 2, err)) {
				long size=connector.getSimpleAOClient().getBackupPartitionTotalSize(
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
				long size=connector.getSimpleAOClient().getBackupPartitionUsedSize(
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
