/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

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
 * @author  AO Industries, Inc.
 */
final public class MasterServer extends CachedObjectIntegerKey<MasterServer> {

	static final int COLUMN_PKEY=0;
	static final String COLUMN_USERNAME_name = "username";
	static final String COLUMN_SERVER_name = "server";

	private String username;
	private int server;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case 1: return username;
			case 2: return server;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public MasterUser getMasterUser() throws SQLException, IOException {
		MasterUser obj=table.connector.getMasterUsers().get(username);
		if(obj==null) throw new SQLException("Unable to find MasterUser: "+username);
		return obj;
	}

	public Server getServer() throws SQLException, IOException {
		Server obj=table.connector.getServers().get(server);
		if(obj==null) throw new SQLException("Unable to find Server: "+server);
		return obj;
	}

	public int getServerPKey() {
		return server;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.MASTER_SERVERS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		username=result.getString(2);
		server=result.getInt(3);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		username=in.readUTF().intern();
		server=in.readCompressedInt();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(username);
		out.writeCompressedInt(server);
	}
}
