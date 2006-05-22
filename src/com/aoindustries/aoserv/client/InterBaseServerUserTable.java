package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.IntList;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  InterBaseServerUser
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class InterBaseServerUserTable extends CachedTableIntegerKey<InterBaseServerUser> {

    InterBaseServerUserTable(AOServConnector connector) {
	super(connector, InterBaseServerUser.class);
    }

    int addInterBaseServerUser(String username, AOServer aoServer) {
        try {
            int pkey;
            IntList invalidateList;

            AOServConnection connection=connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.ADD);
                out.writeCompressedInt(SchemaTable.INTERBASE_SERVER_USERS);
                out.writeUTF(username);
                out.writeCompressedInt(aoServer.pkey);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) {
                    pkey=in.readCompressedInt();
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
            return pkey;
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public InterBaseServerUser get(Object pkey) {
	return getUniqueRow(InterBaseServerUser.COLUMN_PKEY, pkey);
    }

    public InterBaseServerUser get(int pkey) {
	return getUniqueRow(InterBaseServerUser.COLUMN_PKEY, pkey);
    }

    List<InterBaseServerUser> getInterBaseServerUsers(String username) {
        return getIndexedRows(InterBaseServerUser.COLUMN_USERNAME, username);
    }

    List<InterBaseServerUser> getInterBaseServerUsers(AOServer ao) {
        return getIndexedRows(InterBaseServerUser.COLUMN_AO_SERVER, ao.pkey);
    }

    InterBaseServerUser getInterBaseServerUser(String username, AOServer ao) {
        int aoPKey=ao.pkey;
        // Use the index first
	List<InterBaseServerUser> table=getInterBaseServerUsers(username);
	int size=table.size();
	for(int c=0;c<size;c++) {
            InterBaseServerUser isu=table.get(c);
            if(isu.ao_server==aoPKey) return isu;
	}
	return null;
    }

    int getTableID() {
	return SchemaTable.INTERBASE_SERVER_USERS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_INTERBASE_SERVER_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_INTERBASE_SERVER_USER, args, 2, err)) {
                int pkey=connector.simpleAOClient.addInterBaseServerUser(
                    args[1],
                    args[2]
                );
                out.println(pkey);
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_INTERBASE_SERVER_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.DISABLE_INTERBASE_SERVER_USER, args, 3, err)) {
                out.println(
                    connector.simpleAOClient.disableInterBaseServerUser(
                        args[1],
                        args[2],
                        args[3]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_INTERBASE_SERVER_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_INTERBASE_SERVER_USER, args, 2, err)) {
                connector.simpleAOClient.enableInterBaseServerUser(args[1], args[2]);
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.IS_INTERBASE_SERVER_USER_PASSWORD_SET)) {
            if(AOSH.checkParamCount(AOSHCommand.IS_INTERBASE_SERVER_USER_PASSWORD_SET, args, 2, err)) {
                out.println(
                    connector.simpleAOClient.isInterBaseServerUserPasswordSet(
                        args[1],
                        args[2]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_INTERBASE_SERVER_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_INTERBASE_SERVER_USER, args, 2, err)) {
                connector.simpleAOClient.removeInterBaseServerUser(
                    args[1],
                    args[2]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.SET_INTERBASE_SERVER_USER_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_INTERBASE_SERVER_USER_PASSWORD, args, 3, err)) {
                connector.simpleAOClient.setInterBaseServerUserPassword(
                    args[1],
                    args[2],
                    args[3]
                );
            }
            return true;
	}
	return false;
    }
}