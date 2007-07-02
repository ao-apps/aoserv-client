package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * An <code>InterBaseServerUser</code> grants an <code>InterBaseUser</code> access
 * to an <code>AIServer</code>.
 *
 * @see  InterBaseUser
 * @see  InterBaseDatabase
 * @see  AOServer
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class InterBaseServerUser extends CachedObjectIntegerKey<InterBaseServerUser> implements Removable, PasswordProtected, Disablable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_USERNAME=1,
        COLUMN_AO_SERVER=2
    ;

    String username;
    int ao_server;
    int disable_log;
    private String predisable_password;

    public int arePasswordsSet() {
        return table.connector.requestBooleanQuery(AOServProtocol.IS_INTERBASE_SERVER_USER_PASSWORD_SET, pkey)?PasswordProtected.ALL:PasswordProtected.NONE;
    }

    public boolean canDisable() {
        return disable_log==-1 && !username.equals(InterBaseUser.SYSDBA);
    }

    public boolean canEnable() {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getInterBaseUser().disable_log==-1;
    }

    public PasswordChecker.Result[] checkPassword(Locale userLocale, String password) {
        return InterBaseUser.checkPassword(userLocale, username, password);
    }
/*
    public String checkPasswordDescribe(String password) {
	return InterBaseUser.checkPasswordDescribe(username, password);
    }
*/
    public void disable(DisableLog dl) {
        table.connector.requestUpdateIL(AOServProtocol.DISABLE, SchemaTable.TableID.INTERBASE_SERVER_USERS, dl.pkey, pkey);
    }
    
    public void enable() {
        table.connector.requestUpdateIL(AOServProtocol.ENABLE, SchemaTable.TableID.INTERBASE_SERVER_USERS, pkey);
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_USERNAME: return username;
            case COLUMN_AO_SERVER: return Integer.valueOf(ao_server);
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
    
    public String getPredisablePassword() {
        return predisable_password;
    }

    public List<InterBaseDatabase> getInterBaseDatabases() {
        return table.connector.interBaseDatabases.getInterBaseDatabases(this);
    }

    public InterBaseUser getInterBaseUser() {
	InterBaseUser obj=table.connector.interBaseUsers.get(username);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find InterBaseUser: "+username));
	return obj;
    }

    public AOServer getAOServer() {
	AOServer obj=table.connector.aoServers.get(ao_server);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find AOServer: "+ao_server));
	return obj;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.INTERBASE_SERVER_USERS;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	username=result.getString(2);
	ao_server=result.getInt(3);
        disable_log=result.getInt(4);
        if(result.wasNull()) disable_log=-1;
        predisable_password=result.getString(5);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	username=in.readUTF().intern();
	ao_server=in.readCompressedInt();
        disable_log=in.readCompressedInt();
        predisable_password=in.readNullUTF();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        if(username.equals(InterBaseUser.SYSDBA)) reasons.add(new CannotRemoveReason<InterBaseServerUser>("Not allowed to remove the InterBase user named "+InterBaseUser.SYSDBA, this));
        for(InterBaseDatabase id : getInterBaseDatabases()) {
            reasons.add(new CannotRemoveReason<InterBaseDatabase>("Used by InterBase database "+id.getPath()+" on "+id.getInterBaseDBGroup().getLinuxServerGroup().getAOServer().getServer().getHostname(), id));
        }

        return reasons;
    }

    public void remove() {
	table.connector.requestUpdateIL(
            AOServProtocol.REMOVE,
            SchemaTable.TableID.INTERBASE_SERVER_USERS,
            pkey
	);
    }

    public void setPassword(String password) {
        try {
            AOServConnector connector=table.connector;
            if(!connector.isSecure()) throw new IOException("Passwords for InterBase users may only be set when using secure protocols.  Currently using the "+connector.getProtocol()+" protocol, which is not secure.");

            AOServConnection connection=connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.SET_INTERBASE_SERVER_USER_PASSWORD);
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
                out.writeCompressedInt(AOServProtocol.SET_INTERBASE_SERVER_USER_PREDISABLE_PASSWORD);
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
        return username+" on "+getAOServer().getServer().getHostname();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeUTF(username);
	out.writeCompressedInt(ao_server);
        out.writeCompressedInt(disable_log);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_115)>=0) out.writeNullUTF(predisable_password);
    }

    public boolean canSetPassword() {
        return disable_log==-1 && getInterBaseUser().canSetPassword();
    }
}