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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(PostgresServerUser.COLUMN_USERNAME_name, ASCENDING),
        new OrderBy(PostgresServerUser.COLUMN_POSTGRES_SERVER_name+'.'+PostgresServer.COLUMN_NAME_name, ASCENDING),
        new OrderBy(PostgresServerUser.COLUMN_POSTGRES_SERVER_name+'.'+PostgresServer.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addPostgresServerUser(String username, PostgresServer postgresServer) throws IOException, SQLException {
	int pkey=connector.requestIntQueryIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.POSTGRES_SERVER_USERS,
            username,
            postgresServer.pkey
	);
	return pkey;
    }

    public PostgresServerUser get(Object pkey) {
        try {
            return getUniqueRow(PostgresServerUser.COLUMN_PKEY, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public PostgresServerUser get(int pkey) throws IOException, SQLException {
	return getUniqueRow(PostgresServerUser.COLUMN_PKEY, pkey);
    }

    PostgresServerUser getPostgresServerUser(String username, PostgresServer postgresServer) throws IOException, SQLException {
        return getPostgresServerUser(username, postgresServer.pkey);
    }

    PostgresServerUser getPostgresServerUser(String username, int postgresServer) throws IOException, SQLException {
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

    List<PostgresServerUser> getPostgresServerUsers(PostgresUser pu) throws IOException, SQLException {
        return getIndexedRows(PostgresServerUser.COLUMN_USERNAME, pu.pkey);
    }

    List<PostgresServerUser> getPostgresServerUsers(PostgresServer postgresServer) throws IOException, SQLException {
        return getIndexedRows(PostgresServerUser.COLUMN_POSTGRES_SERVER, postgresServer.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.POSTGRES_SERVER_USERS;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_POSTGRES_SERVER_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_POSTGRES_SERVER_USER, args, 3, err)) {
                out.println(
                    connector.simpleAOClient.addPostgresServerUser(
                        args[1],
                        args[2],
                        args[3]
                    )
                );
                out.flush();
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