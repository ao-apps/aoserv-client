package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.IntList;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A <code>MySQLUser</code> stores the details of a MySQL account
 * that are common to all servers.
 *
 * @see  MySQLDBUser
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLUser extends CachedObjectIntegerKey<MySQLUser> implements PasswordProtected, Removable, Disablable {

    static final int
        COLUMN_PKEY = 0,
        COLUMN_USERNAME = 1,
        COLUMN_MYSQL_SERVER = 2,
        COLUMN_DISABLE_LOG = 32
    ;
    static final String COLUMN_USERNAME_name = "username";
    static final String COLUMN_MYSQL_SERVER_name = "mysql_server";

    public static final int
        UNLIMITED_QUESTIONS=0,
        DEFAULT_MAX_QUESTIONS=UNLIMITED_QUESTIONS
    ;

    public static final int
        UNLIMITED_UPDATES=0,
        DEFAULT_MAX_UPDATES=UNLIMITED_UPDATES
    ;

    public static final int
        UNLIMITED_CONNECTIONS=0,
        DEFAULT_MAX_CONNECTIONS=UNLIMITED_CONNECTIONS
    ;

    public static final int
        UNLIMITED_USER_CONNECTIONS=0,
        DEFAULT_MAX_USER_CONNECTIONS=UNLIMITED_USER_CONNECTIONS
    ;

    public static final int MAX_HOST_LENGTH=60;

    /**
     * Convenience constants for the most commonly used host values.
     */
    public static final String
        ANY_HOST="%",
        ANY_LOCAL_HOST=null
    ;

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

    String username;
    int mysql_server;
    String host;
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
        create_user_priv,
        event_priv,
        trigger_priv
    ;

    int disable_log;
    private String predisable_password;
    int max_questions;
    int max_updates;
    int max_connections;
    int max_user_connections;

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

    public boolean canEvent() {
        return event_priv;
    }

    public boolean canTrigger() {
        return trigger_priv;
    }

    public boolean canCreate() {
        return create_priv;
    }

    public boolean canDelete() {
        return delete_priv;
    }

    public boolean canDisable() throws IOException, SQLException {
        if(disable_log!=-1) return false;
        return true;
    }

    public boolean canDrop() {
        return drop_priv;
    }

    public boolean canEnable() throws SQLException, IOException {
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

    public PasswordChecker.Result[] checkPassword(Locale userLocale, String password) throws IOException {
        return checkPassword(userLocale, username, password);
    }

    public static PasswordChecker.Result[] checkPassword(Locale userLocale, String username, String password) throws IOException {
        return PasswordChecker.checkPassword(userLocale, username, password, true, false);
    }

    /*public String checkPasswordDescribe(String password) {
        return checkPasswordDescribe(pkey, password);
    }

    public static String checkPasswordDescribe(String username, String password) {
        return PasswordChecker.checkPasswordDescribe(username, password, true, false);
    }*/

    public void disable(DisableLog dl) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.MYSQL_USERS, dl.pkey, pkey);
    }
    
    public void enable() throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.MYSQL_USERS, pkey);
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_USERNAME: return username;
            case COLUMN_MYSQL_SERVER: return Integer.valueOf(mysql_server);
            case 3: return host;
            case 4: return select_priv;
            case 5: return insert_priv;
            case 6: return update_priv;
            case 7: return delete_priv;
            case 8: return create_priv;
            case 9: return drop_priv;
            case 10: return reload_priv;
            case 11: return shutdown_priv;
            case 12: return process_priv;
            case 13: return file_priv;
            case 14: return grant_priv;
            case 15: return references_priv;
            case 16: return index_priv;
            case 17: return alter_priv;
            case 18: return show_db_priv;
            case 19: return super_priv;
            case 20: return create_tmp_table_priv;
            case 21: return lock_tables_priv;
            case 22: return execute_priv;
            case 23: return repl_slave_priv;
            case 24: return repl_client_priv;
            case 25: return create_view_priv;
            case 26: return show_view_priv;
            case 27: return create_routine_priv;
            case 28: return alter_routine_priv;
            case 29: return create_user_priv;
            case 30: return event_priv;
            case 31: return trigger_priv;
            case COLUMN_DISABLE_LOG: return disable_log==-1?null:Integer.valueOf(disable_log);
            case 33: return predisable_password;
            case 34: return max_questions;
            case 35: return max_updates;
            case 36: return max_connections;
            case 37: return max_user_connections;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public boolean isDisabled() {
        return disable_log!=-1;
    }

    public DisableLog getDisableLog() throws SQLException, IOException {
        if(disable_log==-1) return null;
        DisableLog obj=table.connector.getDisableLogs().get(disable_log);
        if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
        return obj;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.MYSQL_USERS;
    }

    public Username getUsername() throws SQLException, IOException {
        Username obj=table.connector.getUsernames().get(username);
        if(obj==null) throw new SQLException("Unable to find Username: "+username);
        return obj;
    }

    public void init(ResultSet result) throws SQLException {
        int pos = 1;
        pkey=result.getInt(pos++);
        username=result.getString(pos++);
        mysql_server=result.getInt(pos++);
        host=result.getString(pos++);
        select_priv=result.getBoolean(pos++);
        insert_priv=result.getBoolean(pos++);
        update_priv=result.getBoolean(pos++);
        delete_priv=result.getBoolean(pos++);
        create_priv=result.getBoolean(pos++);
        drop_priv=result.getBoolean(pos++);
        reload_priv=result.getBoolean(pos++);
        shutdown_priv=result.getBoolean(pos++);
        process_priv=result.getBoolean(pos++);
        file_priv=result.getBoolean(pos++);
        grant_priv=result.getBoolean(pos++);
        references_priv=result.getBoolean(pos++);
        index_priv=result.getBoolean(pos++);
        alter_priv=result.getBoolean(pos++);
        show_db_priv=result.getBoolean(pos++);
        super_priv=result.getBoolean(pos++);
        create_tmp_table_priv=result.getBoolean(pos++);
        lock_tables_priv=result.getBoolean(pos++);
        execute_priv=result.getBoolean(pos++);
        repl_slave_priv=result.getBoolean(pos++);
        repl_client_priv=result.getBoolean(pos++);
        create_view_priv=result.getBoolean(pos++);
        show_view_priv=result.getBoolean(pos++);
        create_routine_priv=result.getBoolean(pos++);
        alter_routine_priv=result.getBoolean(pos++);
        create_user_priv=result.getBoolean(pos++);
        event_priv=result.getBoolean(pos++);
        trigger_priv=result.getBoolean(pos++);
        disable_log=result.getInt(pos++);
        if(result.wasNull()) disable_log=-1;
        predisable_password=result.getString(pos++);
        max_questions=result.getInt(pos++);
        max_updates=result.getInt(pos++);
        max_connections=result.getInt(pos++);
        max_user_connections=result.getInt(pos++);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        username=in.readUTF().intern();
        mysql_server=in.readCompressedInt();
        host=StringUtility.intern(in.readNullUTF());
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
        event_priv=in.readBoolean();
        trigger_priv=in.readBoolean();
        disable_log=in.readCompressedInt();
        predisable_password=in.readNullUTF();
        max_questions=in.readCompressedInt();
        max_updates=in.readCompressedInt();
        max_connections=in.readCompressedInt();
        max_user_connections=in.readCompressedInt();
    }

    public List<? extends AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
            getUsername(),
            getMySQLServer(),
            getDisableLog()
        );
    }

    public List<? extends AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
            getMySQLDBUsers()
        );
    }

    public List<CannotRemoveReason> getCannotRemoveReasons(Locale userLocale) {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();
        if(username.equals(ROOT)) reasons.add(new CannotRemoveReason<MySQLUser>("Not allowed to remove the "+ROOT+" MySQL user", this));
        return reasons;
    }

    public void remove() throws IOException, SQLException {
        table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.MYSQL_USERS,
            pkey
        );
    }

    public void setPassword(final String password) throws IOException, SQLException {
        AOServConnector connector=table.connector;
        if(!connector.isSecure()) throw new IOException("Passwords for MySQL users may only be set when using secure protocols.  Currently using the "+connector.getProtocol()+" protocol, which is not secure.");

        connector.requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.SET_MYSQL_USER_PASSWORD.ordinal());
                    out.writeCompressedInt(pkey);
                    out.writeNullUTF(password);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code!=AOServProtocol.DONE) {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public void afterRelease() {
                }
            }
        );
    }

    public void setPredisablePassword(final String password) throws IOException, SQLException {
        table.connector.requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.SET_MYSQL_USER_PREDISABLE_PASSWORD.ordinal());
                    out.writeCompressedInt(pkey);
                    out.writeNullUTF(password);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
                    else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public void afterRelease() {
                    table.connector.tablesUpdated(invalidateList);
                }
            }
        );
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        if(version.compareTo(AOServProtocol.Version.VERSION_1_62)>=0) out.writeCompressedInt(pkey);
        out.writeUTF(username);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_62)>=0) {
            out.writeCompressedInt(mysql_server);
            out.writeNullUTF(host);
        }
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
        if(version.compareTo(AOServProtocol.Version.VERSION_1_54)>=0) {
            out.writeBoolean(event_priv);
            out.writeBoolean(trigger_priv);
        }
        out.writeCompressedInt(disable_log);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_62)>=0) {
            out.writeNullUTF(predisable_password);
            out.writeCompressedInt(max_questions);
            out.writeCompressedInt(max_updates);
            out.writeCompressedInt(max_connections);
            out.writeCompressedInt(max_user_connections);
        }
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
        return disable_log==-1 && !username.equals(ROOT);
    }

    public int arePasswordsSet() throws IOException, SQLException {
        return table.connector.requestBooleanQuery(true, AOServProtocol.CommandID.IS_MYSQL_USER_PASSWORD_SET, pkey)?PasswordProtected.ALL:PasswordProtected.NONE;
    }

    public String getHost() {
    	return host;
    }

    public List<MySQLDBUser> getMySQLDBUsers() throws IOException, SQLException {
        return table.connector.getMysqlDBUsers().getMySQLDBUsers(this);
    }

    public String getPredisablePassword() {
        return predisable_password;
    }

    public int getMaxQuestions() {
        return max_questions;
    }

    public int getMaxUpdates() {
        return max_updates;
    }

    public int getMaxConnections() {
        return max_connections;
    }

    public int getMaxUserConnections() {
        return max_user_connections;
    }

    public MySQLServer getMySQLServer() throws IOException, SQLException{
        // May be filtered
    	return table.connector.getMysqlServers().get(mysql_server);
    }

    @Override
    String toStringImpl(Locale userLocale) throws IOException, SQLException {
        return username+" on "+getMySQLServer().toStringImpl(userLocale);
    }
}