package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.IntList;
import com.aoindustries.util.StringUtility;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * A <code>MySQLServerUser</code> grants a <code>MySQLUser</code> access
 * to an <code>AOServer</code>.  Once access is granted to the <code>Server</code>,
 * access may then be granted to individual <code>MySQLDatabase</code>s via
 * <code>MySQLDBUser</code>s.
 *
 * @see  MySQLUser
 * @see  MySQLDatabase
 * @see  MySQLDBUser
 * @see  MySQLServer
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLServerUser extends CachedObjectIntegerKey<MySQLServerUser> implements Removable, PasswordProtected, Disablable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_USERNAME=1,
        COLUMN_MYSQL_SERVER=2
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

    String username;
    int mysql_server;
    String host;
    int disable_log;
    private String predisable_password;
    int max_questions;
    int max_updates;
    int max_connections;
    int max_user_connections;

    public int arePasswordsSet() throws IOException, SQLException {
        return table.connector.requestBooleanQuery(true, AOServProtocol.CommandID.IS_MYSQL_SERVER_USER_PASSWORD_SET, pkey)?PasswordProtected.ALL:PasswordProtected.NONE;
    }

    public boolean canDisable() {
        return disable_log==-1;
    }
    
    public boolean canEnable() throws SQLException, IOException {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getMySQLUser().disable_log==-1;
    }

    public PasswordChecker.Result[] checkPassword(Locale userLocale, String password) throws IOException {
	return MySQLUser.checkPassword(userLocale, username, password);
    }
/*
    public String checkPasswordDescribe(String password) {
	return MySQLUser.checkPasswordDescribe(username, password);
    }
*/
    public void disable(DisableLog dl) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.MYSQL_SERVER_USERS, dl.pkey, pkey);
    }
    
    public void enable() throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.MYSQL_SERVER_USERS, pkey);
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_USERNAME: return username;
            case COLUMN_MYSQL_SERVER: return Integer.valueOf(mysql_server);
            case 3: return host;
            case 4: return disable_log==-1?null:Integer.valueOf(disable_log);
            case 5: return predisable_password;
            case 6: return max_questions;
            case 7: return max_updates;
            case 8: return max_connections;
            case 9: return max_user_connections;
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

    public String getHost() {
	return host;
    }

    public List<MySQLDBUser> getMySQLDBUsers() throws IOException, SQLException {
        return table.connector.getMysqlDBUsers().getMySQLDBUsers(this);
    }

    public MySQLUser getMySQLUser() throws SQLException, IOException {
	MySQLUser obj=table.connector.getMysqlUsers().get(username);
	if(obj==null) throw new SQLException("Unable to find MySQLUser: "+username);
	return obj;
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

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MYSQL_SERVER_USERS;
    }

    public void init(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	username=result.getString(2);
	mysql_server=result.getInt(3);
	host=result.getString(4);
        disable_log=result.getInt(5);
        if(result.wasNull()) disable_log=-1;
        predisable_password=result.getString(6);
        max_questions=result.getInt(7);
        max_updates=result.getInt(8);
        max_connections=result.getInt(9);
        max_user_connections=result.getInt(10);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        username=in.readUTF().intern();
        mysql_server=in.readCompressedInt();
        host=StringUtility.intern(in.readNullUTF());
        disable_log=in.readCompressedInt();
        predisable_password=in.readNullUTF();
        max_questions=in.readCompressedInt();
        max_updates=in.readCompressedInt();
        max_connections=in.readCompressedInt();
        max_user_connections=in.readCompressedInt();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();
        if(username.equals(MySQLUser.ROOT)) reasons.add(new CannotRemoveReason<MySQLServerUser>("Not allowed to remove the "+MySQLUser.ROOT+" MySQL user", this));
        return reasons;
    }

    public void remove() throws IOException, SQLException {
    	table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.MYSQL_SERVER_USERS,
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
                    out.writeCompressedInt(AOServProtocol.CommandID.SET_MYSQL_SERVER_USER_PASSWORD.ordinal());
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
                    out.writeCompressedInt(AOServProtocol.CommandID.SET_MYSQL_SERVER_USER_PREDISABLE_PASSWORD.ordinal());
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

    @Override
    String toStringImpl() throws IOException, SQLException {
        return username+" on "+getMySQLServer().toString();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeUTF(username);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_4)<0) out.writeCompressedInt(-1);
        else out.writeCompressedInt(mysql_server);
        out.writeNullUTF(host);
        out.writeCompressedInt(disable_log);
        out.writeNullUTF(predisable_password);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_4)>=0) {
            out.writeCompressedInt(max_questions);
            out.writeCompressedInt(max_updates);
        }
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_111)>=0) out.writeCompressedInt(max_connections);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_4)>=0) out.writeCompressedInt(max_user_connections);
    }

    public boolean canSetPassword() throws SQLException, IOException {
        return disable_log==-1 && getMySQLUser().canSetPassword();
    }
}