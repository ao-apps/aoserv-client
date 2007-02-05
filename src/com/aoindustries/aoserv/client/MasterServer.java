package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * <code>MasterUser</code>s are restricted to data based on a list
 * of <code>Server</code>s they may access.  A <code>MasterServer</code>
 * grants a <code>MasterUser</code> permission to data associated with
 * a <code>Server</code>.  If a <code>MasterUser</code> does not have
 * any <code>MasterServer</code>s associated with it, it is granted
 * permissions to all servers.
 *
 * @see  MasterUser
 * @see  Server
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MasterServer extends CachedObjectIntegerKey<MasterServer> {

    static final int COLUMN_PKEY=0;

    private String username;
    private int server;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return username;
            case 2: return Integer.valueOf(server);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public MasterUser getMasterUser() {
	MasterUser obj=table.connector.masterUsers.get(username);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find MasterUser: "+username));
	return obj;
    }

    public Server getServer() {
	Server obj=table.connector.servers.get(server);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find Server: "+server));
	return obj;
    }
    
    public int getServerPKey() {
        return server;
    }

    protected int getTableIDImpl() {
	return SchemaTable.MASTER_SERVERS;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	username=result.getString(2);
	server=result.getInt(3);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	username=in.readUTF();
	server=in.readCompressedInt();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeUTF(username);
	out.writeCompressedInt(server);
    }
}