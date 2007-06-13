package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  MySQLServer
 *
 * @version  1.4
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLServerTable extends CachedTableIntegerKey<MySQLServer> {

    MySQLServerTable(AOServConnector connector) {
	super(connector, MySQLServer.class);
    }

    int addMySQLServer(
        String name,
        AOServer aoServer,
        TechnologyVersion version,
        int maxConnections
    ) {
        if(!version.name.equals(TechnologyName.MYSQL)) throw new WrappedException(new SQLException("TechnologyVersion must have name of "+TechnologyName.MYSQL+": "+version.name));
	return connector.requestIntQueryIL(
            AOServProtocol.ADD,
            SchemaTable.MYSQL_SERVERS,
            name,
            aoServer.pkey,
            version.pkey,
            maxConnections
	);
    }

    public MySQLServer get(Object pkey) {
	return getUniqueRow(MySQLServer.COLUMN_PKEY, pkey);
    }

    public MySQLServer get(int pkey) {
	return getUniqueRow(MySQLServer.COLUMN_PKEY, pkey);
    }

    MySQLServer getMySQLServer(NetBind nb) {
	return getUniqueRow(MySQLServer.COLUMN_NET_BIND, nb.pkey);
    }

    List<MySQLServer> getMySQLServers(AOServer ao) {
        return getIndexedRows(MySQLServer.COLUMN_AO_SERVER, ao.pkey);
    }

    MySQLServer getMySQLServer(String name, AOServer ao) {
        // Use the index first
        List<MySQLServer> table=getMySQLServers(ao);
	int size=table.size();
	for(int c=0;c<size;c++) {
            MySQLServer ms=table.get(c);
            if(ms.name.equals(name)) return ms;
	}
	return null;
    }

    List<MySQLServer> getMySQLServers(Package pk) {
        return getIndexedRows(MySQLServer.COLUMN_PACKAGE, pk.name);
    }

    int getTableID() {
	return SchemaTable.MYSQL_SERVERS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.CHECK_MYSQL_SERVER_NAME)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_MYSQL_SERVER_NAME, args, 1, err)) {
                try {
                    SimpleAOClient.checkMySQLServerName(args[1]);
                    out.println("true");
                    out.flush();
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.CHECK_MYSQL_SERVER_NAME+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.IS_MYSQL_SERVER_NAME_AVAILABLE)) {
            if(AOSH.checkParamCount(AOSHCommand.IS_MYSQL_SERVER_NAME_AVAILABLE, args, 2, err)) {
                try {
                    out.println(
                        connector.simpleAOClient.isMySQLServerNameAvailable(
                            args[1],
                            args[2]
                        )
                    );
                    out.flush();
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.IS_MYSQL_SERVER_NAME_AVAILABLE+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.RESTART_MYSQL)) {
            if(AOSH.checkParamCount(AOSHCommand.RESTART_MYSQL, args, 2, err)) {
                connector.simpleAOClient.restartMySQL(
                    args[1],
                    args[2]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.START_MYSQL)) {
            if(AOSH.checkParamCount(AOSHCommand.START_MYSQL, args, 2, err)) {
                connector.simpleAOClient.startMySQL(
                    args[1],
                    args[2]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.STOP_MYSQL)) {
            if(AOSH.checkParamCount(AOSHCommand.STOP_MYSQL, args, 2, err)) {
                connector.simpleAOClient.stopMySQL(
                    args[1],
                    args[2]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_MYSQL_SERVER_REBUILD)) {
            if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_MYSQL_SERVER_REBUILD, args, 1, err)) {
                connector.simpleAOClient.waitForMySQLServerRebuild(args[1]);
            }
            return true;
	}
	return false;
    }

    boolean isMySQLServerNameAvailable(String name, AOServer ao) {
	return connector.requestBooleanQuery(AOServProtocol.IS_MYSQL_SERVER_NAME_AVAILABLE, name, ao.pkey);
    }

    void waitForRebuild(AOServer aoServer) {
        connector.requestUpdate(
            AOServProtocol.WAIT_FOR_REBUILD,
            SchemaTable.MYSQL_SERVERS,
            aoServer.pkey
        );
    }
}