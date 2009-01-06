package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * A <code>MySQLUser</code> stores the details of a MySQL account
 * that are common to all servers.
 *
 * @see  MySQLServerUser
 * @see  MySQLDBUser
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLUser extends CachedObjectStringKey<MySQLUser> implements PasswordProtected, Removable, Disablable {

    static final int COLUMN_USERNAME=0;
    static final String COLUMN_USERNAME_name = "username";

    /**
     * The maximum length of a MySQL username.
     */
    public static final int MAX_USERNAME_LENGTH=16;

    /**
     * The username of the MySQL super user.
     */
    public static final String ROOT="root";

    /**
     * A password may be set to null, which means that the account will
     * be disabled.
     */
    public static final String NO_PASSWORD=null;

    public static final String NO_PASSWORD_DB_VALUE="*";

    private boolean
        select_priv,
        insert_priv,
        update_priv,
        delete_priv,
        create_priv,
        drop_priv,
        reload_priv,
        shutdown_priv,
        process_priv,
        file_priv,
        grant_priv,
        references_priv,
        index_priv,
        alter_priv,
        show_db_priv,
        super_priv,
        create_tmp_table_priv,
        lock_tables_priv,
        execute_priv,
        repl_slave_priv,
        repl_client_priv,
        create_view_priv,
        show_view_priv,
        create_routine_priv,
        alter_routine_priv,
        create_user_priv
    ;

    int disable_log;

    public int addMySQLServerUser(MySQLServer mysqlServer, String host) {
	return table.connector.mysqlServerUsers.addMySQLServerUser(pkey, mysqlServer, host);
    }

    public int arePasswordsSet() {
        return Username.groupPasswordsSet(getMySQLServerUsers());
    }

    public boolean canAlter() {
        return alter_priv;
    }
    
    public boolean canShowDB() {
        return show_db_priv;
    }
    
    public boolean isSuper() {
        return super_priv;
    }
    
    public boolean canCreateTempTable() {
        return create_tmp_table_priv;
    }
    
    public boolean canLockTables() {
        return lock_tables_priv;
    }
    
    public boolean canExecute() {
        return execute_priv;
    }
    
    public boolean isReplicationSlave() {
        return repl_slave_priv;
    }
    
    public boolean isReplicationClient() {
        return repl_client_priv;
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
    
    public boolean canCreateUser() {
        return create_user_priv;
    }

    public boolean canCreate() {
        return create_priv;
    }

    public boolean canDelete() {
        return delete_priv;
    }

    public boolean canDisable() {
        if(disable_log!=-1) return false;
        for(MySQLServerUser msu : getMySQLServerUsers()) if(msu.disable_log==-1) return false;
        return true;
    }
    
    public boolean canDrop() {
        return drop_priv;
    }

    public boolean canEnable() {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getUsername().disable_log==-1;
    }

    public boolean canFile() {
        return file_priv;
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

    public boolean canProcess() {
        return process_priv;
    }

    public boolean canReference() {
        return references_priv;
    }

    public boolean canReload() {
        return reload_priv;
    }

    public boolean canSelect() {
        return select_priv;
    }

    public boolean canShutdown() {
        return shutdown_priv;
    }

    public boolean canUpdate() {
        return update_priv;
    }

    public PasswordChecker.Result[] checkPassword(Locale userLocale, String password) {
        return checkPassword(userLocale, pkey, password);
    }

    public static PasswordChecker.Result[] checkPassword(Locale userLocale, String username, String password) {
        return PasswordChecker.checkPassword(userLocale, username, password, true, false);
    }

    /*public String checkPasswordDescribe(String password) {
        return checkPasswordDescribe(pkey, password);
    }

    public static String checkPasswordDescribe(String username, String password) {
        return PasswordChecker.checkPasswordDescribe(username, password, true, false);
    }*/

    public void disable(DisableLog dl) {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.MYSQL_USERS, dl.pkey, pkey);
    }
    
    public void enable() {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.MYSQL_USERS, pkey);
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_USERNAME: return pkey;
            case 1: return select_priv;
            case 2: return insert_priv;
            case 3: return update_priv;
            case 4: return delete_priv;
            case 5: return create_priv;
            case 6: return drop_priv;
            case 7: return reload_priv;
            case 8: return shutdown_priv;
            case 9: return process_priv;
            case 10: return file_priv;
            case 11: return grant_priv;
            case 12: return references_priv;
            case 13: return index_priv;
            case 14: return alter_priv;
            case 15: return show_db_priv;
            case 16: return super_priv;
            case 17: return create_tmp_table_priv;
            case 18: return lock_tables_priv;
            case 19: return execute_priv;
            case 20: return repl_slave_priv;
            case 21: return repl_client_priv;
            case 22: return create_view_priv;
            case 23: return show_view_priv;
            case 24: return create_routine_priv;
            case 25: return alter_routine_priv;
            case 26: return create_user_priv;
            case 27: return disable_log==-1?null:Integer.valueOf(disable_log);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public DisableLog getDisableLog() {
        if(disable_log==-1) return null;
        DisableLog obj=table.connector.disableLogs.get(disable_log);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find DisableLog: "+disable_log));
        return obj;
    }

    public MySQLServerUser getMySQLServerUser(MySQLServer mysqlServer) {
        return table.connector.mysqlServerUsers.getMySQLServerUser(pkey, mysqlServer);
    }

    public List<MySQLServerUser> getMySQLServerUsers() {
        return table.connector.mysqlServerUsers.getMySQLServerUsers(this);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.MYSQL_USERS;
    }

    public Username getUsername() {
        Username obj=table.connector.usernames.get(pkey);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find Username: "+pkey));
        return obj;
    }

    public void init(ResultSet result) throws SQLException {
        pkey=result.getString(1);
        select_priv=result.getBoolean(2);
        insert_priv=result.getBoolean(3);
        update_priv=result.getBoolean(4);
        delete_priv=result.getBoolean(5);
        create_priv=result.getBoolean(6);
        drop_priv=result.getBoolean(7);
        reload_priv=result.getBoolean(8);
        shutdown_priv=result.getBoolean(9);
        process_priv=result.getBoolean(10);
        file_priv=result.getBoolean(11);
        grant_priv=result.getBoolean(12);
        references_priv=result.getBoolean(13);
        index_priv=result.getBoolean(14);
        alter_priv=result.getBoolean(15);
        show_db_priv=result.getBoolean(16);
        super_priv=result.getBoolean(17);
        create_tmp_table_priv=result.getBoolean(18);
        lock_tables_priv=result.getBoolean(19);
        execute_priv=result.getBoolean(20);
        repl_slave_priv=result.getBoolean(21);
        repl_client_priv=result.getBoolean(22);
        create_view_priv=result.getBoolean(23);
        show_view_priv=result.getBoolean(24);
        create_routine_priv=result.getBoolean(25);
        alter_routine_priv=result.getBoolean(26);
        create_user_priv=result.getBoolean(27);
        disable_log=result.getInt(28);
        if(result.wasNull()) disable_log=-1;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readUTF().intern();
        select_priv=in.readBoolean();
        insert_priv=in.readBoolean();
        update_priv=in.readBoolean();
        delete_priv=in.readBoolean();
        create_priv=in.readBoolean();
        drop_priv=in.readBoolean();
        reload_priv=in.readBoolean();
        shutdown_priv=in.readBoolean();
        process_priv=in.readBoolean();
        file_priv=in.readBoolean();
        grant_priv=in.readBoolean();
        references_priv=in.readBoolean();
        index_priv=in.readBoolean();
        alter_priv=in.readBoolean();
        show_db_priv=in.readBoolean();
        super_priv=in.readBoolean();
        create_tmp_table_priv=in.readBoolean();
        lock_tables_priv=in.readBoolean();
        execute_priv=in.readBoolean();
        repl_slave_priv=in.readBoolean();
        repl_client_priv=in.readBoolean();
        create_view_priv=in.readBoolean();
        show_view_priv=in.readBoolean();
        create_routine_priv=in.readBoolean();
        alter_routine_priv=in.readBoolean();
        create_user_priv=in.readBoolean();
        disable_log=in.readCompressedInt();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();
        if(pkey.equals(ROOT)) reasons.add(new CannotRemoveReason<MySQLUser>("Not allowed to remove the "+ROOT+" MySQL user", this));
        return reasons;
    }

    public void remove() {
        table.connector.requestUpdateIL(
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.MYSQL_USERS,
            pkey
        );
    }

    public void setPassword(String password) {
        for(MySQLServerUser user : getMySQLServerUsers()) user.setPassword(password);
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(pkey);
        out.writeBoolean(select_priv);
        out.writeBoolean(insert_priv);
        out.writeBoolean(update_priv);
        out.writeBoolean(delete_priv);
        out.writeBoolean(create_priv);
        out.writeBoolean(drop_priv);
        out.writeBoolean(reload_priv);
        out.writeBoolean(shutdown_priv);
        out.writeBoolean(process_priv);
        out.writeBoolean(file_priv);
        out.writeBoolean(grant_priv);
        out.writeBoolean(references_priv);
        out.writeBoolean(index_priv);
        out.writeBoolean(alter_priv);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_111)>=0) {
            out.writeBoolean(show_db_priv);
            out.writeBoolean(super_priv);
            out.writeBoolean(create_tmp_table_priv);
            out.writeBoolean(lock_tables_priv);
            out.writeBoolean(execute_priv);
            out.writeBoolean(repl_slave_priv);
            out.writeBoolean(repl_client_priv);
        }
        if(version.compareTo(AOServProtocol.Version.VERSION_1_4)>=0) {
            out.writeBoolean(create_view_priv);
            out.writeBoolean(show_view_priv);
            out.writeBoolean(create_routine_priv);
            out.writeBoolean(alter_routine_priv);
            out.writeBoolean(create_user_priv);
        }        
        out.writeCompressedInt(disable_log);
    }

    /**
     * Determines if a name can be used as a username.  A name is valid if
     * it is between 1 and 16 characters in length and uses only [a-z], [0-9], or _
     */
    public static boolean isValidUsername(String name) {
        int len = name.length();
        if (len == 0 || len > MAX_USERNAME_LENGTH) return false;
        // The first character must be [a-z]
        char ch = name.charAt(0);
        if (ch < 'a' || ch > 'z') return false;
        // The rest may have additional characters
        for (int c = 1; c < len; c++) {
            ch = name.charAt(c);
            if(
                (ch<'a' || ch>'z')
                && (ch<'0' || ch>'9')
                && ch!='_'
            ) return false;
        }
        return true;
    }
    
    public boolean canSetPassword() {
        return disable_log==-1 && !pkey.equals(ROOT);
    }
}