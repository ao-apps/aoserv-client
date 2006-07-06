package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * A limited number of hosts may connect to a <code>AOServer</code>'s daemon,
 * each is configured as an <code>AOServerDaemonHost</code>.
 *
 * @see  Server
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class AOServerDaemonHost extends CachedObjectIntegerKey<AOServerDaemonHost> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_AO_SERVER=1
    ;

    int aoServer;
    private String host;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_AO_SERVER: return Integer.valueOf(aoServer);
            case 2: return host;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getHost() {
	return host;
    }

    public AOServer getAOServer() {
	AOServer ao=table.connector.aoServers.get(aoServer);
	if(ao==null) throw new WrappedException(new SQLException("Unable to find AOServer: "+aoServer));
	return ao;
    }

    protected int getTableIDImpl() {
	return SchemaTable.AO_SERVER_DAEMON_HOSTS;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	aoServer=result.getInt(2);
	host=result.getString(3);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	aoServer=in.readCompressedInt();
	host=in.readUTF();
    }

    String toStringImpl() {
	return aoServer+'|'+host;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeCompressedInt(aoServer);
	out.writeUTF(host);
    }
}