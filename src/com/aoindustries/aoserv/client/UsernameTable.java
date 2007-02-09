package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  Username
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class UsernameTable extends CachedTableStringKey<Username> {

    UsernameTable(AOServConnector connector) {
        super(connector, Username.class);
    }

    void addUsername(Package packageObject, String username) {
	connector.requestUpdateIL(
            AOServProtocol.ADD,
            SchemaTable.USERNAMES,
            packageObject.name,
            username
	);
    }

    public Username get(Object pkey) {
	return getUniqueRow(Username.COLUMN_USERNAME, pkey);
    }

    int getTableID() {
	return SchemaTable.USERNAMES;
    }

    List<Username> getUsernames(Package pack) {
        return getIndexedRows(Username.COLUMN_PACKAGE, pack.name);
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_USERNAME)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_USERNAME, args, 2, err)) {
                connector.simpleAOClient.addUsername(
                    args[1],
                        args[2]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ARE_USERNAME_PASSWORDS_SET)) {
            if(AOSH.checkParamCount(AOSHCommand.ARE_USERNAME_PASSWORDS_SET, args, 1, err)) {
                int result=connector.simpleAOClient.areUsernamePasswordsSet(args[1]);
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
                PasswordChecker.Result[] results = connector.simpleAOClient.checkUsernamePassword(args[1], args[2]);
                if(PasswordChecker.hasResults(results)) {
                    PasswordChecker.printResults(results, out, Locale.getDefault());
                    out.flush();
                }
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_USERNAME)) {
            if(AOSH.checkParamCount(AOSHCommand.DISABLE_USERNAME, args, 2, err)) {
                out.println(
                    connector.simpleAOClient.disableUsername(
                        args[1],
                        args[2]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_USERNAME)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_USERNAME, args, 1, err)) {
                connector.simpleAOClient.enableUsername(args[1]);
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.IS_USERNAME_AVAILABLE)) {
            if(AOSH.checkParamCount(AOSHCommand.IS_USERNAME_AVAILABLE, args, 1, err)) {
                try {
                    out.println(connector.simpleAOClient.isUsernameAvailable(args[1]));
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
                connector.simpleAOClient.removeUsername(args[1]);
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.SET_USERNAME_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_USERNAME_PASSWORD, args, 2, err)) {
                connector.simpleAOClient.setUsernamePassword(args[1], args[2]);
            }
            return true;
	}
	return false;
    }

    public boolean isUsernameAvailable(String username) {
	if(!Username.isValidUsername(username)) throw new WrappedException(new SQLException("Invalid username: "+username));
	return connector.requestBooleanQuery(AOServProtocol.IS_USERNAME_AVAILABLE, username);
    }
}