package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  InterBaseBackup
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class InterBaseBackupTable extends CachedTableIntegerKey<InterBaseBackup> {

    InterBaseBackupTable(AOServConnector connector) {
	super(connector, InterBaseBackup.class);
    }

    public InterBaseBackup get(Object pkey) {
	return getUniqueRow(InterBaseBackup.COLUMN_PKEY, pkey);
    }

    public InterBaseBackup get(int pkey) {
	return getUniqueRow(InterBaseBackup.COLUMN_PKEY, pkey);
    }

    List<InterBaseBackup> getInterBaseBackups(AOServer ao) {
        return getIndexedRows(InterBaseBackup.COLUMN_AO_SERVER, ao.pkey);
    }

    int getTableID() {
	return SchemaTable.INTERBASE_BACKUPS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.GET_INTERBASE_BACKUP)) {
            if(AOSH.checkParamCount(AOSHCommand.GET_INTERBASE_BACKUP, args, 1, err)) {
                connector.simpleAOClient.getInterBaseBackup(AOSH.parseInt(args[1], "pkey"), out);
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_INTERBASE_BACKUP)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_INTERBASE_BACKUP, args, 1, err)) {
                connector.simpleAOClient.removeInterBaseBackup(AOSH.parseInt(args[1], "pkey"));
            }
            return true;
	}
	return false;
    }

    void removeExpiredInterBaseBackups(AOServer aoServer) {
	connector.requestUpdateIL(AOServProtocol.REMOVE_EXPIRED_INTERBASE_BACKUPS, aoServer.pkey);
    }
}