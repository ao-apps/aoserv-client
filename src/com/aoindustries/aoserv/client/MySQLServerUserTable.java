package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.IntList;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  MySQLServerUser
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLServerUserTable extends CachedTableIntegerKey<MySQLServerUser> {

    MySQLServerUserTable(AOServConnector connector) {
	super(connector, MySQLServerUser.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(MySQLServerUser.COLUMN_USERNAME_name, ASCENDING),
        new OrderBy(MySQLServerUser.COLUMN_MYSQL_SERVER_name+'.'+MySQLServer.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
        new OrderBy(MySQLServerUser.COLUMN_MYSQL_SERVER_name+'.'+MySQLServer.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addMySQLServerUser(String username, MySQLServer mysqlServer, String host) throws IOException, SQLException {
        int pkey;
        IntList invalidateList;
        AOServConnection connection=connector.getConnection();
        try {
            CompressedDataOutputStream out=connection.getOutputStream();
            out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
            out.writeCompressedInt(SchemaTable.TableID.MYSQL_SERVER_USERS.ordinal());
            out.writeUTF(username);
            out.writeCompressedInt(mysqlServer.pkey);
            out.writeBoolean(host!=null); if(host!=null) out.writeUTF(host);
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
    }

    public MySQLServerUser get(Object pkey) {
        try {
            return getUniqueRow(MySQLServerUser.COLUMN_PKEY, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public MySQLServerUser get(int pkey) throws IOException, SQLException {
	return getUniqueRow(MySQLServerUser.COLUMN_PKEY, pkey);
    }

    MySQLServerUser getMySQLServerUser(String username, MySQLServer ms) throws IOException, SQLException {
        int msPKey=ms.pkey;

        List<MySQLServerUser> table=getRows();
	int size=table.size();
	for(int c=0;c<size;c++) {
            MySQLServerUser msu=table.get(c);
            if(msu.mysql_server==msPKey && msu.username.equals(username)) return msu;
	}
	return null;
    }

    List<MySQLServerUser> getMySQLServerUsers(MySQLUser mu) throws IOException, SQLException {
        return getIndexedRows(MySQLServerUser.COLUMN_USERNAME, mu.pkey);
    }

    List<MySQLServerUser> getMySQLServerUsers(MySQLServer ms) throws IOException, SQLException {
        return getIndexedRows(MySQLServerUser.COLUMN_MYSQL_SERVER, ms.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MYSQL_SERVER_USERS;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_MYSQL_SERVER_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_MYSQL_SERVER_USER, args, 4, err)) {
                int pkey=connector.simpleAOClient.addMySQLServerUser(
                    args[1],
                    args[2],
                    args[3],
                    args[4]
                );
                out.println(pkey);
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_MYSQL_SERVER_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.DISABLE_MYSQL_SERVER_USER, args, 4, err)) {
                out.println(
                    connector.simpleAOClient.disableMySQLServerUser(
                        args[1],
                        args[2],
                        args[3],
                        args[4]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_MYSQL_SERVER_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_MYSQL_SERVER_USER, args, 3, err)) {
                connector.simpleAOClient.enableMySQLServerUser(
                    args[1],
                    args[2],
                    args[3]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.IS_MYSQL_SERVER_USER_PASSWORD_SET)) {
            if(AOSH.checkParamCount(AOSHCommand.IS_MYSQL_SERVER_USER_PASSWORD_SET, args, 3, err)) {
                out.println(
                    connector.simpleAOClient.isMySQLServerUserPasswordSet(
                        args[1],
                        args[2],
                        args[3]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_MYSQL_SERVER_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_MYSQL_SERVER_USER, args, 3, err)) {
                connector.simpleAOClient.removeMySQLServerUser(
                    args[1],
                    args[2],
                    args[3]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.SET_MYSQL_SERVER_USER_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_MYSQL_SERVER_USER_PASSWORD, args, 4, err)) {
                connector.simpleAOClient.setMySQLServerUserPassword(
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