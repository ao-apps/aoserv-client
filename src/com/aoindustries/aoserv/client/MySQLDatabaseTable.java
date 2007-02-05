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
 * @see  MySQLDatabase
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLDatabaseTable extends CachedTableIntegerKey<MySQLDatabase> {

    MySQLDatabaseTable(AOServConnector connector) {
	super(connector, MySQLDatabase.class);
    }

    int addMySQLDatabase(
        String name,
        MySQLServer mysqlServer,
        Package packageObj
    ) {
	int pkey=connector.requestIntQueryIL(
            AOServProtocol.ADD,
            SchemaTable.MYSQL_DATABASES,
            name,
            mysqlServer.pkey,
            packageObj.name
	);
	return pkey;
    }

    public String generateMySQLDatabaseName(String template_base, String template_added) {
	return connector.requestStringQuery(AOServProtocol.GENERATE_MYSQL_DATABASE_NAME, template_base, template_added);
    }

    public MySQLDatabase get(Object pkey) {
	return getUniqueRow(MySQLDatabase.COLUMN_PKEY, pkey);
    }

    public MySQLDatabase get(int pkey) {
	return getUniqueRow(MySQLDatabase.COLUMN_PKEY, pkey);
    }

    MySQLDatabase getMySQLDatabase(String name, MySQLServer ms) {
        // Use index first
	for(MySQLDatabase md : getMySQLDatabases(ms)) if(md.name.equals(name)) return md;
	return null;
    }

    List<MySQLDatabase> getMySQLDatabases(Package pack) {
        return getIndexedRows(MySQLDatabase.COLUMN_PACKAGE, pack.name);
    }

    List<MySQLDatabase> getMySQLDatabases(MySQLServer ms) {
        return getIndexedRows(MySQLDatabase.COLUMN_MYSQL_SERVER, ms.pkey);
    }

    int getTableID() {
	return SchemaTable.MYSQL_DATABASES;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_MYSQL_DATABASE)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_MYSQL_DATABASE, args, 4, err)) {
                int pkey=connector.simpleAOClient.addMySQLDatabase(
                    args[1],
                    args[2],
                    args[3],
                    args[4]
                );
                out.println(pkey);
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.BACKUP_MYSQL_DATABASE)) {
            if(AOSH.checkParamCount(AOSHCommand.BACKUP_MYSQL_DATABASE, args, 3, err)) {
                try {
                    int pkey=connector.simpleAOClient.backupMySQLDatabase(args[1], args[2], args[3]);
                    out.println(pkey);
                    out.flush();
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.BACKUP_MYSQL_DATABASE+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_MYSQL_DATABASE_NAME)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_MYSQL_DATABASE_NAME, args, 1, err)) {
                try {
                    connector.simpleAOClient.checkMySQLDatabaseName(args[1]);
                    out.println("true");
                    out.flush();
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.CHECK_MYSQL_DATABASE_NAME+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.DUMP_MYSQL_DATABASE)) {
            if(AOSH.checkParamCount(AOSHCommand.DUMP_MYSQL_DATABASE, args, 3, err)) {
                try {
                    connector.simpleAOClient.dumpMySQLDatabase(args[1], args[2], args[3], out);
                    out.flush();
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.DUMP_MYSQL_DATABASE+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.GENERATE_MYSQL_DATABASE_NAME)) {
            if(AOSH.checkParamCount(AOSHCommand.GENERATE_MYSQL_DATABASE_NAME, args, 2, err)) {
                out.println(connector.simpleAOClient.generateMySQLDatabaseName(args[1], args[2]));
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.IS_MYSQL_DATABASE_NAME_AVAILABLE)) {
            if(AOSH.checkParamCount(AOSHCommand.IS_MYSQL_DATABASE_NAME_AVAILABLE, args, 3, err)) {
                try {
                    out.println(connector.simpleAOClient.isMySQLDatabaseNameAvailable(args[1], args[2], args[3]));
                    out.flush();
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.IS_MYSQL_DATABASE_NAME_AVAILABLE+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_MYSQL_DATABASE)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_MYSQL_DATABASE, args, 3, err)) {
                connector.simpleAOClient.removeMySQLDatabase(args[1], args[2], args[3]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_MYSQL_DATABASE_BACKUP_RETENTION)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_MYSQL_DATABASE_BACKUP_RETENTION, args, 4, err)) {
                connector.simpleAOClient.setMySQLDatabaseBackupRetention(
                    args[1],
                    args[2],
                    args[3],
                    AOSH.parseShort(args[4], "backup_retention")
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_MYSQL_DATABASE_REBUILD)) {
            if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_MYSQL_DATABASE_REBUILD, args, 1, err)) {
                connector.simpleAOClient.waitForMySQLDatabaseRebuild(args[1]);
            }
            return true;
	}
	return false;
    }

    boolean isMySQLDatabaseNameAvailable(String name, MySQLServer mysqlServer) {
        return connector.requestBooleanQuery(AOServProtocol.IS_MYSQL_DATABASE_NAME_AVAILABLE, name, mysqlServer.pkey);
    }

    public boolean isValidDatabaseName(String name) {
	return isValidDatabaseName(name, connector.mysqlReservedWords.getRows());
    }

    public static boolean isValidDatabaseName(String name, List<?> reservedWords) {
	// Must be a-z first, then a-z or 0-9 or _
	int len = name.length();
	if (len == 0 || len > MySQLDatabase.MAX_DATABASE_NAME_LENGTH) return false;
	// The first character must be [a-z]
	char ch = name.charAt(0);
	if (ch < 'a' || ch > 'z') return false;
	// The rest may have additional characters
	for (int c = 1; c < len; c++) {
            ch = name.charAt(c);
            if ((ch < 'a' || ch > 'z') && (ch < '0' || ch > '9') && ch != '_') return false;
	}

	// Also must not be a reserved word
	int size=reservedWords.size();
	for(int c=0;c<size;c++) {
            if(name.equalsIgnoreCase(reservedWords.get(c).toString())) return false;
	}
	return true;
    }

    void waitForRebuild(AOServer aoServer) {
        connector.requestUpdate(
            AOServProtocol.WAIT_FOR_REBUILD,
            SchemaTable.MYSQL_DATABASES,
            aoServer.pkey
        );
    }
}