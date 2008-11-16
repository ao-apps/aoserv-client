package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
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

    public int arePasswordsSet() {
        return table.connector.requestBooleanQuery(AOServProtocol.CommandID.IS_POSTGRES_SERVER_USER_PASSWORD_SET, pkey)?PasswordProtected.ALL:PasswordProtected.NONE;
    }

    public boolean canDisable() {
        return disable_log==-1;
    }
    
    public boolean canEnable() {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getPostgresUser().disable_log==-1;
    }

    public PasswordChecker.Result[] checkPassword(Locale userLocale, String password) {
	return PostgresUser.checkPassword(userLocale, username, password);
    }

    /*public String checkPasswordDescribe(String password) {
	return PostgresUser.checkPasswordDescribe(username, password);
    }*/

    public void disable(DisableLog dl) {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.POSTGRES_SERVER_USERS, dl.pkey, pkey);
    }
    
    public void enable() {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.POSTGRES_SERVER_USERS, pkey);
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_USERNAME: return username;
            case COLUMN_POSTGRES_SERVER: return Integer.valueOf(postgres_server);
            case 3: return disable_log==-1?null:Integer.valueOf(disable_log);
            case 4: return predisable_password;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public DisableLog getDisableLog() {
        if(disable_log==-1) return null;
        DisableLog obj=table.connector.disableLogs.get(disable_log);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find DisableLog: "+disable_log));
        return obj;
    }

    public List<PostgresDatabase> getPostgresDatabases() {
        return table.connector.postgresDatabases.getPostgresDatabases(this);
    }

    public PostgresUser getPostgresUser() {
	PostgresUser obj=table.connector.postgresUsers.get(username);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find PostgresUser: "+username));
	return obj;
    }

    public String getPredisablePassword() {
        return predisable_password;
    }

    public PostgresServer getPostgresServer(){
        // May be filtered
	return table.connector.postgresServers.get(postgres_server);
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

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        if(username.equals(PostgresUser.POSTGRES)) reasons.add(new CannotRemoveReason<PostgresServerUser>("Not allowed to remove the "+PostgresUser.POSTGRES+" PostgreSQL user", this));
        
        for(PostgresDatabase pd : getPostgresDatabases()) {
            PostgresServer ps=pd.getPostgresServer();
            reasons.add(new CannotRemoveReason<PostgresDatabase>("Used by PostgreSQL database "+pd.getName()+" on "+ps.getName()+" on "+ps.getAOServer().getHostname(), pd));
        }
        
        return reasons;
    }

    public void remove() {
	table.connector.requestUpdateIL(
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.POSTGRES_SERVER_USERS,
            pkey
	);
    }

    public void setPassword(String password) {
        try {
            AOServConnector connector=table.connector;
            if(!connector.isSecure()) throw new IOException("Passwords for PostgreSQL users may only be set when using secure protocols.  Currently using the "+connector.getProtocol()+" protocol, which is not secure.");

            AOServConnection connection=connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.CommandID.SET_POSTGRES_SERVER_USER_PASSWORD.ordinal());
                out.writeCompressedInt(pkey);
                out.writeBoolean(password!=null); if(password!=null) out.writeUTF(password);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code!=AOServProtocol.DONE) {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unexpected response code: "+code);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                connector.releaseConnection(connection);
            }
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public void setPredisablePassword(String password) {
        try {
            IntList invalidateList;
            AOServConnector connector=table.connector;
            AOServConnection connection=connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.CommandID.SET_POSTGRES_SERVER_USER_PREDISABLE_PASSWORD.ordinal());
                out.writeCompressedInt(pkey);
                out.writeNullUTF(password);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
                else {
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
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    String toStringImpl() {
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

    public boolean canSetPassword() {
        return disable_log==-1 && getPostgresUser().canSetPassword();
    }
}