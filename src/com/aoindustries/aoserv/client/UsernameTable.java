/*
 * Copyright 2001-2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  Username
 *
 * @author  AO Industries, Inc.
 */
final public class UsernameTable extends CachedTableStringKey<Username> {

    UsernameTable(AOServConnector connector) {
        super(connector, Username.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(Username.COLUMN_USERNAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    void addUsername(Package packageObject, String username) throws IOException, SQLException {
    	connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.USERNAMES,
            packageObject.name,
            username
    	);
    }

    public Username get(String username) throws IOException, SQLException {
        return getUniqueRow(Username.COLUMN_USERNAME, username);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.USERNAMES;
    }

    List<Username> getUsernames(Package pack) throws IOException, SQLException {
        return getIndexedRows(Username.COLUMN_PACKAGE, pack.name);
    }

    @Override
    boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_USERNAME)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_USERNAME, args, 2, err)) {
                connector.getSimpleAOClient().addUsername(
                    args[1],
                        args[2]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ARE_USERNAME_PASSWORDS_SET)) {
            if(AOSH.checkParamCount(AOSHCommand.ARE_USERNAME_PASSWORDS_SET, args, 1, err)) {
                int result=connector.getSimpleAOClient().areUsernamePasswordsSet(args[1]);
                if(result==PasswordProtected.NONE) out.println("none");
                else if(result==PasswordProtected.SOME) out.println("some");
                else if(result==PasswordProtected.ALL) out.println("all");
                else throw new RuntimeException("Unexpected value for result: "+result);
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_USERNAME)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_USERNAME, args, 1, err)) {
                try {
                    SimpleAOClient.checkUsername(args[1]);
                    out.println("true");
                } catch(IllegalArgumentException iae) {
                    out.print("aosh: "+AOSHCommand.CHECK_USERNAME+": ");
                    out.println(iae.getMessage());
                }
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_USERNAME_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_USERNAME_PASSWORD, args, 2, err)) {
                PasswordChecker.Result[] results = connector.getSimpleAOClient().checkUsernamePassword(args[1], args[2]);
                if(PasswordChecker.hasResults(results)) {
                    PasswordChecker.printResults(results, out);
                    out.flush();
                }
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_USERNAME)) {
            if(AOSH.checkParamCount(AOSHCommand.DISABLE_USERNAME, args, 2, err)) {
                out.println(
                    connector.getSimpleAOClient().disableUsername(
                        args[1],
                        args[2]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_USERNAME)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_USERNAME, args, 1, err)) {
                connector.getSimpleAOClient().enableUsername(args[1]);
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.IS_USERNAME_AVAILABLE)) {
            if(AOSH.checkParamCount(AOSHCommand.IS_USERNAME_AVAILABLE, args, 1, err)) {
                try {
                    out.println(connector.getSimpleAOClient().isUsernameAvailable(args[1]));
                    out.flush();
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.IS_USERNAME_AVAILABLE+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_USERNAME)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_USERNAME, args, 1, err)) {
                connector.getSimpleAOClient().removeUsername(args[1]);
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.SET_USERNAME_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_USERNAME_PASSWORD, args, 2, err)) {
                connector.getSimpleAOClient().setUsernamePassword(args[1], args[2]);
            }
            return true;
	}
	return false;
    }

    public boolean isUsernameAvailable(String username) throws SQLException, IOException {
        String check = Username.checkUsername(username);
        if(check!=null) throw new SQLException(check);
        return connector.requestBooleanQuery(true, AOServProtocol.CommandID.IS_USERNAME_AVAILABLE, username);
    }
}
