package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.WrappedException;
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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(MySQLDatabase.COLUMN_NAME_name, ASCENDING),
        new OrderBy(MySQLDatabase.COLUMN_MYSQL_SERVER_name+'.'+MySQLServer.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
        new OrderBy(MySQLDatabase.COLUMN_MYSQL_SERVER_name+'.'+MySQLServer.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addMySQLDatabase(
        String name,
        MySQLServer mysqlServer,
        Package packageObj
    ) throws IOException, SQLException {
	int pkey=connector.requestIntQueryIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.MYSQL_DATABASES,
            name,
            mysqlServer.pkey,
            packageObj.name
	);
	return pkey;
    }

    public String generateMySQLDatabaseName(String template_base, String template_added) throws IOException, SQLException {
	return connector.requestStringQuery(AOServProtocol.CommandID.GENERATE_MYSQL_DATABASE_NAME, template_base, template_added);
    }

    public MySQLDatabase get(Object pkey) {
        try {
            return getUniqueRow(MySQLDatabase.COLUMN_PKEY, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public MySQLDatabase get(int pkey) throws IOException, SQLException {
	return getUniqueRow(MySQLDatabase.COLUMN_PKEY, pkey);
    }

    MySQLDatabase getMySQLDatabase(String name, MySQLServer ms) throws IOException, SQLException {
        // Use index first
	for(MySQLDatabase md : getMySQLDatabases(ms)) if(md.name.equals(name)) return md;
	return null;
    }

    List<MySQLDatabase> getMySQLDatabases(Package pack) throws IOException, SQLException {
        return getIndexedRows(MySQLDatabase.COLUMN_PACKAGE, pack.name);
    }

    List<MySQLDatabase> getMySQLDatabases(MySQLServer ms) throws IOException, SQLException {
        return getIndexedRows(MySQLDatabase.COLUMN_MYSQL_SERVER, ms.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MYSQL_DATABASES;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
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
        } else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_MYSQL_DATABASE_REBUILD)) {
            if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_MYSQL_DATABASE_REBUILD, args, 1, err)) {
                connector.simpleAOClient.waitForMySQLDatabaseRebuild(args[1]);
            }
            return true;
	}
	return false;
    }

    boolean isMySQLDatabaseNameAvailable(String name, MySQLServer mysqlServer) throws IOException, SQLException {
        return connector.requestBooleanQuery(AOServProtocol.CommandID.IS_MYSQL_DATABASE_NAME_AVAILABLE, name, mysqlServer.pkey);
    }

    public boolean isValidDatabaseName(String name) throws IOException, SQLException {
	return isValidDatabaseName(name, connector.getMysqlReservedWords().getRows());
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

    void waitForRebuild(AOServer aoServer) throws IOException, SQLException {
        connector.requestUpdate(
            AOServProtocol.CommandID.WAIT_FOR_REBUILD,
            SchemaTable.TableID.MYSQL_DATABASES,
            aoServer.pkey
        );
    }
}