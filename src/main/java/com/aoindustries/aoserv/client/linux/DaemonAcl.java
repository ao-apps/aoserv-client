/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.linux;

import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.dto.DtoFactory;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.net.HostAddress;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A limited number of hosts may connect to a {@link Server server's} daemon,
 * each is configured as an <code>AOServerDaemonHost</code>.
 *
 * @see  Server
 *
 * @author  AO Industries, Inc.
 */
public final class DaemonAcl extends CachedObjectIntegerKey<DaemonAcl>
	implements DtoFactory<com.aoindustries.aoserv.client.dto.LinuxDaemonAcl> {

	static final int
		COLUMN_PKEY=0,
		COLUMN_AO_SERVER=1
	;
	static final String COLUMN_AO_SERVER_name = "ao_server";
	static final String COLUMN_HOST_name = "host";

	int aoServer;
	private HostAddress host;

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_AO_SERVER: return aoServer;
			case 2: return host;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public HostAddress getHost() {
		return host;
	}

	public Server getServer() throws SQLException, IOException {
		Server ao=table.getConnector().getLinux().getServer().get(aoServer);
		if(ao==null) throw new SQLException("Unable to find linux.Server: "+aoServer);
		return ao;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.AO_SERVER_DAEMON_HOSTS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = result.getInt(1);
			aoServer = result.getInt(2);
			host = HostAddress.valueOf(result.getString(3));
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			pkey = in.readCompressedInt();
			aoServer = in.readCompressedInt();
			host = HostAddress.valueOf(in.readUTF().intern());
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public String toStringImpl() {
		return aoServer+"|"+host;
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(aoServer);
		out.writeUTF(host.toString());
	}

	// <editor-fold defaultstate="collapsed" desc="DTO">
	@Override
	public com.aoindustries.aoserv.client.dto.LinuxDaemonAcl getDto() {
		return new com.aoindustries.aoserv.client.dto.LinuxDaemonAcl(getPkey(), aoServer, getDto(host));
	}
	// </editor-fold>
}
