package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  MySQLDBUser
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLDBUserTable extends CachedTableIntegerKey<MySQLDBUser> {

    MySQLDBUserTable(AOServConnector connector) {
    	super(connector, MySQLDBUser.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(MySQLDBUser.COLUMN_MYSQL_DATABASE_name+'.'+MySQLDatabase.COLUMN_NAME_name, ASCENDING),
        new OrderBy(MySQLDBUser.COLUMN_MYSQL_DATABASE_name+'.'+MySQLDatabase.COLUMN_MYSQL_SERVER_name+'.'+MySQLServer.COLUMN_AO_SERVER_RESOURCE_name+'.'+AOServerResource.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
        new OrderBy(MySQLDBUser.COLUMN_MYSQL_DATABASE_name+'.'+MySQLDatabase.COLUMN_MYSQL_SERVER_name+'.'+MySQLServer.COLUMN_NAME_name, ASCENDING),
        new OrderBy(MySQLDBUser.COLUMN_MYSQL_USER_name+'.'+MySQLUser.COLUMN_USERNAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addMySQLDBUser(
        final MySQLDatabase md,
        final MySQLUser mu,
        final boolean canSelect,
        final boolean canInsert,
        final boolean canUpdate,
        final boolean canDelete,
        final boolean canCreate,
        final boolean canDrop,
        final boolean canIndex,
        final boolean canAlter,
        final boolean canCreateTempTable,
        final boolean canLockTables,
        final boolean canCreateView,
        final boolean canShowView,
        final boolean canCreateRoutine,
        final boolean canAlterRoutine,
        final boolean canExecute,
        final boolean canEvent,
        final boolean canTrigger
    ) throws IOException, SQLException {
        return connector.requestResult(
            true,
            new AOServConnector.ResultRequest<Integer>() {
                int pkey;
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
                    out.writeCompressedInt(SchemaTable.TableID.MYSQL_DB_USERS.ordinal());
                    out.writeCompressedInt(md.pkey);
                    out.writeCompressedInt(mu.pkey);
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
                    out.writeBoolean(canEvent);
                    out.writeBoolean(canTrigger);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        pkey=in.readCompressedInt();
                        invalidateList=AOServConnector.readInvalidateList(in);
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public Integer afterRelease() {
                    connector.tablesUpdated(invalidateList);
                    return pkey;
                }
            }
        );
    }

    public MySQLDBUser get(int pkey) throws IOException, SQLException {
    	return getUniqueRow(MySQLDBUser.COLUMN_PKEY, pkey);
    }

    MySQLDBUser getMySQLDBUser(MySQLDatabase db, MySQLUser mu) throws IOException, SQLException {
    	int muPKey=mu.pkey;

        // Use index first on database
        List<MySQLDBUser> cached=getMySQLDBUsers(db);
        int size=cached.size();
        for(int c=0;c<size;c++) {
            MySQLDBUser mdu=cached.get(c);
            if(mdu.mysql_user==muPKey) return mdu;
        }
        return null;
    }

    List<MySQLDBUser> getMySQLDBUsers(MySQLServer ms) throws IOException, SQLException {
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

    List<MySQLDBUser> getMySQLDBUsers(MySQLUser mu) throws IOException, SQLException {
        return getIndexedRows(MySQLDBUser.COLUMN_MYSQL_USER, mu.pkey);
    }

    List<MySQLDBUser> getMySQLDBUsers(MySQLDatabase md) throws IOException, SQLException {
        return getIndexedRows(MySQLDBUser.COLUMN_MYSQL_DATABASE, md.pkey);
    }

    //List<MySQLUser> getMySQLUsers(MySQLDatabase md) throws IOException, SQLException {
        // Use index first
    	//List<MySQLDBUser> cached=getMySQLDBUsers(md);
        //int len=cached.size();
    	//List<MySQLUser> array=new ArrayList<MySQLUser>(len);
        //for(int c=0;c<len;c++) array.add(cached.get(c).getMySQLUser());
    	//return array;
    //}

    public SchemaTable.TableID getTableID() {
    	return SchemaTable.TableID.MYSQL_DB_USERS;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_MYSQL_DB_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_MYSQL_DB_USER, args, 21, err)) {
                int pkey=connector.getSimpleAOClient().addMySQLDBUser(
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
                    AOSH.parseBoolean(args[19], "can_execute"),
                    AOSH.parseBoolean(args[20], "can_event"),
                    AOSH.parseBoolean(args[21], "can_trigger")
                );
                out.println(pkey);
                out.flush();
            }
            return true;
    	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_MYSQL_DB_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_MYSQL_DB_USER, args, 4, err)) {
                connector.getSimpleAOClient().removeMySQLDBUser(
                    args[1],
                    args[2],
                    args[3],
                    args[4]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_MYSQL_DB_USER_REBUILD)) {
            if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_MYSQL_DB_USER_REBUILD, args, 1, err)) {
                connector.getSimpleAOClient().waitForMySQLDBUserRebuild(args[1]);
            }
            return true;
        }
        return false;
    }

    void waitForRebuild(AOServer aoServer) throws IOException, SQLException {
        connector.requestUpdate(
            true,
            AOServProtocol.CommandID.WAIT_FOR_REBUILD,
            SchemaTable.TableID.MYSQL_DB_USERS,
            aoServer.pkey
        );
    }
}