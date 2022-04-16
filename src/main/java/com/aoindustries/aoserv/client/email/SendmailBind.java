/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoindustries.aoserv.client.email;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.net.IpAddress;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Each <code>SendmailServer</code> may listen for network connections on
 * multiple <code>NetBind</code>s.  A <code>SendmailBind</code> ties
 * <code>SendmailServer</code>s to <code>NetBinds</code>.
 *
 * @see  SendmailServer
 * @see  Bind
 *
 * @author  AO Industries, Inc.
 */
public final class SendmailBind extends CachedObjectIntegerKey<SendmailBind> {

	static final int
		COLUMN_NET_BIND = 0,
		COLUMN_SENDMAIL_SERVER = 1
	;
	static final String COLUMN_NET_BIND_name = "net_bind";

	private int sendmail_server;
	private String name;

	/**
	 * @deprecated  Only required for implementation, do not use directly.
	 *
	 * @see  #init(java.sql.ResultSet)
	 * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
	 */
	@Deprecated/* Java 9: (forRemoval = true) */
	public SendmailBind() {
		// Do nothing
	}

	@Override
	public String toStringImpl() throws SQLException, IOException {
		SendmailServer server = getSendmailServer();
		Bind bind = getNetBind();
		return server.toStringImpl() + '|' + bind.toStringImpl();
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_NET_BIND: return pkey;
			case COLUMN_SENDMAIL_SERVER: return sendmail_server;
			case 2: return name;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.SENDMAIL_BINDS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getInt(1);
		sendmail_server = result.getInt(2);
		name = result.getString(3);
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		pkey = in.readCompressedInt();
		sendmail_server = in.readCompressedInt();
		name = in.readNullUTF();
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(sendmail_server);
		out.writeNullUTF(name);
	}

	public Bind getNetBind() throws SQLException, IOException {
		Bind obj = table.getConnector().getNet().getBind().get(pkey);
		if(obj == null) throw new SQLException("Unable to find NetBind: " + pkey);
		return obj;
	}

	public SendmailServer getSendmailServer() throws SQLException, IOException {
		SendmailServer obj = table.getConnector().getEmail().getSendmailServer().get(sendmail_server);
		if(obj == null) throw new SQLException("Unable to find SendmailServer: " + sendmail_server);
		return obj;
	}

	/**
	 * The <code>Name</code> for <code>DaemonPortOptions</code> or {@code null} if not set.
	 * The name is unique per-{@link SendmailServer}.
	 * The name will never contain a space.
	 * Will default to a generated value based on {@link SendmailServer#getHostname()} or
	 * {@link IpAddress#getHostname()} if not specified.
	 */
	public String getName() {
		assert name == null || name.indexOf(' ') == -1;
		return name;
	}
}
