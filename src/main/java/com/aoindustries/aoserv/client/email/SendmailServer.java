/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2018, 2019  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.email;

import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.net.IpAddress;
import com.aoindustries.aoserv.client.pki.Certificate;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.util.SystemdUtil;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.net.DomainName;
import com.aoindustries.net.InetAddress;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * An <code>SendmailServer</code> represents one running instance of Sendmail.
 *
 * @see  SendmailBind
 *
 * @author  AO Industries, Inc.
 */
final public class SendmailServer extends CachedObjectIntegerKey<SendmailServer> {

	static final int
		COLUMN_ID = 0,
		COLUMN_AO_SERVER = 1,
		COLUMN_PACKAGE = 3,
		COLUMN_SERVER_CERTIFICATE = 5,
		COLUMN_CLIENT_CERTIFICATE = 6
	;
	static final String COLUMN_AO_SERVER_name = "ao_server";
	static final String COLUMN_NAME_name = "name";

	/**
	 * Default value for sendmail_servers.allow_plaintext_auth
	 */
	public static final boolean DEFAULT_ALLOW_PLAINTEXT_AUTH = false;

	/**
	 * Default value for sendmail_servers.max_queue_children
	 */
	public static final int DEFAULT_MAX_QUEUE_CHILDREN = 100;

	/**
	 * Default value for sendmail_servers.nice_queue_run
	 */
	public static final int DEFAULT_NICE_QUEUE_RUN = 10;

	/**
	 * Default value for sendmail_servers.delay_la
	 */
	public static final int DEFAULT_DELAY_LA = 40;

	/**
	 * Default value for sendmail_servers.queue_la
	 */
	public static final int DEFAULT_QUEUE_LA = 50;

	/**
	 * Default value for sendmail_servers.refuse_la
	 */
	public static final int DEFAULT_REFUSE_LA = 80;

	/**
	 * Default value for sendmail_servers.max_daemon_children
	 */
	public static final int DEFAULT_MAX_DAEMON_CHILDREN = 1000;

	/**
	 * Default value for sendmail_servers.bad_rcpt_throttle
	 */
	public static final int DEFAULT_BAD_RCPT_THROTTLE = 10;

	/**
	 * Default value for sendmail_servers.connection_rate_throttle
	 */
	public static final int DEFAULT_CONNECTION_RATE_THROTTLE = 100;

	/**
	 * Default value for sendmail_servers.max_message_size
	 */
	public static final int DEFAULT_MAX_MESSAGE_SIZE = 100000000;

	/**
	 * Default value for sendmail_servers.min_free_blocks
	 */
	public static final int DEFAULT_MIN_FREE_BLOCKS = 65536;

	private int ao_server;
	private String name;
	private int packageNum;
	private DomainName hostname;
	private int serverCertificate;
	private int clientCertificate;
	private boolean allowPlaintextAuth;
	private int maxQueueChildren;
	private int niceQueueRun;
	private int delayLA;
	private int queueLA;
	private int refuseLA;
	private int maxDaemonChildren;
	private int badRcptThrottle;
	private int connectionRateThrottle;
	private int maxMessageSize;
	private int minFreeBlocks;
	private int clientAddrInet;
	private int clientAddrInet6;

