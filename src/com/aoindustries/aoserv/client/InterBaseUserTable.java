package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.IntList;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  InterBaseUser
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class InterBaseUserTable extends CachedTableStringKey<InterBaseUser> {

    InterBaseUserTable(AOServConnector connector) {
        super(connector, InterBaseUser.class);
    }

    void addInterBaseUser(
        Username username,
        String firstName,
        String middleName,
        String lastName
    ) {
        try {
            IntList invalidateList;
            AOServConnection connection=connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.ADD);
                out.writeCompressedInt(SchemaTable.INTERBASE_USERS);
                out.writeUTF(username.pkey);
                AOServObject.writeNullUTF(out, firstName);
                AOServObject.writeNullUTF(out, middleName);
                AOServObject.writeNullUTF(out, lastName);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) {
                    invalidateList=AOServConnector.readInvalidateList(in);
                } else {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unexpected response code: "+code);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                connector.releaseConnection(connection);
            }
            connector.tablesUpdated(invalidateList);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    int getTableID() {
        return SchemaTable.INTERBASE_USERS;
    }

    public InterBaseUser get(Object pkey) {
	return getUniqueRow(InterBaseUser.COLUMN_USERNAME, pkey);
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_INTERBASE_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_INTERBASE_USER, args, 4, err)) {
                connector.simpleAOClient.addInterBaseUser(
                    args[1],
                    args[2],
                    args[3],
                    args[4]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ARE_INTERBASE_USER_PASSWORDS_SET)) {
            if(AOSH.checkParamCount(AOSHCommand.ARE_INTERBASE_USER_PASSWORDS_SET, args, 1, err)) {
                int result=connector.simpleAOClient.areInterBaseUserPasswordsSet(args[1]);
                if(result==PasswordProtected.NONE) out.println("none");
                else if(result==PasswordProtected.SOME) out.println("some");
                else if(result==PasswordProtected.ALL) out.println("all");
                else throw new RuntimeException("Unexpected value for result: "+result);
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CHECK_INTERBASE_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_INTERBASE_PASSWORD, args, 2, err)) {
                try {
                    String desc=SimpleAOClient.checkInterBasePassword(args[1], args[2]);
                    if(desc!=null) {
                        out.println(desc);
                        out.flush();
                    }
                } catch(IOException err2) {
                    throw new WrappedException(err2);
                }
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CHECK_INTERBASE_USERNAME)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_INTERBASE_USERNAME, args, 1, err)) {
                SimpleAOClient.checkInterBaseUsername(args[1]);
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_INTERBASE_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.DISABLE_INTERBASE_USER, args, 2, err)) {
                out.println(
                    connector.simpleAOClient.disableInterBaseUser(
                        args[1],
                        args[2]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_INTERBASE_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_INTERBASE_USER, args, 1, err)) {
                connector.simpleAOClient.enableInterBaseUser(args[1]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_INTERBASE_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_INTERBASE_USER, args, 1, err)) {
                connector.simpleAOClient.removeInterBaseUser(
                    args[1]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_INTERBASE_USER_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_INTERBASE_USER_PASSWORD, args, 2, err)) {
                connector.simpleAOClient.setInterBaseUserPassword(
                    args[1],
                    args[2]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_INTERBASE_REBUILD)) {
            if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_INTERBASE_REBUILD, args, 1, err)) {
                connector.simpleAOClient.waitForInterBaseRebuild(args[1]);
            }
            return true;
        }
        return false;
    }

    void waitForRebuild(AOServer aoServer) {
        connector.requestUpdate(
            AOServProtocol.WAIT_FOR_REBUILD,
            SchemaTable.INTERBASE_USERS,
            aoServer.pkey
        );
    }
}