package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * A <code>MySQLDBUser</code> grants a <code>MySQLServerUser</code>
 * access to a <code>MySQLDatabase</code>.  The database and
 * user must be on the same server.
 *
 * @see  MySQLDatabase
 * @see  MySQLServerUser
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLDBUser extends CachedObjectIntegerKey<MySQLDBUser> implements Removable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_MYSQL_DATABASE=1,
        COLUMN_MYSQL_SERVER_USER=2
    ;
    static final String COLUMN_MYSQL_DATABASE_name = "mysql_database";
    static final String COLUMN_MYSQL_SERVER_USER_name = "mysql_server_user";

    int mysql_database;
    int mysql_server_user;

    private boolean
        select_priv,
        insert_priv,
        update_priv,
        delete_priv,
        create_priv,
        drop_priv,
        grant_priv,
        references_priv,
        index_priv,
        alter_priv,
        create_tmp_table_priv,
        lock_tables_priv,
        create_view_priv,
        show_view_priv,
        create_routine_priv,
        alter_routine_priv,
        execute_priv
    ;

    public boolean canAlter() {
	return alter_priv;
    }

    public boolean canCreateTempTable() {
        return create_tmp_table_priv;
    }

    public boolean canLockTables() {
        return lock_tables_priv;
    }

    public boolean canCreate() {
	return create_priv;
    }

    public boolean canDelete() {
	return delete_priv;
    }

    public boolean canDrop() {
	return drop_priv;
    }

    public boolean canGrant() {
	return grant_priv;
    }

    public boolean canIndex() {
	return index_priv;
    }

    public boolean canInsert() {
	return insert_priv;
    }

    public boolean canReference() {
	return references_priv;
    }

    public boolean canSelect() {
	return select_priv;
    }

    public boolean canUpdate() {
	return update_priv;
    }
    
    public boolean canCreateView() {
        return create_view_priv;
    }
    
    public boolean canShowView() {
        return show_view_priv;
    }
    
    public boolean canCreateRoutine() {
        return create_routine_priv;
    }
    
    public boolean canAlterRoutine() {
        return alter_routine_priv;
    }
    
    public boolean canExecute() {
        return execute_priv;
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return pkey;
            case COLUMN_MYSQL_DATABASE: return mysql_database;
            case COLUMN_MYSQL_SERVER_USER: return mysql_server_user;
            case 3: return select_priv;
            case 4: return insert_priv;
            case 5: return update_priv;
            case 6: return delete_priv;
            case 7: return create_priv;
            case 8: return drop_priv;
            case 9: return grant_priv;
            case 10: return references_priv;
            case 11: return index_priv;
            case 12: return alter_priv;
            case 13: return create_tmp_table_priv;
            case 14: return lock_tables_priv;
            case 15: return create_view_priv;
            case 16: return show_view_priv;
            case 17: return create_routine_priv;
            case 18: return alter_routine_priv;
            case 19: return execute_priv;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public MySQLDatabase getMySQLDatabase() {
        // May be null due to filtering or a recently removed table
	return table.connector.mysqlDatabases.get(mysql_database);
    }

    public MySQLServerUser getMySQLServerUser() {
        // May be null due to filtering or a recently removed table
	return table.connector.mysqlServerUsers.get(mysql_server_user);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MYSQL_DB_USERS;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	mysql_database=result.getInt(2);
	mysql_server_user=result.getInt(3);
	select_priv=result.getBoolean(4);
	insert_priv=result.getBoolean(5);
	update_priv=result.getBoolean(6);
	delete_priv=result.getBoolean(7);
	create_priv=result.getBoolean(8);
	drop_priv=result.getBoolean(9);
	grant_priv=result.getBoolean(10);
	references_priv=result.getBoolean(11);
	index_priv=result.getBoolean(12);
	alter_priv=result.getBoolean(13);
        create_tmp_table_priv=result.getBoolean(14);
        lock_tables_priv=result.getBoolean(15);
        create_view_priv=result.getBoolean(16);
        show_view_priv=result.getBoolean(17);
        create_routine_priv=result.getBoolean(18);
        alter_routine_priv=result.getBoolean(19);
        execute_priv=result.getBoolean(20);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	mysql_database=in.readCompressedInt();
	mysql_server_user=in.readCompressedInt();
	select_priv=in.readBoolean();
	insert_priv=in.readBoolean();
	update_priv=in.readBoolean();
	delete_priv=in.readBoolean();
	create_priv=in.readBoolean();
	drop_priv=in.readBoolean();
	grant_priv=in.readBoolean();
	references_priv=in.readBoolean();
	index_priv=in.readBoolean();
	alter_priv=in.readBoolean();
        create_tmp_table_priv=in.readBoolean();
        lock_tables_priv=in.readBoolean();
        create_view_priv=in.readBoolean();
        show_view_priv=in.readBoolean();
        create_routine_priv=in.readBoolean();
        alter_routine_priv=in.readBoolean();
        execute_priv=in.readBoolean();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();
        reasons.addAll(getMySQLServerUser().getCannotRemoveReasons());
        reasons.addAll(getMySQLDatabase().getCannotRemoveReasons());
        return reasons;
    }

    public void remove() {
	table.connector.requestUpdateIL(
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.MYSQL_DB_USERS,
            pkey
	);
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeCompressedInt(mysql_database);
	out.writeCompressedInt(mysql_server_user);
	out.writeBoolean(select_priv);
	out.writeBoolean(insert_priv);
	out.writeBoolean(update_priv);
	out.writeBoolean(delete_priv);
	out.writeBoolean(create_priv);
	out.writeBoolean(drop_priv);
	out.writeBoolean(grant_priv);
	out.writeBoolean(references_priv);
	out.writeBoolean(index_priv);
	out.writeBoolean(alter_priv);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_111)>=0) {
            out.writeBoolean(create_tmp_table_priv);
            out.writeBoolean(lock_tables_priv);
        }
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_4)>=0) {
            out.writeBoolean(create_view_priv);
            out.writeBoolean(show_view_priv);
            out.writeBoolean(create_routine_priv);
            out.writeBoolean(alter_routine_priv);
            out.writeBoolean(execute_priv);
        }
    }
}