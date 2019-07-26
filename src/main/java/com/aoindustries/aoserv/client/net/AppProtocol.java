/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.net;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.web.tomcat.JkProtocol;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.net.Port;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

/**
 * A <code>Protocol</code> represents one type of application
 * protocol used in <code>NetBind</code>s.  Monitoring is performed
 * in protocol-specific ways.
 *
 * @see  NetBind
 *
 * @author  AO Industries, Inc.
 */
final public class AppProtocol extends GlobalObjectStringKey<AppProtocol> {

	static final int COLUMN_PROTOCOL = 0;
	static final String COLUMN_PORT_name = "port";

	private Port port;
	private String name;
	private boolean is_user_service;

	public static final String
		AOSERV_DAEMON = "aoserv-daemon",
		AOSERV_DAEMON_SSL = "aoserv-daemon-ssl",
		AOSERV_MASTER = "aoserv-master",
		AOSERV_MASTER_SSL = "aoserv-master-ssl",
		AUTH = "auth",
		CVSPSERVER = "cvspserver",
		DNS = "DNS",
		FTP = "FTP",
		FTP_DATA = "FTP-DATA",
		HTTP = "HTTP",
		HTTPS = "HTTPS",
		HYPERSONIC = "hypersonic",
		IMAP2 = "IMAP2",
		JMX = "JMX",
		JNP = "JNP",
		MEMCACHED = "memcached",
		MILTER = "milter",
		MYSQL = "MySQL",
		NTALK = "ntalk",
		POP3 = "POP3",
		POSTGRESQL = "PostgreSQL",
		RFB = "RFB",
		RMI = "RMI",
		SIEVE = "sieve",
		SIMAP = "SIMAP",
		SPAMD = "spamd",
		SPOP3 = "SPOP3",
		SSH = "SSH",
		SMTP = "SMTP",
		SMTPS = "SMTPS",
		SUBMISSION = "submission",
		TALK = "talk",
		TELNET = "Telnet",
		TOMCAT4_SHUTDOWN = "tomcat4-shutdown",
		WEBSERVER = "webserver"
	;

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PROTOCOL: return pkey;
			case 1: return port;
			case 2: return name;
			case 3: return is_user_service;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public JkProtocol getHttpdJKProtocol(AOServConnector connector) throws IOException, SQLException {
		return connector.getWeb_tomcat().getJkProtocol().get(pkey);
	}

	public String getName() {
		return name;
	}

	public boolean isUserService() {
		return is_user_service;
	}

	public Port getPort() {
		return port;
	}

	/**
	 * Gets the unique name of the protocol.
	 */
	public String getProtocol() {
		return pkey;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.PROTOCOLS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = result.getString(1);
			int portNum = result.getInt(2);
			name = result.getString(3);
			is_user_service = result.getBoolean(4);
			port = Port.valueOf(
				portNum,
				com.aoindustries.net.Protocol.valueOf(result.getString(5).toUpperCase(Locale.ROOT))
			);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			pkey = in.readUTF().intern();
			int portNum = in.readCompressedInt();
			name = in.readUTF();
			is_user_service = in.readBoolean();
			port = Port.valueOf(
				portNum,
				in.readEnum(com.aoindustries.net.Protocol.class)
			);
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey);
		out.writeCompressedInt(port.getPort());
		out.writeUTF(name);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_105) >= 0) {
			out.writeBoolean(is_user_service);
			if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_80_0) < 0) {
				out.writeUTF(port.getProtocol().name().toLowerCase(Locale.ROOT));
			} else {
				out.writeEnum(port.getProtocol());
			}
		}
	}
}
