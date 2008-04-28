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
    static final String COLUMN_NAME_name = "name";
    static final String COLUMN_POSTGRES_SERVER_name = "postgres_server";

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
    private boolean enable_postgis;

    public boolean allowsConnections() {
	return allow_conn;
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
                masterOut.writeCompressedInt(AOServProtocol.CommandID.DUMP_POSTGRES_DATABASE.ordinal());
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

    /**
     * Indicates that PostGIS should be enabled for this database.
     */
    public boolean getEnablePostgis() {
        return enable_postgis;
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return pkey;
            case 1: return name;
            case COLUMN_POSTGRES_SERVER: return postgres_server;
            case COLUMN_DATDBA: return datdba;
            case 4: return encoding;
            case 5: return is_template;
            case 6: return allow_conn;
            case 7: return enable_postgis;
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
               :ao.getHostname()
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

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.POSTGRES_DATABASES;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	name=result.getString(2);
	postgres_server=result.getInt(3);
	datdba=result.getInt(4);
	encoding=result.getInt(5);
	is_template=result.getBoolean(6);
	allow_conn=result.getBoolean(7);
        enable_postgis=result.getBoolean(8);
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
        enable_postgis=in.readBoolean();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();
        
        PostgresServer ps=getPostgresServer();
        if(!allow_conn) reasons.add(new CannotRemoveReason<PostgresDatabase>("Not allowed to drop a PostgreSQL database that does not allow connections: "+name+" on "+ps.getName()+" on "+ps.getAOServer().getHostname(), this));
        if(is_template) reasons.add(new CannotRemoveReason<PostgresDatabase>("Not allowed to drop a template PostgreSQL database: "+name+" on "+ps.getName()+" on "+ps.getAOServer().getHostname(), this));
        if(
            name.equals(AOINDUSTRIES)
            || name.equals(AOSERV)
            || name.equals(AOWEB)
        ) reasons.add(new CannotRemoveReason<PostgresDatabase>("Not allowed to drop a special PostgreSQL database: "+name+" on "+ps.getName()+" on "+ps.getAOServer().getHostname(), this));

        return reasons;
    }

    public void remove() {
	table.connector.requestUpdateIL(
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.POSTGRES_DATABASES,
            pkey
	);
    }

    String toStringImpl() {
	return name;
    }

    public void write(CompressedDataOutputStream out, String protocolVersion) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeUTF(name);
	out.writeCompressedInt(postgres_server);
	out.writeCompressedInt(datdba);
	out.writeCompressedInt(encoding);
	out.writeBoolean(is_template);
	out.writeBoolean(allow_conn);
        if(AOServProtocol.compareVersions(protocolVersion, AOServProtocol.VERSION_1_30)<=0) {
            out.writeShort(0);
            out.writeShort(7);
        }
        if(AOServProtocol.compareVersions(protocolVersion, AOServProtocol.VERSION_1_27)>=0) out.writeBoolean(enable_postgis);
    }
}
