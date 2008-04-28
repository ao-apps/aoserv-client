package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  FileBackupSettingTable
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class FileBackupSettingTable extends CachedTableIntegerKey<FileBackupSetting> {

    FileBackupSettingTable(AOServConnector connector) {
	super(connector, FileBackupSetting.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(FileBackupSetting.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
        new OrderBy(FileBackupSetting.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
        new OrderBy(FileBackupSetting.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_BACKUP_PARTITION_name+'.'+BackupPartition.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
        new OrderBy(FileBackupSetting.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_BACKUP_PARTITION_name+'.'+BackupPartition.COLUMN_PATH_name, ASCENDING),
        new OrderBy(FileBackupSetting.COLUMN_PATH_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addFileBackupSetting(FailoverFileReplication replication, String path, boolean backupEnabled) {
        return connector.requestIntQueryIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.FILE_BACKUP_SETTINGS,
            replication.pkey,
            path,
            backupEnabled
        );
    }

    public FileBackupSetting get(Object pkey) {
	return getUniqueRow(FileBackupSetting.COLUMN_PKEY, pkey);
    }

    public FileBackupSetting get(int pkey) {
	return getUniqueRow(FileBackupSetting.COLUMN_PKEY, pkey);
    }

    FileBackupSetting getFileBackupSetting(FailoverFileReplication ffr, String path) {
        // Use index first
	for(FileBackupSetting fbs : getFileBackupSettings(ffr)) if(fbs.path.equals(path)) return fbs;
	return null;
    }

    List<FileBackupSetting> getFileBackupSettings(FailoverFileReplication ffr) {
        return getIndexedRows(FileBackupSetting.COLUMN_REPLICATION, ffr.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.FILE_BACKUP_SETTINGS;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
        Profiler.startProfile(Profiler.UNKNOWN, FileBackupSettingTable.class, "handleCommand(String[],InputStream,TerminalWriter,TerminalWriter,boolean)", null);
        try {
            String command=args[0];
            if(command.equalsIgnoreCase(AOSHCommand.ADD_FILE_BACKUP_SETTING)) {
                if(AOSH.checkParamCount(AOSHCommand.ADD_FILE_BACKUP_SETTING, args, 3, err)) {
                    out.println(
                        connector.simpleAOClient.addFileBackupSetting(
                            AOSH.parseInt(args[1], "replication"),
                            args[2],
                            AOSH.parseBoolean(args[3], "backup_enabled")
                        )
                    );
                    out.flush();
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_FILE_BACKUP_SETTING)) {
                if(AOSH.checkParamCount(AOSHCommand.REMOVE_FILE_BACKUP_SETTING, args, 2, err)) {
                    connector.simpleAOClient.removeFileBackupSetting(
                        AOSH.parseInt(args[1], "replication"),
                        args[2]
                    );
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.SET_FILE_BACKUP_SETTING)) {
                if(AOSH.checkParamCount(AOSHCommand.SET_FILE_BACKUP_SETTING, args, 3, err)) {
                    connector.simpleAOClient.setFileBackupSetting(
                        AOSH.parseInt(args[1], "replication"),
                        args[2],
                        AOSH.parseBoolean(args[3], "backup_enabled")
                    );
                }
                return true;
            }
            return false;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }
}