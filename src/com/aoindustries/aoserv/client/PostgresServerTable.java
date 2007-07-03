package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  PostgresServer
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresServerTable extends CachedTableIntegerKey<PostgresServer> {

    PostgresServerTable(AOServConnector connector) {
	super(connector, PostgresServer.class);
    }

    int addPostgresServer(
        String name,
        AOServer aoServer,
        PostgresVersion version,
        int maxConnections,
        int sortMem,
        int sharedBuffers,
        boolean fsync
    ) {
	return connector.requestIntQueryIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.POSTGRES_SERVERS,
            name,
            aoServer.pkey,
            version.pkey,
            maxConnections,
            sortMem,
            sharedBuffers,
            fsync
	);
    }

    public PostgresServer get(Object pkey) {
	return getUniqueRow(PostgresServer.COLUMN_PKEY, pkey);
    }

    public PostgresServer get(int pkey) {
	return getUniqueRow(PostgresServer.COLUMN_PKEY, pkey);
    }

    PostgresServer getPostgresServer(NetBind nb) {
	return (PostgresServer)getUniqueRow(PostgresServer.COLUMN_NET_BIND, nb.pkey);
    }

    List<PostgresServer> getPostgresServers(AOServer ao) {
        return getIndexedRows(PostgresServer.COLUMN_AO_SERVER, ao.pkey);
    }

    PostgresServer getPostgresServer(String name, AOServer ao) {
        // Use the index first
        List<PostgresServer> table=getPostgresServers(ao);
	int size=table.size();
	for(int c=0;c<size;c++) {
            PostgresServer ps=table.get(c);
            if(ps.name.equals(name)) return ps;
	}
	return null;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.POSTGRES_SERVERS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.CHECK_POSTGRES_SERVER_NAME)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_POSTGRES_SERVER_NAME, args, 1, err)) {
                try {
                    SimpleAOClient.checkPostgresServerName(args[1]);
                    out.println("true");
                    out.flush();
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.CHECK_POSTGRES_SERVER_NAME+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.IS_POSTGRES_SERVER_NAME_AVAILABLE)) {
            if(AOSH.checkParamCount(AOSHCommand.IS_POSTGRES_SERVER_NAME_AVAILABLE, args, 2, err)) {
                try {
                    out.println(
                        connector.simpleAOClient.isPostgresServerNameAvailable(
                            args[1],
                            args[2]
                        )
                    );
                    out.flush();
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.IS_POSTGRES_SERVER_NAME_AVAILABLE+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.RESTART_POSTGRESQL)) {
            if(AOSH.checkParamCount(AOSHCommand.RESTART_POSTGRESQL, args, 2, err)) {
                connector.simpleAOClient.restartPostgreSQL(
                    args[1],
                    args[2]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.START_POSTGRESQL)) {
            if(AOSH.checkParamCount(AOSHCommand.START_POSTGRESQL, args, 2, err)) {
                connector.simpleAOClient.startPostgreSQL(
                    args[1],
                    args[2]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.STOP_POSTGRESQL)) {
            if(AOSH.checkParamCount(AOSHCommand.STOP_POSTGRESQL, args, 2, err)) {
                connector.simpleAOClient.stopPostgreSQL(
                    args[1],
                    args[2]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_POSTGRES_SERVER_REBUILD)) {
            if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_POSTGRES_SERVER_REBUILD, args, 1, err)) {
                connector.simpleAOClient.waitForPostgresServerRebuild(args[1]);
            }
            return true;
	}
	return false;
    }

    boolean isPostgresServerNameAvailable(String name, AOServer ao) {
	return connector.requestBooleanQuery(AOServProtocol.CommandID.IS_POSTGRES_SERVER_NAME_AVAILABLE, name, ao.pkey);
    }

    void waitForRebuild(AOServer aoServer) {
        connector.requestUpdate(
            AOServProtocol.CommandID.WAIT_FOR_REBUILD,
            SchemaTable.TableID.POSTGRES_SERVERS,
            aoServer.pkey
        );
    }
}