	@Override
	public String toStringImpl() {
		return name==null ? "sendmail" : ("sendmail(" + name + ')');
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_ID: return pkey;
			case COLUMN_AO_SERVER: return ao_server;
			case 2: return name;
			case COLUMN_PACKAGE: return packageNum;
			case 4: return hostname;
			case COLUMN_SERVER_CERTIFICATE: return serverCertificate;
			case COLUMN_CLIENT_CERTIFICATE: return clientCertificate;
			case 7: return allowPlaintextAuth;
			case 8: return maxQueueChildren==-1 ? null : maxQueueChildren;
			case 9: return niceQueueRun==-1 ? null : niceQueueRun;
			case 10: return delayLA==-1 ? null : delayLA;
			case 11: return queueLA==-1 ? null : queueLA;
			case 12: return refuseLA==-1 ? null : refuseLA;
			case 13: return maxDaemonChildren==-1 ? null : maxDaemonChildren;
			case 14: return badRcptThrottle==-1 ? null : badRcptThrottle;
			case 15: return connectionRateThrottle==-1 ? null : connectionRateThrottle;
			case 16: return maxMessageSize==-1 ? null : maxMessageSize;
			case 17: return minFreeBlocks==-1 ? null : minFreeBlocks;
			case 18: return clientAddrInet==-1 ? null : clientAddrInet;
			case 19: return clientAddrInet6==-1 ? null : clientAddrInet6;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.SENDMAIL_SERVERS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			int pos = 1;
			pkey = result.getInt(pos++);
			ao_server = result.getInt(pos++);
			name = result.getString(pos++);
			packageNum = result.getInt(pos++);
			hostname = DomainName.valueOf(result.getString(pos++));
			serverCertificate = result.getInt(pos++);
			clientCertificate = result.getInt(pos++);
			allowPlaintextAuth = result.getBoolean(pos++);
			maxQueueChildren = result.getInt(pos++);
			if(result.wasNull()) maxQueueChildren = -1;
			niceQueueRun = result.getInt(pos++);
			if(result.wasNull()) niceQueueRun = -1;
			delayLA = result.getInt(pos++);
			if(result.wasNull()) delayLA = -1;
			queueLA = result.getInt(pos++);
			if(result.wasNull()) queueLA = -1;
			refuseLA = result.getInt(pos++);
			if(result.wasNull()) refuseLA = -1;
			maxDaemonChildren = result.getInt(pos++);
			if(result.wasNull()) maxDaemonChildren = -1;
			badRcptThrottle = result.getInt(pos++);
			if(result.wasNull()) badRcptThrottle = -1;
			connectionRateThrottle = result.getInt(pos++);
			if(result.wasNull()) connectionRateThrottle = -1;
			maxMessageSize = result.getInt(pos++);
			if(result.wasNull()) maxMessageSize = -1;
			minFreeBlocks = result.getInt(pos++);
			if(result.wasNull()) minFreeBlocks = -1;
			clientAddrInet = result.getInt(pos++);
			if(result.wasNull()) clientAddrInet = -1;
			clientAddrInet6 = result.getInt(pos++);
			if(result.wasNull()) clientAddrInet6 = -1;
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = in.readCompressedInt();
			ao_server = in.readCompressedInt();
			name = in.readNullUTF();
			packageNum = in.readCompressedInt();
			hostname = DomainName.valueOf(in.readNullUTF());
			serverCertificate = in.readCompressedInt();
			clientCertificate = in.readCompressedInt();
			allowPlaintextAuth = in.readBoolean();
			maxQueueChildren = in.readCompressedInt();
			niceQueueRun = in.readCompressedInt();
			delayLA = in.readCompressedInt();
			queueLA = in.readCompressedInt();
			refuseLA = in.readCompressedInt();
			maxDaemonChildren = in.readCompressedInt();
			badRcptThrottle = in.readCompressedInt();
			connectionRateThrottle = in.readCompressedInt();
			maxMessageSize = in.readCompressedInt();
			minFreeBlocks = in.readCompressedInt();
			clientAddrInet = in.readCompressedInt();
			clientAddrInet6 = in.readCompressedInt();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(ao_server);
		out.writeNullUTF(name);
		out.writeCompressedInt(packageNum);
		out.writeNullUTF(Objects.toString(hostname, null));
		out.writeCompressedInt(serverCertificate);
		out.writeCompressedInt(clientCertificate);
		out.writeBoolean(allowPlaintextAuth);
		out.writeCompressedInt(maxQueueChildren);
		out.writeCompressedInt(niceQueueRun);
		out.writeCompressedInt(delayLA);
		out.writeCompressedInt(queueLA);
		out.writeCompressedInt(refuseLA);
		out.writeCompressedInt(maxDaemonChildren);
		out.writeCompressedInt(badRcptThrottle);
		out.writeCompressedInt(connectionRateThrottle);
		out.writeCompressedInt(maxMessageSize);
		out.writeCompressedInt(minFreeBlocks);
		out.writeCompressedInt(clientAddrInet);
		out.writeCompressedInt(clientAddrInet6);
	}

	public int getId() {
		return pkey;
	}

	public int getAOServer_server_pkey() {
		return ao_server;
	}

	public Server getAoServer() throws SQLException, IOException {
		Server obj = table.getConnector().getLinux().getServer().get(ao_server);
		if(obj == null) throw new SQLException("Unable to find AOServer: " + ao_server);
		return obj;
	}

	/**
	 * Gets the name of the sendmail server instance.  The default instance has a null name.
	 * Additional instances will have non-empty names.
	 * The name is unique per server, including only one default instance.
	 *
	 * @see #getSystemdEscapedName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the <a href="https://www.freedesktop.org/software/systemd/man/systemd.unit.html">systemd-encoded</a>
	 * name of the sendmail server instance.  The default instance has a null name.
	 * Additional instances will have non-empty names.
	 * The name is unique per server, including only one default instance.
	 *
	 * @see #getName()
	 * @see SystemdUtil#encode(java.lang.String)
	 */
	public String getSystemdEscapedName() {
		return SystemdUtil.encode(name);
	}

	public int getPackage_pkey() {
		return packageNum;
	}

	public Package getPackage() throws IOException, SQLException {
		// Package may be filtered
		return table.getConnector().getBilling().getPackage().get(packageNum);
	}

	/**
	 * The fully qualified hostname for <code>Dw</code>, <code>Dm</code>, and <code>Dj</code>.
	 *
	 * When {@code null}, defaults to {@link AOServer#getHostname()}.
	 */
	public DomainName getHostname() {
		return hostname;
	}

	public int getServerCertificate_pkey() {
		return serverCertificate;
	}

	/**
	 * Gets the server certificate for this server.
	 *
	 * @return  the server SSL certificate or {@code null} when filtered
	 */
	public Certificate getServerCertificate() throws SQLException, IOException {
		// May be filtered
		return table.getConnector().getPki().getCertificate().get(serverCertificate);
	}

	public int getClientCertificate_pkey() {
		return clientCertificate;
	}

