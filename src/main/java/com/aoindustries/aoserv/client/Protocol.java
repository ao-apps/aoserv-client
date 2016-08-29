/*
 * Copyright 2000-2013, 2016 by AO Industries, Inc.,
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
 * A <code>Protocol</code> represents one type of application
 * protocol used in <code>NetBind</code>s.  Monitoring is performed
 * in protocol-specific ways.
 *
 * @see  NetBind
 *
 * @author  AO Industries, Inc.
 */
final public class Protocol extends GlobalObjectStringKey<Protocol> {

	static final int COLUMN_PROTOCOL=0;
	static final String COLUMN_PORT_name = "port";
	static final String COLUMN_NET_PROTOCOL_name = "net_protocol";

	private int port;
	private String name;
	private boolean is_user_service;
	private String net_protocol;

	public static final String
		AOSERV_DAEMON="aoserv-daemon",
		AOSERV_DAEMON_SSL="aoserv-daemon-ssl",
		AOSERV_MASTER="aoserv-master",
		AOSERV_MASTER_SSL="aoserv-master-ssl",
		AUTH="auth",
		CVSPSERVER="cvspserver",
		DNS="DNS",
		FTP="FTP",
		FTP_DATA="FTP-DATA",
		HTTP="HTTP",
		HTTPS="HTTPS",
		HYPERSONIC="hypersonic",
		IMAP2="IMAP2",
		JMX="JMX",
		JNP="JNP",
		MILTER="milter",
		MYSQL="MySQL",
		NTALK="ntalk",
		POP3="POP3",
		POSTGRESQL="PostgreSQL",
		RMI="RMI",
		SIEVE="sieve",
		SIMAP="SIMAP",
		SPOP3="SPOP3",
		SSH="SSH",
		SMTP="SMTP",
		SMTPS="SMTPS",
		SUBMISSION="submission",
		TALK="talk",
		TELNET="Telnet",
		TOMCAT4_SHUTDOWN="tomcat4-shutdown",
		WEBSERVER="webserver"
	;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PROTOCOL: return pkey;
			case 1: return port;
			case 2: return name;
			case 3: return is_user_service;
			case 4: return net_protocol;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public HttpdJKProtocol getHttpdJKProtocol(AOServConnector connector) throws IOException, SQLException {
		return connector.getHttpdJKProtocols().get(pkey);
	}

	public String getName() {
		return name;
	}

	public boolean isUserService() {
		return is_user_service;
	}

	public NetProtocol getNetProtocol(AOServConnector connector) throws SQLException, IOException {
		NetProtocol np=connector.getNetProtocols().get(net_protocol);
		if(np==null) throw new SQLException("Unable to find NetProtocol: "+net_protocol);
		return np;
	}

	public NetPort getPort(AOServConnector connector) throws SQLException {
		NetPort obj=connector.getNetPorts().get(port);
		if(obj==null) throw new SQLException("Unable to find NetPort: "+port);
		return obj;
	}

	/**
	 * Gets the unique name of the protocol.
	 */
	public String getProtocol() {
		return pkey;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.PROTOCOLS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getString(1);
		port = result.getInt(2);
		name = result.getString(3);
		is_user_service = result.getBoolean(4);
		net_protocol = result.getString(5);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF().intern();
		port=in.readCompressedInt();
		name=in.readUTF();
		is_user_service=in.readBoolean();
		net_protocol=in.readUTF().intern();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey);
		out.writeCompressedInt(port);
		out.writeUTF(name);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_105)>=0) {
			out.writeBoolean(is_user_service);
			out.writeUTF(net_protocol);
		}
	}
}
