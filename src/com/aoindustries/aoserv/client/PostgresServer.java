package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>PostgresServer</code> corresponds to a unique PostgreSQL install
 * space on one server.  The server name must be unique per server.
 * <code>PostgresDatabase</code>s and <code>PostgresServerUser</code>s are
 * unique per <code>PostgresServer</code>.
 *
 * @see  PostgresVersion
 * @see  PostgresDatabase
 * @see  PostgresServerUser
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresServer extends CachedObjectIntegerKey<PostgresServer> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_AO_SERVER=2,
        COLUMN_NET_BIND=5
    ;

    /**
     * The directory that contains the PostgreSQL data files.
     */
    public static final String DATA_BASE_DIR="/var/lib/pgsql";

    /**
     * The maximum length of the name.
     */
    public static final int MAX_SERVER_NAME_LENGTH=31;
    
    String name;
    int ao_server;
    private int version;
    private int max_connections;
    int net_bind;
    private int sort_mem;
    private int shared_buffers;
    private boolean fsync;

    public int addPostgresDatabase(
        String name,
        PostgresServerUser datdba,
        PostgresEncoding encoding,
        boolean enablePostgis
    ) {
	return table.connector.postgresDatabases.addPostgresDatabase(
            name,
            this,
            datdba,
            encoding,
            enablePostgis
	);
    }

    public static void checkServerName(String name) throws IllegalArgumentException {
	// Must be a-z or 0-9 first, then a-z or 0-9 or . or _
	int len = name.length();
	if (len == 0 || len > MAX_SERVER_NAME_LENGTH) throw new IllegalArgumentException("PostgreSQL server name should not exceed "+MAX_SERVER_NAME_LENGTH+" characters.");

        // The first character must be [a-z] or [0-9]
	char ch = name.charAt(0);
	if ((ch < 'a' || ch > 'z') && (ch<'0' || ch>'9')) throw new IllegalArgumentException("PostgreSQL server names must start with [a-z] or [0-9]");
        // The rest may have additional characters
	for (int c = 1; c < len; c++) {
            ch = name.charAt(c);
            if (
                (ch<'a' || ch>'z')
                && (ch<'0' || ch>'9')
                && ch!='.'
                && ch!='_'
            ) throw new IllegalArgumentException("PostgreSQL server names may only contain [a-z], [0-9], period (.), and underscore (_)");
	}
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return name;
            case COLUMN_AO_SERVER: return Integer.valueOf(ao_server);
            case 3: return Integer.valueOf(version);
            case 4: return Integer.valueOf(max_connections);
            case COLUMN_NET_BIND: return Integer.valueOf(net_bind);
            case 6: return Integer.valueOf(sort_mem);
            case 7: return Integer.valueOf(shared_buffers);
            case 8: return fsync?Boolean.TRUE:Boolean.FALSE;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getDataDirectory() {
        return DATA_BASE_DIR+'/'+name;
    }

    public String getName() {
	return name;
    }

    public List<PostgresBackup> getPostgresBackups() {
	return table.connector.postgresBackups.getPostgresBackups(this);
    }

    public PostgresVersion getPostgresVersion() {
	PostgresVersion obj=table.connector.postgresVersions.get(version);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find PostgresVersion: "+version));
	return obj;
    }

    public AOServer getAOServer() {
	AOServer ao=table.connector.aoServers.get(ao_server);
	if(ao==null) throw new WrappedException(new SQLException("Unable to find AOServer: "+ao_server));
	return ao;
    }

    public int getMaxConnections() {
        return max_connections;
    }
    
    public NetBind getNetBind() {
        NetBind nb=table.connector.netBinds.get(net_bind);
        if(nb==null) throw new WrappedException(new SQLException("Unable to find NetBind: "+net_bind));
        return nb;
    }
    
    public PostgresDatabase getPostgresDatabase(String name) {
	return table.connector.postgresDatabases.getPostgresDatabase(name, this);
    }

    public List<PostgresDatabase> getPostgresDatabases() {
	return table.connector.postgresDatabases.getPostgresDatabases(this);
    }

    public PostgresServerUser getPostgresServerUser(String username) {
	return table.connector.postgresServerUsers.getPostgresServerUser(username, this);
    }

    public List<PostgresServerUser> getPostgresServerUsers() {
	return table.connector.postgresServerUsers.getPostgresServerUsers(this);
    }

    public List<PostgresUser> getPostgresUsers() {
	List<PostgresServerUser> psu=getPostgresServerUsers();
	int len=psu.size();
	List<PostgresUser> pu=new ArrayList<PostgresUser>(len);
	for(int c=0;c<len;c++) pu.add(psu.get(c).getPostgresUser());
	return pu;
    }

    public int getSortMem() {
        return sort_mem;
    }
    
    public int getSharedBuffers() {
        return shared_buffers;
    }
    
    public boolean getFSync() {
        return fsync;
    }

    protected int getTableIDImpl() {
	return SchemaTable.POSTGRES_SERVERS;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	name=result.getString(2);
	ao_server=result.getInt(3);
	version=result.getInt(4);
        max_connections=result.getInt(5);
        net_bind=result.getInt(6);
        sort_mem=result.getInt(7);
        shared_buffers=result.getInt(8);
        fsync=result.getBoolean(9);
    }

    public boolean isPostgresDatabaseNameAvailable(String name) {
	return table.connector.postgresDatabases.isPostgresDatabaseNameAvailable(name, this);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	name=in.readUTF();
	ao_server=in.readCompressedInt();
	version=in.readCompressedInt();
        max_connections=in.readCompressedInt();
        net_bind=in.readCompressedInt();
        sort_mem=in.readCompressedInt();
        shared_buffers=in.readCompressedInt();
        fsync=in.readBoolean();
    }

    public void restartPostgreSQL() {
        table.connector.requestUpdate(AOServProtocol.RESTART_POSTGRESQL, pkey);
    }

    public void startPostgreSQL() {
        table.connector.requestUpdate(AOServProtocol.START_POSTGRESQL, pkey);
    }

    public void stopPostgreSQL() {
        table.connector.requestUpdate(AOServProtocol.STOP_POSTGRESQL, pkey);
    }

    String toStringImpl() {
        return name+" on "+getAOServer().getServer().hostname;
    }

    public void write(CompressedDataOutputStream out, String protocolVersion) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeUTF(name);
	out.writeCompressedInt(ao_server);
	out.writeCompressedInt(version);
        out.writeCompressedInt(max_connections);
        out.writeCompressedInt(net_bind);
        out.writeCompressedInt(sort_mem);
        out.writeCompressedInt(shared_buffers);
        out.writeBoolean(fsync);
        if(AOServProtocol.compareVersions(protocolVersion, AOServProtocol.VERSION_1_0_A_130)<=0) {
            out.writeCompressedInt(-1);
        }
    }
}
