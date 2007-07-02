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
 * @see  MySQLBackup
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLBackupTable extends CachedTableIntegerKey<MySQLBackup> {

    MySQLBackupTable(AOServConnector connector) {
	super(connector, MySQLBackup.class);
    }

    public MySQLBackup get(Object pkey) {
	return getUniqueRow(MySQLBackup.COLUMN_PKEY, pkey);
    }

    public MySQLBackup get(int pkey) {
	return getUniqueRow(MySQLBackup.COLUMN_PKEY, pkey);
    }

    List<MySQLBackup> getMySQLBackups(MySQLServer mysqlServer) {
        return getIndexedRows(MySQLBackup.COLUMN_MYSQL_SERVER, mysqlServer.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MYSQL_BACKUPS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.GET_MYSQL_BACKUP)) {
            if(AOSH.checkParamCount(AOSHCommand.GET_MYSQL_BACKUP, args, 1, err)) {
                connector.simpleAOClient.getMySQLBackup(AOSH.parseInt(args[1], "pkey"), out);
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_MYSQL_BACKUP)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_MYSQL_BACKUP, args, 1, err)) {
                connector.simpleAOClient.removeMySQLBackup(AOSH.parseInt(args[1], "pkey"));
            }
            return true;
	}
	return false;
    }

    void removeExpiredMySQLBackups(AOServer aoServer) {
	connector.requestUpdateIL(AOServProtocol.REMOVE_EXPIRED_MYSQL_BACKUPS, aoServer.pkey);
    }
}