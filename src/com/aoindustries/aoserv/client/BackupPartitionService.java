/*
 * Copyright 2002-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.backup_partitions)
public interface BackupPartitionService extends AOServService<Integer,BackupPartition> {

    /* TODO
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
    }*/

    /* TODO
    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
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
     */
}
