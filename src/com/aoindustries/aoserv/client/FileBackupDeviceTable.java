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
 * @see  FileBackupDevice
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class FileBackupDeviceTable extends GlobalTable<Short,FileBackupDevice> {

    FileBackupDeviceTable(AOServConnector connector) {
	super(connector, FileBackupDevice.class);
    }

    public short addFileBackupDevice(long device, boolean canBackup, String description) {
        return connector.requestShortQueryIL(AOServProtocol.ADD, SchemaTable.FILE_BACKUP_DEVICES, device, canBackup, description);
    }

    public FileBackupDevice get(Object pkey) {
        if(pkey instanceof Short) return get((Short)pkey);
        if(pkey instanceof Long) return get((Long)pkey);
        throw new IllegalArgumentException("Must be an Integer or a Long");
    }

    public FileBackupDevice get(short pkey) {
        return getUniqueRow(FileBackupDevice.COLUMN_PKEY, pkey);
    }

    public FileBackupDevice get(long device) {
        return getUniqueRow(FileBackupDevice.COLUMN_DEVICE, device);
    }

    int getTableID() {
        return SchemaTable.FILE_BACKUP_DEVICES;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
        Profiler.startProfile(Profiler.UNKNOWN, FileBackupDeviceTable.class, "handleCommand(String[],InputStream,TerminalWriter,TerminalWriter,boolean)", null);
        try {
            String command=args[0];
            if(command.equalsIgnoreCase(AOSHCommand.ADD_FILE_BACKUP_DEVICE)) {
                if(AOSH.checkParamCount(AOSHCommand.ADD_FILE_BACKUP_DEVICE, args, 3, err)) {
                    out.println(
                        connector.simpleAOClient.addFileBackupDevice(
                            AOSH.parseLong(args[1], "device"),
                            AOSH.parseBoolean(args[2], "can_backup"),
                            args[3]
                        )
                    );
                    out.flush();
                }
                return true;
            }
            return false;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }
}