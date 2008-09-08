package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  PostgresUser
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresUserTable extends CachedTableStringKey<PostgresUser> {

    PostgresUserTable(AOServConnector connector) {
        super(connector, PostgresUser.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(PostgresUser.COLUMN_USERNAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    void addPostgresUser(String username) {
        connector.requestUpdateIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.POSTGRES_USERS,
            username
        );
    }

    public PostgresUser get(Object pkey) {
	return getUniqueRow(PostgresUser.COLUMN_USERNAME, pkey);
    }

    List<PostgresUser> getPostgresUsers(Package pack) {
        String name=pack.name;

        List<PostgresUser> cached=getRows();
        int size=cached.size();
        List<PostgresUser> matches=new ArrayList<PostgresUser>(size);
        for(int c=0;c<size;c++) {
            PostgresUser psu=cached.get(c);
            if(psu.getUsername().packageName.equals(name)) matches.add(psu);
        }
        return matches;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.POSTGRES_USERS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_POSTGRES_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_POSTGRES_USER, args, 1, err)) {
                connector.simpleAOClient.addPostgresUser(
                    args[1]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ARE_POSTGRES_USER_PASSWORDS_SET)) {
            if(AOSH.checkParamCount(AOSHCommand.ARE_POSTGRES_USER_PASSWORDS_SET, args, 1, err)) {
                int result=connector.simpleAOClient.arePostgresUserPasswordsSet(args[1]);
                if(result==PasswordProtected.NONE) out.println("none");
                else if(result==PasswordProtected.SOME) out.println("some");
                else if(result==PasswordProtected.ALL) out.println("all");
                else throw new RuntimeException("Unexpected value for result: "+result);
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CHECK_POSTGRES_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_POSTGRES_PASSWORD, args, 2, err)) {
                try {
                    PasswordChecker.Result[] results = SimpleAOClient.checkPostgresPassword(args[1], args[2]);
                    if(PasswordChecker.hasResults(Locale.getDefault(), results)) {
                        PasswordChecker.printResults(results, out);
                        out.flush();
                    }
                } catch(IOException err2) {
                    throw new WrappedException(err2);
                }
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CHECK_POSTGRES_USERNAME)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_POSTGRES_USERNAME, args, 1, err)) {
                SimpleAOClient.checkPostgresUsername(args[1]);
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_POSTGRES_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.DISABLE_POSTGRES_USER, args, 2, err)) {
                out.println(
                    connector.simpleAOClient.disablePostgresUser(
                        args[1],
                        args[2]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_POSTGRES_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_POSTGRES_USER, args, 1, err)) {
                connector.simpleAOClient.enablePostgresUser(args[1]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_POSTGRES_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_POSTGRES_USER, args, 1, err)) {
                connector.simpleAOClient.removePostgresUser(
                    args[1]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_POSTGRES_USER_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_POSTGRES_USER_PASSWORD, args, 2, err)) {
                connector.simpleAOClient.setPostgresUserPassword(
                    args[1],
                    args[2]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_POSTGRES_USER_REBUILD)) {
            if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_POSTGRES_USER_REBUILD, args, 1, err)) {
                connector.simpleAOClient.waitForPostgresUserRebuild(args[1]);
            }
            return true;
        }
        return false;
    }

    void waitForRebuild(AOServer aoServer) {
        connector.requestUpdate(
            AOServProtocol.CommandID.WAIT_FOR_REBUILD,
            SchemaTable.TableID.POSTGRES_USERS,
            aoServer.pkey
        );
    }
}