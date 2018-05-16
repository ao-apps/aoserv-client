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

import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.lang.ObjectUtils;
import com.aoindustries.net.AddressFamily;
import com.aoindustries.net.DomainName;
import com.aoindustries.net.InetAddress;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * An <code>SendmailServer</code> represents one running instance of Sendmail.
 *
 * @see  SendmailBind
 *
 * @author  AO Industries, Inc.
 */
final public class SendmailServer extends CachedObjectIntegerKey<SendmailServer> {

	static final int
		COLUMN_PKEY = 0,
		COLUMN_AO_SERVER = 1,
		COLUMN_PACKAGE = 3
	;
	static final String COLUMN_AO_SERVER_name = "ao_server";
	static final String COLUMN_NAME_name = "name";

	int ao_server;
	private String name;
	private int packageNum;
	private DomainName hostname;
	private UnixPath cacertPath;
	private UnixPath cacert;
	private UnixPath serverCert;
	private UnixPath serverKey;
	private UnixPath clientCert;
	private UnixPath clientKey;
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
	String toStringImpl() {
		return name==null ? "sendmail" : ("sendmail(" + name + ')');
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_AO_SERVER: return ao_server;
			case 2: return name;
			case COLUMN_PACKAGE: return packageNum;
			case 4: return hostname;
			case 5: return cacertPath;
			case 6: return cacert;
			case 7: return serverCert;
			case 8: return serverKey;
			case 9: return clientCert;
			case 10: return clientKey;
			case 11: return allowPlaintextAuth;
			case 12: return maxQueueChildren==-1 ? null : maxQueueChildren;
			case 13: return niceQueueRun==-1 ? null : niceQueueRun;
			case 14: return delayLA==-1 ? null : delayLA;
			case 15: return queueLA==-1 ? null : queueLA;
			case 16: return refuseLA==-1 ? null : refuseLA;
			case 17: return maxDaemonChildren==-1 ? null : maxDaemonChildren;
			case 18: return badRcptThrottle==-1 ? null : badRcptThrottle;
			case 19: return connectionRateThrottle==-1 ? null : connectionRateThrottle;
			case 20: return maxMessageSize==-1 ? null : maxMessageSize;
			case 21: return minFreeBlocks==-1 ? null : minFreeBlocks;
			case 22: return clientAddrInet==-1 ? null : clientAddrInet;
			case 23: return clientAddrInet6==-1 ? null : clientAddrInet6;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.SENDMAIL_SERVERS;
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
			cacertPath = UnixPath.valueOf(result.getString(pos++));
			cacert = UnixPath.valueOf(result.getString(pos++));
			serverCert = UnixPath.valueOf(result.getString(pos++));
			serverKey = UnixPath.valueOf(result.getString(pos++));
			clientCert = UnixPath.valueOf(result.getString(pos++));
			clientKey = UnixPath.valueOf(result.getString(pos++));
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
			cacertPath = UnixPath.valueOf(in.readUTF());
			cacert = UnixPath.valueOf(in.readUTF());
			serverCert = UnixPath.valueOf(in.readUTF());
			serverKey = UnixPath.valueOf(in.readUTF());
			clientCert = UnixPath.valueOf(in.readUTF());
			clientKey = UnixPath.valueOf(in.readUTF());
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
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(ao_server);
		out.writeNullUTF(name);
		out.writeCompressedInt(packageNum);
		out.writeNullUTF(ObjectUtils.toString(hostname));
		out.writeUTF(cacertPath.toString());
		out.writeUTF(cacert.toString());
		out.writeUTF(serverCert.toString());
		out.writeUTF(serverKey.toString());
		out.writeUTF(clientCert.toString());
		out.writeUTF(clientKey.toString());
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

	public AOServer getAOServer() throws SQLException, IOException {
		AOServer obj = table.connector.getAoServers().get(ao_server);
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

	public Package getPackage() throws IOException, SQLException {
		// Package may be filtered
		return table.connector.getPackages().get(packageNum);
	}

	/**
	 * The fully qualified hostname for <code>Dw</code>, <code>Dm</code>, and <code>Dj</code>.
	 *
	 * When {@code null}, defaults to {@link AOServer#getHostname()}.
	 */
	public DomainName getHostname() {
		return hostname;
	}

	/**
	 * The path for <code>confCACERT_PATH</code>.
	 */
	public UnixPath getCacertPath() {
		return cacertPath;
	}

	/**
	 * The path for <code>confCACERT</code>.
	 */
	public UnixPath getCacert() {
		return cacert;
	}

	/**
	 * The path for <code>confSERVER_CERT</code>.
	 */
	public UnixPath getServerCert() {
		return serverCert;
	}

	/**
	 * The path for <code>confSERVER_KEY</code>.
	 */
	public UnixPath getServerKey() {
		return serverKey;
	}

	/**
	 * The path for <code>confCLIENT_CERT</code>.
	 */
	public UnixPath getClientCert() {
		return clientCert;
	}

	/**
	 * The path for <code>confCLIENT_KEY</code>.
	 */
	public UnixPath getClientKey() {
		return clientKey;
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

	/**
	 * The <code>Addr</code> for <code>ClientPortOptions</code> with <code>Family=inet</code> or {@code null} if not set.
	 */
	public IPAddress getClientAddrInet() throws IOException, SQLException {
		if(clientAddrInet == -1) return null;
		IPAddress obj = table.connector.getIpAddresses().get(clientAddrInet);
		if(obj == null) throw new SQLException("Unable to find IPAddress: " + clientAddrInet);
		InetAddress address = obj.getInetAddress();
		AddressFamily family = address.getAddressFamily();
		if(family != AddressFamily.INET) throw new SQLException("Unexpected address family for clientAddrInet #" + clientAddrInet + ": " + family);
		if(address.isUnspecified()) throw new SQLException("May not use unspecified address for clientAddrInet #" + clientAddrInet);
		if(!getAOServer().getServer().equals(obj.getNetDevice().getServer())) throw new SQLException("IPAddress is not on this server for clientAddrInet #" + clientAddrInet);
		return obj;
	}

	/**
	 * The <code>Addr</code> for <code>ClientPortOptions</code> with <code>Family=inet6</code> or {@code null} if not set.
	 */
	public IPAddress getClientAddrInet6() throws IOException, SQLException {
		if(clientAddrInet6 == -1) return null;
		IPAddress obj = table.connector.getIpAddresses().get(clientAddrInet6);
		if(obj == null) throw new SQLException("Unable to find IPAddress: " + clientAddrInet6);
		InetAddress address = obj.getInetAddress();
		AddressFamily family = address.getAddressFamily();
		if(family != AddressFamily.INET6) throw new SQLException("Unexpected address family for clientAddrInet6 #" + clientAddrInet6 + ": " + family);
		if(address.isUnspecified()) throw new SQLException("May not use unspecified address for clientAddrInet6 #" + clientAddrInet6);
		if(!getAOServer().getServer().equals(obj.getNetDevice().getServer())) throw new SQLException("IPAddress is not on this server for clientAddrInet6 #" + clientAddrInet6);
		return obj;
	}

	public List<SendmailBind> getSendmailBinds() throws IOException, SQLException {
		return table.connector.getSendmailBinds().getSendmailBinds(this);
	}
}
