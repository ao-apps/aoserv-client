package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
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

    int addFileBackupSetting(Server se, String path, Package packageObj, BackupLevel backupLevel, BackupRetention backupRetention, boolean recurse) {
        return connector.requestIntQueryIL(
            AOServProtocol.ADD,
            SchemaTable.FILE_BACKUP_SETTINGS,
            se.pkey,
            path,
            packageObj.pkey,
            backupLevel.level,
            backupRetention.days,
            recurse
        );
    }

    public FileBackupSetting get(Object pkey) {
	return getUniqueRow(FileBackupSetting.COLUMN_PKEY, pkey);
    }

    public FileBackupSetting get(int pkey) {
	return getUniqueRow(FileBackupSetting.COLUMN_PKEY, pkey);
    }

    FileBackupSetting getFileBackupSetting(Server se, String path) {
        // Use index first
	for(FileBackupSetting fbs : getFileBackupSettings(se)) if(fbs.path.equals(path)) return fbs;
	return null;
    }

    List<FileBackupSetting> getFileBackupSettings(Server se) {
        return getIndexedRows(FileBackupSetting.COLUMN_SERVER, se.pkey);
    }

    List<String> getFileBackupSettingWarnings(Server se) {
        int sePKey=se.pkey;

        List<FileBackupSetting> cached=getRows();
	int size=cached.size();
        List<String> matches=new ArrayList<String>();
        for(int c=0;c<size;c++) {
            FileBackupSetting fbs=cached.get(c);
            if(fbs.server==sePKey && !fbs.recurse) {
                String fbsPath=fbs.getPath();
                // Find those that are being blocked by this non-recursive entry
                for(int d=0;d<size;d++) {
                    if(c!=d) {
                        FileBackupSetting otherFBS=cached.get(d);
                        if(otherFBS.server==sePKey && otherFBS.backup_level>BackupLevel.DO_NOT_BACKUP && otherFBS.getPath().startsWith(fbsPath)) {
                            matches.add("No-recurse configuration for '"+fbsPath+"' will cause '"+otherFBS.getPath()+"' to not be backed-up.");
                        }
                    }
                }
            }
        }
	return matches;
    }

    int getTableID() {
	return SchemaTable.FILE_BACKUP_SETTINGS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
        Profiler.startProfile(Profiler.UNKNOWN, FileBackupSettingTable.class, "handleCommand(String[],InputStream,TerminalWriter,TerminalWriter,boolean)", null);
        try {
            String command=args[0];
            if(command.equalsIgnoreCase(AOSHCommand.ADD_FILE_BACKUP_SETTING)) {
                if(AOSH.checkParamCount(AOSHCommand.ADD_FILE_BACKUP_SETTING, args, 6, err)) {
                    out.println(
                        connector.simpleAOClient.addFileBackupSetting(
                            args[1],
                            args[2],
                            args[3],
                            AOSH.parseShort(args[4], "backup_level"),
                            AOSH.parseShort(args[5], "backup_retention"),
                            AOSH.parseBoolean(args[6], "recurse")
                        )
                    );
                    out.flush();
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_FILE_BACKUP_SETTING)) {
                if(AOSH.checkParamCount(AOSHCommand.REMOVE_FILE_BACKUP_SETTING, args, 2, err)) {
                    connector.simpleAOClient.removeFileBackupSetting(
                        args[1],
                        args[2]
                    );
                }
                return true;
            } else if(command.equalsIgnoreCase(AOSHCommand.SET_FILE_BACKUP_SETTING)) {
                if(AOSH.checkParamCount(AOSHCommand.SET_FILE_BACKUP_SETTING, args, 6, err)) {
                    connector.simpleAOClient.setFileBackupSetting(
                        args[1],
                        args[2],
                        args[3],
                        AOSH.parseShort(args[4], "backup_level"),
                        AOSH.parseShort(args[5], "backup_retention"),
                        AOSH.parseBoolean(args[6], "recurse")
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