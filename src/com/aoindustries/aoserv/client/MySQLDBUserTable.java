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
 * @see  MySQLDBUser
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLDBUserTable extends CachedTableIntegerKey<MySQLDBUser> {

    MySQLDBUserTable(AOServConnector connector) {
	super(connector, MySQLDBUser.class);
    }

    int addMySQLDBUser(
	MySQLDatabase md,
	MySQLServerUser msu,
	boolean canSelect,
	boolean canInsert,
	boolean canUpdate,
	boolean canDelete,
	boolean canCreate,
	boolean canDrop,
	boolean canIndex,
	boolean canAlter,
        boolean canCreateTempTable,
        boolean canLockTables,
        boolean canCreateView,
        boolean canShowView,
        boolean canCreateRoutine,
        boolean canAlterRoutine,
        boolean canExecute
    ) {
        try {
            int pkey;
            IntList invalidateList;
            AOServConnection connection=connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.ADD);
                out.writeCompressedInt(SchemaTable.MYSQL_DB_USERS);
                out.writeCompressedInt(md.pkey);
                out.writeCompressedInt(msu.pkey);
                out.writeBoolean(canSelect);
                out.writeBoolean(canInsert);
                out.writeBoolean(canUpdate);
                out.writeBoolean(canDelete);
                out.writeBoolean(canCreate);
                out.writeBoolean(canDrop);
                out.writeBoolean(canIndex);
                out.writeBoolean(canAlter);
                out.writeBoolean(canCreateTempTable);
                out.writeBoolean(canLockTables);
                out.writeBoolean(canCreateView);
                out.writeBoolean(canShowView);
                out.writeBoolean(canCreateRoutine);
                out.writeBoolean(canAlterRoutine);
                out.writeBoolean(canExecute);
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

    public MySQLDBUser get(Object pkey) {
	return getUniqueRow(MySQLDBUser.COLUMN_PKEY, pkey);
    }

    public MySQLDBUser get(int pkey) {
	return getUniqueRow(MySQLDBUser.COLUMN_PKEY, pkey);
    }

    MySQLDBUser getMySQLDBUser(MySQLDatabase db, MySQLServerUser msu) {
	int msuPKey=msu.pkey;

        // Use index first on database
	List<MySQLDBUser> cached=getMySQLDBUsers(db);
	int size=cached.size();
	for(int c=0;c<size;c++) {
            MySQLDBUser mdu=cached.get(c);
            if(mdu.mysql_user==msuPKey) return mdu;
	}
	return null;
    }

    List<MySQLDBUser> getMySQLDBUsers(MySQLServer ms) {
        int msPKey=ms.pkey;

	List<MySQLDBUser> cached=getRows();
	int size=cached.size();
        List<MySQLDBUser> matches=new ArrayList<MySQLDBUser>(size);
	for(int c=0;c<size;c++) {
            MySQLDBUser mdu=cached.get(c);
            MySQLDatabase md=mdu.getMySQLDatabase();
            // The database might be null if filtered or recently removed
            if(md!=null && md.mysql_server==msPKey) matches.add(mdu);
	}
	return matches;
    }

    List<MySQLDBUser> getMySQLDBUsers(MySQLServerUser msu) {
        return getIndexedRows(MySQLDBUser.COLUMN_MYSQL_USER, msu.pkey);
    }

    List<MySQLDBUser> getMySQLDBUsers(MySQLDatabase md) {
        return getIndexedRows(MySQLDBUser.COLUMN_MYSQL_DATABASE, md.pkey);
    }

    List<MySQLServerUser> getMySQLServerUsers(MySQLDatabase md) {
        // Use index first
	List<MySQLDBUser> cached=getMySQLDBUsers(md);
        int len=cached.size();
	List<MySQLServerUser> array=new ArrayList<MySQLServerUser>(len);
        for(int c=0;c<len;c++) array.add(cached.get(c).getMySQLServerUser());
	return array;
    }

    int getTableID() {
	return SchemaTable.MYSQL_DB_USERS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_MYSQL_DB_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_MYSQL_DB_USER, args, 19, err)) {
                int pkey=connector.simpleAOClient.addMySQLDBUser(
                    args[1],
                    args[2],
                    args[3],
                    args[4],
                    AOSH.parseBoolean(args[5], "can_select"),
                    AOSH.parseBoolean(args[6], "can_insert"),
                    AOSH.parseBoolean(args[7], "can_update"),
                    AOSH.parseBoolean(args[8], "can_delete"),
                    AOSH.parseBoolean(args[9], "can_create"),
                    AOSH.parseBoolean(args[10], "can_drop"),
                    AOSH.parseBoolean(args[11], "can_index"),
                    AOSH.parseBoolean(args[12], "can_alter"),
                    AOSH.parseBoolean(args[13], "can_create_temp_table"),
                    AOSH.parseBoolean(args[14], "can_lock_tables"),
                    AOSH.parseBoolean(args[15], "can_create_view"),
                    AOSH.parseBoolean(args[16], "can_show_view"),
                    AOSH.parseBoolean(args[17], "can_create_routine"),
                    AOSH.parseBoolean(args[18], "can_alter_routine"),
                    AOSH.parseBoolean(args[19], "can_execute")
                );
                out.println(pkey);
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_MYSQL_DB_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_MYSQL_DB_USER, args, 4, err)) {
                connector.simpleAOClient.removeMySQLDBUser(
                    args[1],
                    args[2],
                    args[3],
                    args[4]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_MYSQL_DB_USER_REBUILD)) {
            if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_MYSQL_DB_USER_REBUILD, args, 1, err)) {
                connector.simpleAOClient.waitForMySQLDBUserRebuild(args[1]);
            }
            return true;
	}
	return false;
    }

    void waitForRebuild(AOServer aoServer) {
        connector.requestUpdate(
            AOServProtocol.WAIT_FOR_REBUILD,
            SchemaTable.MYSQL_DB_USERS,
            aoServer.pkey
        );
    }
}