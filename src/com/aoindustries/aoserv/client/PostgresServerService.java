/*
 * Copyright 2002-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  PostgresServer
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.postgres_servers)
public interface PostgresServerService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,Integer,PostgresServer> {

    /* TODO
    int addPostgresServer(
        String name,
        AOServer aoServer,
        PostgresVersion version,
        int maxConnections,
        int sortMem,
        int sharedBuffers,
        boolean fsync
    ) throws IOException, SQLException {
    	return connector.requestIntQueryIL(
            true,
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

    PostgresServer getPostgresServer(NetBind nb) throws IOException, SQLException {
	return (PostgresServer)getUniqueRow(PostgresServer.COLUMN_NET_BIND, nb.pkey);
    }

    List<PostgresServer> getPostgresServers(AOServer ao) throws IOException, SQLException {
        return getIndexedRows(PostgresServer.COLUMN_AO_SERVER, ao.pkey);
    }

    PostgresServer getPostgresServer(String name, AOServer ao) throws IOException, SQLException {
        // Use the index first
        List<PostgresServer> table=getPostgresServers(ao);
	int size=table.size();
	for(int c=0;c<size;c++) {
            PostgresServer ps=table.get(c);
            if(ps.name.equals(name)) return ps;
	}
	return null;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
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
                        connector.getSimpleAOClient().isPostgresServerNameAvailable(
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
                connector.getSimpleAOClient().restartPostgreSQL(
                    args[1],
                    args[2]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.START_POSTGRESQL)) {
            if(AOSH.checkParamCount(AOSHCommand.START_POSTGRESQL, args, 2, err)) {
                connector.getSimpleAOClient().startPostgreSQL(
                    args[1],
                    args[2]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.STOP_POSTGRESQL)) {
            if(AOSH.checkParamCount(AOSHCommand.STOP_POSTGRESQL, args, 2, err)) {
                connector.getSimpleAOClient().stopPostgreSQL(
                    args[1],
                    args[2]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_POSTGRES_SERVER_REBUILD)) {
            if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_POSTGRES_SERVER_REBUILD, args, 1, err)) {
                connector.getSimpleAOClient().waitForPostgresServerRebuild(args[1]);
            }
            return true;
	}
	return false;
    }

    boolean isPostgresServerNameAvailable(String name, AOServer ao) throws IOException, SQLException {
    	return connector.requestBooleanQuery(true, AOServProtocol.CommandID.IS_POSTGRES_SERVER_NAME_AVAILABLE, name, ao.pkey);
    }

    void waitForRebuild(AOServer aoServer) throws IOException, SQLException {
        connector.requestUpdate(
            true,
            AOServProtocol.CommandID.WAIT_FOR_REBUILD,
            SchemaTable.TableID.POSTGRES_SERVERS,
            aoServer.pkey
        );
    }
    */
}