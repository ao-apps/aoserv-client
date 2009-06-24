package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.IntList;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * A <code>PostgresServerUser</code> grants a <code>PostgresUser</code>
 * access to a <code>Server</code>.
 *
 * @see  PostgresUser
 * @see  PostgresServer
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresServerUser extends CachedObjectIntegerKey<PostgresServerUser> implements Removable, PasswordProtected, Disablable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_USERNAME=1,
        COLUMN_POSTGRES_SERVER=2
    ;
    static final String COLUMN_USERNAME_name = "username";
    static final String COLUMN_POSTGRES_SERVER_name = "postgres_server";

    String username;
    int postgres_server;
    int disable_log;
    private String predisable_password;

    public int arePasswordsSet() throws IOException, SQLException {
        return table.connector.requestBooleanQuery(true, AOServProtocol.CommandID.IS_POSTGRES_SERVER_USER_PASSWORD_SET, pkey)?PasswordProtected.ALL:PasswordProtected.NONE;
    }

    public boolean canDisable() {
        return disable_log==-1;
    }
    
    public boolean canEnable() throws SQLException, IOException {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getPostgresUser().disable_log==-1;
    }

    public PasswordChecker.Result[] checkPassword(Locale userLocale, String password) throws IOException {
	return PostgresUser.checkPassword(userLocale, username, password);
    }

    /*public String checkPasswordDescribe(String password) {
	return PostgresUser.checkPasswordDescribe(username, password);
    }*/

    public void disable(DisableLog dl) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.POSTGRES_SERVER_USERS, dl.pkey, pkey);
    }
    
    public void enable() throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.POSTGRES_SERVER_USERS, pkey);
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_USERNAME: return username;
            case COLUMN_POSTGRES_SERVER: return Integer.valueOf(postgres_server);
            case 3: return disable_log==-1?null:Integer.valueOf(disable_log);
            case 4: return predisable_password;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public boolean isDisabled() {
        return disable_log!=-1;
    }

    public DisableLog getDisableLog() throws IOException, SQLException {
        if(disable_log==-1) return null;
        DisableLog obj=table.connector.getDisableLogs().get(disable_log);
        if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
        return obj;
    }

    public List<PostgresDatabase> getPostgresDatabases() throws IOException, SQLException {
        return table.connector.getPostgresDatabases().getPostgresDatabases(this);
    }

    public PostgresUser getPostgresUser() throws SQLException, IOException {
	PostgresUser obj=table.connector.getPostgresUsers().get(username);
	if(obj==null) throw new SQLException("Unable to find PostgresUser: "+username);
	return obj;
    }

    public String getPredisablePassword() {
        return predisable_password;
    }

    public PostgresServer getPostgresServer() throws IOException, SQLException{
        // May be filtered
	return table.connector.getPostgresServers().get(postgres_server);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.POSTGRES_SERVER_USERS;
    }

    public void init(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	username=result.getString(2);
	postgres_server=result.getInt(3);
        disable_log=result.getInt(4);
        if(result.wasNull()) disable_log=-1;
        predisable_password=result.getString(5);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	username=in.readUTF().intern();
	postgres_server=in.readCompressedInt();
        disable_log=in.readCompressedInt();
        predisable_password=in.readNullUTF();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() throws SQLException, IOException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        if(username.equals(PostgresUser.POSTGRES)) reasons.add(new CannotRemoveReason<PostgresServerUser>("Not allowed to remove the "+PostgresUser.POSTGRES+" PostgreSQL user", this));
        
        for(PostgresDatabase pd : getPostgresDatabases()) {
            PostgresServer ps=pd.getPostgresServer();
            reasons.add(new CannotRemoveReason<PostgresDatabase>("Used by PostgreSQL database "+pd.getName()+" on "+ps.getName()+" on "+ps.getAOServer().getHostname(), pd));
        }
        
        return reasons;
    }

    public void remove() throws IOException, SQLException {
    	table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.POSTGRES_SERVER_USERS,
            pkey
    	);
    }

    public void setPassword(final String password) throws IOException, SQLException {
        AOServConnector connector=table.connector;
        if(!connector.isSecure()) throw new IOException("Passwords for PostgreSQL users may only be set when using secure protocols.  Currently using the "+connector.getProtocol()+" protocol, which is not secure.");

        connector.requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.SET_POSTGRES_SERVER_USER_PASSWORD.ordinal());
                    out.writeCompressedInt(pkey);
                    out.writeBoolean(password!=null); if(password!=null) out.writeUTF(password);
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
                    out.writeCompressedInt(AOServProtocol.CommandID.SET_POSTGRES_SERVER_USER_PREDISABLE_PASSWORD.ordinal());
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
        return username+" on "+getPostgresServer().toString();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeUTF(username);
	out.writeCompressedInt(postgres_server);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_130)<=0) {
            out.writeCompressedInt(-1);
        }
        out.writeCompressedInt(disable_log);
        out.writeNullUTF(predisable_password);
    }

    public boolean canSetPassword() throws SQLException, IOException {
        return disable_log==-1 && getPostgresUser().canSetPassword();
    }
}