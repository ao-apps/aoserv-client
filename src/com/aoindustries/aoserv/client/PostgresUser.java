package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * A <code>PostgresUser</code> may have access to multiple servers.  The information
 * common to all servers is contained in <code>PostgresUser</code>.
 *
 * @see  PostgresServerUser
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresUser extends CachedObjectStringKey<PostgresUser> implements Removable, PasswordProtected, Disablable {

    static final int COLUMN_USERNAME=0;
    static final String COLUMN_USERNAME_name = "username";

    /**
     * The maximum length of a PostgreSQL username.
     */
    public static final int MAX_USERNAME_LENGTH=31;

    /**
     * The username of the PostgreSQL special users.
     */
    public static final String
        POSTGRES="postgres",
        AOADMIN="aoadmin",
        AOSERV_APP="aoserv_app",
        AOWEB_APP="aoweb_app"
    ;

    /**
     * A password may be set to null, which means that the account will
     * be disabled.
     */
    public static final String NO_PASSWORD=null;

    public static final String NO_PASSWORD_DB_VALUE="";

    private boolean
        createdb,
        trace,
        superPriv,
        catupd
    ;
    int disable_log;

    public int addPostgresServerUser(PostgresServer postgresServer) throws IOException, SQLException {
        return table.connector.getPostgresServerUsers().addPostgresServerUser(pkey, postgresServer);
    }

    public int arePasswordsSet() throws IOException, SQLException {
        return Username.groupPasswordsSet(getPostgresServerUsers());
    }

    public boolean canCatUPD() {
        return catupd;
    }

    public boolean canCreateDB() {
        return createdb;
    }

    public boolean canDisable() throws IOException, SQLException {
        if(disable_log!=-1) return false;
        for(PostgresServerUser psu : getPostgresServerUsers()) if(psu.disable_log==-1) return false;
        return true;
    }

    public boolean canEnable() throws SQLException, IOException {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getUsername().disable_log==-1;
    }

    public boolean canTrace() {
        return trace;
    }

    public PasswordChecker.Result[] checkPassword(Locale userLocale, String password) throws IOException {
        return checkPassword(userLocale, pkey, password);
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
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.POSTGRES_USERS, dl.pkey, pkey);
    }
    
    public void enable() throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.POSTGRES_USERS, pkey);
    }

    Object getColumnImpl(int i) {
        if(i==COLUMN_USERNAME) return pkey;
        if(i==1) return createdb?Boolean.TRUE:Boolean.FALSE;
        if(i==2) return trace?Boolean.TRUE:Boolean.FALSE;
        if(i==3) return superPriv?Boolean.TRUE:Boolean.FALSE;
        if(i==4) return catupd?Boolean.TRUE:Boolean.FALSE;
        if(i==5) return disable_log==-1?null:Integer.valueOf(disable_log);
        throw new IllegalArgumentException("Invalid index: "+i);
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

    public PostgresServerUser getPostgresServerUser(PostgresServer server) throws IOException, SQLException {
        return server.getPostgresServerUser(pkey);
    }

    public List<PostgresServerUser> getPostgresServerUsers() throws IOException, SQLException {
        return table.connector.getPostgresServerUsers().getPostgresServerUsers(this);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.POSTGRES_USERS;
    }

    public Username getUsername() throws SQLException, IOException {
        Username username=table.connector.getUsernames().get(this.pkey);
        if(username==null) throw new SQLException("Unable to find Username: "+this.pkey);
        return username;
    }

    public void init(ResultSet result) throws SQLException {
        pkey=result.getString(1);
        createdb=result.getBoolean(2);
        trace=result.getBoolean(3);
        superPriv=result.getBoolean(4);
        catupd=result.getBoolean(5);
        disable_log=result.getInt(6);
        if(result.wasNull()) disable_log=-1;
    }

    public boolean isDatabaseAdmin() {
        return superPriv;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readUTF().intern();
        createdb=in.readBoolean();
        trace=in.readBoolean();
        superPriv=in.readBoolean();
        catupd=in.readBoolean();
        disable_log=in.readCompressedInt();
    }

    public List<? extends AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
            getUsername(),
            getDisableLog()
        );
    }

    public List<? extends AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
        );
    }

    public List<CannotRemoveReason> getCannotRemoveReasons(Locale userLocale) throws SQLException, IOException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();
        for(PostgresServerUser psu : getPostgresServerUsers()) reasons.addAll(psu.getCannotRemoveReasons(userLocale));
        return reasons;
    }

    public void remove() throws IOException, SQLException {
        table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.POSTGRES_USERS,
            pkey
        );
    }

    public void setPassword(String password) throws IOException, SQLException {
        for(PostgresServerUser user : getPostgresServerUsers()) if(user.canSetPassword()) user.setPassword(password);
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(pkey);
        out.writeBoolean(createdb);
        out.writeBoolean(trace);
        out.writeBoolean(superPriv);
        out.writeBoolean(catupd);
        out.writeCompressedInt(disable_log);
    }

    /**
     * Determines if a name can be used as a username.  A name is valid if
     * it is between 1 and 31 characters in length and uses only [a-z], [0-9], _, or -
     */
    public static boolean isValidUsername(String name) {
        if(
            name.equals("sameuser")
            || name.equals("samegroup")
            || name.equals("all")
        ) return false;
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
        return disable_log==-1 && !POSTGRES.equals(pkey);
    }
}