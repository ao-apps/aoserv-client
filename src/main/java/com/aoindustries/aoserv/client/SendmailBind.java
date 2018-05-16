/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Each <code>SendmailServer</code> may listen for network connections on
 * multiple <code>NetBind</code>s.  A <code>SendmailBind</code> ties
 * <code>SendmailServer</code>s to <code>NetBinds</code>.
 *
 * @see  SendmailServer
 * @see  NetBind
 *
 * @author  AO Industries, Inc.
 */
final public class SendmailBind extends CachedObjectIntegerKey<SendmailBind> {

	static final int
		COLUMN_NET_BIND = 0,
		COLUMN_SENDMAIL_SERVER = 1
	;
	static final String COLUMN_NET_BIND_name = "net_bind";

	int sendmail_server;
	private String name;

	@Override
	String toStringImpl() throws SQLException, IOException {
		SendmailServer server = getSendmailServer();
		NetBind bind = getNetBind();
		return server.toStringImpl() + '|' + bind.toStringImpl();
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_NET_BIND: return pkey;
			case COLUMN_SENDMAIL_SERVER: return sendmail_server;
			case 2: return name;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.SENDMAIL_BINDS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getInt(1);
		sendmail_server = result.getInt(2);
		name = result.getString(3);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey = in.readCompressedInt();
		sendmail_server = in.readCompressedInt();
		name = in.readNullUTF();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(sendmail_server);
		out.writeNullUTF(name);
	}

	public NetBind getNetBind() throws SQLException, IOException {
		NetBind obj = table.connector.getNetBinds().get(pkey);
		if(obj == null) throw new SQLException("Unable to find NetBind: " + pkey);
		return obj;
	}

	public SendmailServer getSendmailServer() throws SQLException, IOException {
		SendmailServer obj = table.connector.getSendmailServers().get(sendmail_server);
		if(obj == null) throw new SQLException("Unable to find SendmailServer: " + sendmail_server);
		return obj;
	}

	/**
	 * The <code>Name</code> for <code>DaemonPortOptions</code> or {@code null} if not set.
	 * The name is unique per-{@link SendmailServer}.
	 * Will default to a generated value based on {@link SendmailServer#getHostname()} or
	 * {@link IPAddress#getHostname()} if not specified.
	 */
	public String getName() {
		return name;
	}
}
