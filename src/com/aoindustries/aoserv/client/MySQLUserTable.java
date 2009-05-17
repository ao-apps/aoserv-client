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
 * @see  MySQLUser
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLUserTable extends CachedTableStringKey<MySQLUser> {

    MySQLUserTable(AOServConnector connector) {
        super(connector, MySQLUser.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(MySQLUser.COLUMN_USERNAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    void addMySQLUser(String username) throws IOException, SQLException {
        connector.requestUpdateIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.MYSQL_USERS,
            username
        );
    }

    public MySQLUser get(Object pkey) {
        try {
            return getUniqueRow(MySQLUser.COLUMN_USERNAME, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    List<MySQLUser> getMySQLUsers(Package pack) throws IOException, SQLException {
        String name=pack.name;
        List<MySQLUser> cached=getRows();
        int size=cached.size();
        List<MySQLUser> matches=new ArrayList<MySQLUser>(size);
        for(int c=0;c<size;c++) {
            MySQLUser msu=cached.get(c);
            if(msu.getUsername().packageName.equals(name)) matches.add(msu);
        }
        return matches;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.MYSQL_USERS;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_MYSQL_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_MYSQL_USER, args, 1, err)) {
                connector.getSimpleAOClient().addMySQLUser(
                    args[1]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ARE_MYSQL_USER_PASSWORDS_SET)) {
            if(AOSH.checkParamCount(AOSHCommand.ARE_MYSQL_USER_PASSWORDS_SET, args, 1, err)) {
                int result=connector.getSimpleAOClient().areMySQLUserPasswordsSet(args[1]);
                if(result==PasswordProtected.NONE) out.println("none");
                else if(result==PasswordProtected.SOME) out.println("some");
                else if(result==PasswordProtected.ALL) out.println("all");
                else throw new RuntimeException("Unexpected value for result: "+result);
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CHECK_MYSQL_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_MYSQL_PASSWORD, args, 2, err)) {
                PasswordChecker.Result[] results=SimpleAOClient.checkMySQLPassword(args[1], args[2]);
                if(PasswordChecker.hasResults(Locale.getDefault(), results)) {
                    PasswordChecker.printResults(results, out);
                    out.flush();
                }
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CHECK_MYSQL_USERNAME)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_MYSQL_USERNAME, args, 1, err)) {
                SimpleAOClient.checkMySQLUsername(args[1]);
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_MYSQL_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.DISABLE_MYSQL_USER, args, 2, err)) {
                out.println(
                    connector.getSimpleAOClient().disableMySQLUser(
                        args[1],
                        args[2]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_MYSQL_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_MYSQL_USER, args, 1, err)) {
                connector.getSimpleAOClient().enableMySQLUser(args[1]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_MYSQL_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_MYSQL_USER, args, 1, err)) {
                connector.getSimpleAOClient().removeMySQLUser(
                    args[1]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_MYSQL_USER_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_MYSQL_USER_PASSWORD, args, 2, err)) {
                connector.getSimpleAOClient().setMySQLUserPassword(
                    args[1],
                    args[2]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_MYSQL_USER_REBUILD)) {
            if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_MYSQL_USER_REBUILD, args, 1, err)) {
                connector.getSimpleAOClient().waitForMySQLUserRebuild(args[1]);
            }
            return true;
        }
        return false;
    }

    void waitForRebuild(AOServer aoServer) throws IOException, SQLException {
        connector.requestUpdate(
            AOServProtocol.CommandID.WAIT_FOR_REBUILD,
            SchemaTable.TableID.MYSQL_USERS,
            aoServer.pkey
        );
    }
}