	/**
	 * Gets the client certificate for this server.
	 *
	 * @return  the client SSL certificate or {@code null} when filtered
	 */
	public Certificate getClientCertificate() throws SQLException, IOException {
		// May be filtered
		return table.getConnector().getPki().getCertificate().get(clientCertificate);
	}

	/**
	 * Allows plaintext authentication (PLAIN/LOGIN) on non-TLS links.
	 * When enabled, removes "p" from AuthOptions.
	 */
	public boolean getAllowPlaintextAuth() {
		return allowPlaintextAuth;
	}

	/**
	 * The value for <code>confMAX_QUEUE_CHILDREN</code> or {@code -1} if not set.
	 */
	public int getMaxQueueChildren() {
		return maxQueueChildren;
	}

	/**
	 * The value for <code>confNICE_QUEUE_RUN</code> or {@code -1} if not set.
	 */
	public int getNiceQueueRun() {
		return niceQueueRun;
	}

	/**
	 * The value for <code>confDELAY_LA</code> or {@code -1} if not set.
	 */
	public int getDelayLA() {
		return delayLA;
	}

	/**
	 * The value for <code>confQUEUE_LA</code> or {@code -1} if not set.
	 */
	public int getQueueLA() {
		return queueLA;
	}

	/**
	 * The value for <code>confREFUSE_LA</code> or {@code -1} if not set.
	 */
	public int getRefuseLA() {
		return refuseLA;
	}

	/**
	 * The value for <code>confMAX_DAEMON_CHILDREN</code> or {@code -1} if not set.
	 */
	public int getMaxDaemonChildren() {
		return maxDaemonChildren;
	}

	/**
	 * The value for <code>confBAD_RCPT_THROTTLE</code> or {@code -1} if not set.
	 */
	public int getBadRcptThrottle() {
		return badRcptThrottle;
	}

	/**
	 * The value for <code>confCONNECTION_RATE_THROTTLE</code> or {@code -1} if not set.
	 */
	public int getConnectionRateThrottle() {
		return connectionRateThrottle;
	}

	/**
	 * The value for <code>confMAX_MESSAGE_SIZE</code> or {@code -1} if not set.
	 */
	public int getMaxMessageSize() {
		return maxMessageSize;
	}

	/**
	 * The value for <code>confMIN_FREE_BLOCKS</code> or {@code -1} if not set.
	 */
	public int getMinFreeBlocks() {
		return minFreeBlocks;
	}

	public Integer getClientAddrInet_id() {
		return clientAddrInet == -1 ? null : clientAddrInet;
	}

	/**
	 * The <code>Addr</code> for <code>ClientPortOptions</code> with <code>Family=inet</code> or {@code null} if not set.
	 */
	@SuppressWarnings("deprecation")
	public IpAddress getClientAddrInet() throws IOException, SQLException {
		if(clientAddrInet == -1) return null;
		IpAddress obj = table.getConnector().getNet().getIpAddress().get(clientAddrInet);
		if(obj == null) throw new SQLException("Unable to find IPAddress: " + clientAddrInet);
		InetAddress address = obj.getInetAddress();
		com.aoindustries.net.AddressFamily family = address.getAddressFamily();
		if(family != com.aoindustries.net.AddressFamily.INET) throw new SQLException("Unexpected address family for clientAddrInet #" + clientAddrInet + ": " + family);
		if(address.isUnspecified()) throw new SQLException("May not use unspecified address for clientAddrInet #" + clientAddrInet);
		if(!getAoServer().getServer().equals(obj.getDevice().getServer())) throw new SQLException("IPAddress is not on this server for clientAddrInet #" + clientAddrInet);
		return obj;
	}

	public Integer getClientAddrInet6_id() {
		return clientAddrInet6 == -1 ? null : clientAddrInet6;
	}

	/**
	 * The <code>Addr</code> for <code>ClientPortOptions</code> with <code>Family=inet6</code> or {@code null} if not set.
	 */
	@SuppressWarnings("deprecation")
	public IpAddress getClientAddrInet6() throws IOException, SQLException {
		if(clientAddrInet6 == -1) return null;
		IpAddress obj = table.getConnector().getNet().getIpAddress().get(clientAddrInet6);
		if(obj == null) throw new SQLException("Unable to find IPAddress: " + clientAddrInet6);
		InetAddress address = obj.getInetAddress();
		com.aoindustries.net.AddressFamily family = address.getAddressFamily();
		if(family != com.aoindustries.net.AddressFamily.INET6) throw new SQLException("Unexpected address family for clientAddrInet6 #" + clientAddrInet6 + ": " + family);
		if(address.isUnspecified()) throw new SQLException("May not use unspecified address for clientAddrInet6 #" + clientAddrInet6);
		if(!getAoServer().getServer().equals(obj.getDevice().getServer())) throw new SQLException("IPAddress is not on this server for clientAddrInet6 #" + clientAddrInet6);
		return obj;
	}

	public List<SendmailBind> getSendmailBinds() throws IOException, SQLException {
		return table.getConnector().getEmail().getSendmailBind().getSendmailBinds(this);
	}
}
