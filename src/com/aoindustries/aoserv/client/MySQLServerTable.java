package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(MySQLServer.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
        new OrderBy(MySQLServer.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addMySQLServer(
        String name,
        AOServer aoServer,
        TechnologyVersion version,
        int maxConnections
    ) throws SQLException, IOException {
        if(!version.name.equals(TechnologyName.MYSQL)) throw new SQLException("TechnologyVersion must have name of "+TechnologyName.MYSQL+": "+version.name);
	return connector.requestIntQueryIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.MYSQL_SERVERS,
            name,
            aoServer.pkey,
            version.pkey,
            maxConnections
	);
    }

    public MySQLServer get(int pkey) throws IOException, SQLException {
        return getUniqueRow(MySQLServer.COLUMN_PKEY, pkey);
    }

    MySQLServer getMySQLServer(NetBind nb) throws IOException, SQLException {
	return getUniqueRow(MySQLServer.COLUMN_NET_BIND, nb.pkey);
    }

    List<MySQLServer> getMySQLServers(AOServer ao) throws IOException, SQLException {
        return getIndexedRows(MySQLServer.COLUMN_AO_SERVER, ao.pkey);
    }

    MySQLServer getMySQLServer(String name, AOServer ao) throws IOException, SQLException {
        // Use the index first
        List<MySQLServer> table=getMySQLServers(ao);
	int size=table.size();
	for(int c=0;c<size;c++) {
            MySQLServer ms=table.get(c);
            if(ms.name.equals(name)) return ms;
	}
	return null;
    }

    List<MySQLServer> getMySQLServers(Package pk) throws IOException, SQLException {
        return getIndexedRows(MySQLServer.COLUMN_PACKAGE, pk.name);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MYSQL_SERVERS;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
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
                        connector.getSimpleAOClient().isMySQLServerNameAvailable(
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
                connector.getSimpleAOClient().restartMySQL(
                    args[1],
                    args[2]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.START_MYSQL)) {
            if(AOSH.checkParamCount(AOSHCommand.START_MYSQL, args, 2, err)) {
                connector.getSimpleAOClient().startMySQL(
                    args[1],
                    args[2]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.STOP_MYSQL)) {
            if(AOSH.checkParamCount(AOSHCommand.STOP_MYSQL, args, 2, err)) {
                connector.getSimpleAOClient().stopMySQL(
                    args[1],
                    args[2]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_MYSQL_SERVER_REBUILD)) {
            if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_MYSQL_SERVER_REBUILD, args, 1, err)) {
                connector.getSimpleAOClient().waitForMySQLServerRebuild(args[1]);
            }
            return true;
	}
	return false;
    }

    boolean isMySQLServerNameAvailable(String name, AOServer ao) throws IOException, SQLException {
	return connector.requestBooleanQuery(AOServProtocol.CommandID.IS_MYSQL_SERVER_NAME_AVAILABLE, name, ao.pkey);
    }

    void waitForRebuild(AOServer aoServer) throws IOException, SQLException {
        connector.requestUpdate(
            AOServProtocol.CommandID.WAIT_FOR_REBUILD,
            SchemaTable.TableID.MYSQL_SERVERS,
            aoServer.pkey
        );
    }
}