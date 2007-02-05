package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * A <code>PostgresDatabase</code> corresponds to a unique PostgreSQL table
 * space on one server.  The database name must be unique per server
 * and, to aid in account portability, will typically be unique
 * across the entire system.
 *
 * @see  PostgresEncoding
 * @see  PostgresServerUser
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresDatabase extends CachedObjectIntegerKey<PostgresDatabase> implements Dumpable, Removable, JdbcProvider {

    static final int
        COLUMN_PKEY=0,
        COLUMN_POSTGRES_SERVER=2,
        COLUMN_DATDBA=3
    ;

    /**
     * The default number of days to keep backups.
     */
    public static final short DEFAULT_BACKUP_LEVEL=BackupLevel.DEFAULT_BACKUP_LEVEL;
    public static final short DEFAULT_BACKUP_RETENTION=BackupRetention.DEFAULT_BACKUP_RETENTION;

    /**
     * The classname of the JDBC driver used for the <code>PostgresDatabase</code>.
     */
    public static final String JDBC_DRIVER="org.postgresql.Driver";

    /**
     * Special databases.
     */
    public static final String
        AOINDUSTRIES="aoindustries",
        AOSERV="aoserv",
        AOSERV_BACKUP="aoserv_backup",
        AOWEB="aoweb",
        TEMPLATE0="template0",
        TEMPLATE1="template1"
    ;

    /**
     * The name of a database is limited by the internal data type of
     * the <code>pg_database</code> table.  The type is <code>name</code>
     * which has a maximum length of 31 characters.
     */
    public static final int MAX_DATABASE_NAME_LENGTH=31;

    String name;
    int postgres_server;
    int datdba;
    private int encoding;
    private boolean is_template;
    private boolean allow_conn;
    private short backup_level;
    private short backup_retention;

    public boolean allowsConnections() {
	return allow_conn;
    }

    public int backup() {
	return table.connector.requestIntQueryIL(
            AOServProtocol.BACKUP_POSTGRES_DATABASE,
            pkey
	);
    }

    public void dump(PrintWriter out) {
	dump((Writer)out);
    }

    public void dump(Writer out) {
        try {
            // Create the new profile
            AOServConnection connection=table.connector.getConnection();
            try {
                CompressedDataOutputStream masterOut=connection.getOutputStream();
                masterOut.writeCompressedInt(AOServProtocol.DUMP_POSTGRES_DATABASE);
                masterOut.writeCompressedInt(pkey);
                masterOut.flush();

                CompressedDataInputStream masterIn=connection.getInputStream();
                int code;
                byte[] buff=BufferManager.getBytes();
                try {
                    char[] chars=BufferManager.getChars();
                    try {
                        while((code=masterIn.readByte())==AOServProtocol.NEXT) {
                            int len=masterIn.readShort();
                            masterIn.readFully(buff, 0, len);
                            for(int c=0;c<len;c++) chars[c]=(char)buff[c];
                            out.write(chars, 0, len);
                        }
                    } finally {
                        BufferManager.release(chars);
                    }
                } finally {
                    BufferManager.release(buff);
                }
                if(code!=AOServProtocol.DONE) AOServProtocol.checkResult(code, masterIn);
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                table.connector.releaseConnection(connection);
            }
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public BackupLevel getBackupLevel() {
        BackupLevel bl=table.connector.backupLevels.get(backup_level);
        if(bl==null) throw new WrappedException(new SQLException("Unable to find BackupLevel: "+backup_level));
        return bl;
    }

    public BackupRetention getBackupRetention() {
        BackupRetention br=table.connector.backupRetentions.get(backup_retention);
        if(br==null) throw new WrappedException(new SQLException("Unable to find BackupRetention: "+backup_retention));
        return br;
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return name;
            case COLUMN_POSTGRES_SERVER: return Integer.valueOf(postgres_server);
            case COLUMN_DATDBA: return Integer.valueOf(datdba);
            case 4: return Integer.valueOf(encoding);
            case 5: return is_template?Boolean.TRUE:Boolean.FALSE;
            case 6: return allow_conn?Boolean.TRUE:Boolean.FALSE;
            case 7: return Short.valueOf(backup_level);
            case 8: return Short.valueOf(backup_retention);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public PostgresServerUser getDatDBA() {
	PostgresServerUser obj=table.connector.postgresServerUsers.get(datdba);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find PostgresServerUser: "+datdba));
	return obj;
    }

    public String getJdbcDriver() {
        return JDBC_DRIVER;
    }

    public String getJdbcUrl(boolean ipOnly) {
	AOServer ao=getPostgresServer().getAOServer();
        return
            "jdbc:postgresql://"
            + (ipOnly
               ?ao.getNetDevice(ao.getDaemonDeviceID().getName()).getPrimaryIPAddress().getIPAddress()
               :ao.getServer().getHostname()
            )
            + ':'
            + getPostgresServer().getNetBind().getPort().getPort()
            + '/'
            + getName()
        ;
    }
    
    public String getJdbcDocumentationUrl() {
        String version=getPostgresServer().getPostgresVersion().getTechnologyVersion(table.connector).getVersion();
        return "http://www.aoindustries.com/docs/postgresql-"+version+"/jdbc.html";
    }

    public String getName() {
	return name;
    }

    public PostgresEncoding getPostgresEncoding() {
	PostgresEncoding obj=table.connector.postgresEncodings.get(encoding);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find PostgresEncoding: "+encoding));
	return obj;
    }

    public PostgresServer getPostgresServer() {
	PostgresServer obj=table.connector.postgresServers.get(postgres_server);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find PostgresServer: "+postgres_server));
	return obj;
    }

    protected int getTableIDImpl() {
	return SchemaTable.POSTGRES_DATABASES;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	name=result.getString(2);
	postgres_server=result.getInt(3);
	datdba=result.getInt(4);
	encoding=result.getInt(5);
	is_template=result.getBoolean(6);
	allow_conn=result.getBoolean(7);
        backup_level=result.getShort(8);
        backup_retention=result.getShort(9);
    }

    public boolean isTemplate() {
	return is_template;
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	name=in.readUTF();
	postgres_server=in.readCompressedInt();
	datdba=in.readCompressedInt();
	encoding=in.readCompressedInt();
	is_template=in.readBoolean();
	allow_conn=in.readBoolean();
        backup_level=in.readShort();
        backup_retention=in.readShort();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();
        
        PostgresServer ps=getPostgresServer();
        if(!allow_conn) reasons.add(new CannotRemoveReason<PostgresDatabase>("Not allowed to drop a PostgreSQL database that does not allow connections: "+name+" on "+ps.getName()+" on "+ps.getAOServer().getServer().getHostname(), this));
        if(is_template) reasons.add(new CannotRemoveReason<PostgresDatabase>("Not allowed to drop a template PostgreSQL database: "+name+" on "+ps.getName()+" on "+ps.getAOServer().getServer().getHostname(), this));
        if(
            name.equals(AOINDUSTRIES)
            || name.equals(AOSERV)
            || name.equals(AOSERV_BACKUP)
            || name.equals(AOWEB)
        ) reasons.add(new CannotRemoveReason<PostgresDatabase>("Not allowed to drop a special PostgreSQL database: "+name+" on "+ps.getName()+" on "+ps.getAOServer().getServer().getHostname(), this));

        return reasons;
    }

    public void remove() {
	table.connector.requestUpdateIL(
            AOServProtocol.REMOVE,
            SchemaTable.POSTGRES_DATABASES,
            pkey
	);
    }

    public void setBackupRetention(short days) {
        table.connector.requestUpdateIL(AOServProtocol.SET_BACKUP_RETENTION, days, SchemaTable.POSTGRES_DATABASES, pkey);
    }

    String toStringImpl() {
	return name;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeUTF(name);
	out.writeCompressedInt(postgres_server);
	out.writeCompressedInt(datdba);
	out.writeCompressedInt(encoding);
	out.writeBoolean(is_template);
	out.writeBoolean(allow_conn);
        out.writeShort(backup_level);
        out.writeShort(backup_retention);
    }
}