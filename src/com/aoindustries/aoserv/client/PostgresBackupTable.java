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
 * @see  PostgresBackup
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresBackupTable extends CachedTableIntegerKey<PostgresBackup> {

    PostgresBackupTable(AOServConnector connector) {
	super(connector, PostgresBackup.class);
    }

    public PostgresBackup get(Object pkey) {
	return getUniqueRow(PostgresBackup.COLUMN_PKEY, pkey);
    }

    public PostgresBackup get(int pkey) {
	return getUniqueRow(PostgresBackup.COLUMN_PKEY, pkey);
    }

    List<PostgresBackup> getPostgresBackups(PostgresServer postgresServer) {
        return getIndexedRows(PostgresBackup.COLUMN_POSTGRES_SERVER, postgresServer.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.POSTGRES_BACKUPS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.GET_POSTGRES_BACKUP)) {
            if(AOSH.checkParamCount(AOSHCommand.GET_POSTGRES_BACKUP, args, 1, err)) {
                connector.simpleAOClient.getPostgresBackup(AOSH.parseInt(args[1], "pkey"), out);
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_POSTGRES_BACKUP)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_POSTGRES_BACKUP, args, 1, err)) {
                connector.simpleAOClient.removePostgresBackup(AOSH.parseInt(args[1], "pkey"));
            }
            return true;
	}
	return false;
    }

    void removeExpiredPostgresBackups(AOServer aoServer) {
	connector.requestUpdateIL(
            AOServProtocol.CommandID.REMOVE_EXPIRED_POSTGRES_BACKUPS,
            aoServer.pkey
	);
    }
}