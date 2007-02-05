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
 * @see  PostgresServerUser
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresServerUserTable extends CachedTableIntegerKey<PostgresServerUser> {

    PostgresServerUserTable(AOServConnector connector) {
        super(connector, PostgresServerUser.class);
    }

    int addPostgresServerUser(String username, PostgresServer postgresServer) {
	int pkey=connector.requestIntQueryIL(
            AOServProtocol.ADD,
            SchemaTable.POSTGRES_SERVER_USERS,
            username,
            postgresServer.pkey
	);
	return pkey;
    }

    public PostgresServerUser get(Object pkey) {
	return getUniqueRow(PostgresServerUser.COLUMN_PKEY, pkey);
    }

    public PostgresServerUser get(int pkey) {
	return getUniqueRow(PostgresServerUser.COLUMN_PKEY, pkey);
    }

    PostgresServerUser getPostgresServerUser(String username, PostgresServer postgresServer) {
        return getPostgresServerUser(username, postgresServer.pkey);
    }

    PostgresServerUser getPostgresServerUser(String username, int postgresServer) {
	List<PostgresServerUser> table=getRows();
	int size=table.size();
	for(int c=0;c<size;c++) {
            PostgresServerUser psu=table.get(c);
            if(
                psu.username.equals(username)
                && psu.postgres_server==postgresServer
            ) return psu;
	}
	return null;
    }

    List<PostgresServerUser> getPostgresServerUsers(PostgresUser pu) {
        return getIndexedRows(PostgresServerUser.COLUMN_USERNAME, pu.pkey);
    }

    List<PostgresServerUser> getPostgresServerUsers(PostgresServer postgresServer) {
        return getIndexedRows(PostgresServerUser.COLUMN_POSTGRES_SERVER, postgresServer.pkey);
    }

    int getTableID() {
	return SchemaTable.POSTGRES_SERVER_USERS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_POSTGRES_SERVER_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_POSTGRES_SERVER_USER, args, 3, err)) {
                connector.simpleAOClient.addPostgresServerUser(
                    args[1],
                    args[2],
                    args[3]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_POSTGRES_SERVER_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.DISABLE_POSTGRES_SERVER_USER, args, 4, err)) {
                out.println(
                    connector.simpleAOClient.disablePostgresServerUser(
                        args[1],
                        args[2],
                        args[3],
                        args[4]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_POSTGRES_SERVER_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_POSTGRES_SERVER_USER, args, 3, err)) {
                connector.simpleAOClient.enablePostgresServerUser(args[1], args[2], args[3]);
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.IS_POSTGRES_SERVER_USER_PASSWORD_SET)) {
            if(AOSH.checkParamCount(AOSHCommand.IS_POSTGRES_SERVER_USER_PASSWORD_SET, args, 2, err)) {
                out.println(
                    connector.simpleAOClient.isPostgresServerUserPasswordSet(
                        args[1],
                        args[2],
                        args[3]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_POSTGRES_SERVER_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_POSTGRES_SERVER_USER, args, 3, err)) {
                connector.simpleAOClient.removePostgresServerUser(
                    args[1],
                    args[2],
                    args[3]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.SET_POSTGRES_SERVER_USER_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_POSTGRES_SERVER_USER_PASSWORD, args, 4, err)) {
                connector.simpleAOClient.setPostgresServerUserPassword(
                    args[1],
                    args[2],
                    args[3],
                    args[4]
                );
            }
            return true;
	}
	return false;
    }
}