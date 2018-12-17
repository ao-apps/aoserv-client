/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.master;

import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.validation.ValidationException;
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
 * @see  User
 * @see  Host
 *
 * @author  AO Industries, Inc.
 */
final public class UserHost extends CachedObjectIntegerKey<UserHost> {

	static final int COLUMN_PKEY=0;
	static final String COLUMN_USERNAME_name = "username";
	static final String COLUMN_SERVER_name = "server";

	private com.aoindustries.aoserv.client.account.User.Name username;
	private int server;

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case 1: return username;
			case 2: return server;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public User getMasterUser() throws SQLException, IOException {
		User obj=table.getConnector().getMaster().getUser().get(username);
		if(obj==null) throw new SQLException("Unable to find MasterUser: "+username);
		return obj;
	}

	public Host getServer() throws SQLException, IOException {
		Host obj=table.getConnector().getNet().getHost().get(server);
		if(obj==null) throw new SQLException("Unable to find Server: "+server);
		return obj;
	}

	public int getServerPKey() {
		return server;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.MASTER_SERVERS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = result.getInt(1);
			username = com.aoindustries.aoserv.client.account.User.Name.valueOf(result.getString(2));
			server = result.getInt(3);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = in.readCompressedInt();
			username = com.aoindustries.aoserv.client.account.User.Name.valueOf(in.readUTF()).intern();
			server = in.readCompressedInt();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(username.toString());
		out.writeCompressedInt(server);
	}
}
