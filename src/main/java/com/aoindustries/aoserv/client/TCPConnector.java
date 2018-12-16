/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017, 2018  AO Industries, Inc.
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

import static com.aoindustries.aoserv.client.ApplicationResources.accessor;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.io.AOPool;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.lang.ObjectUtils;
import com.aoindustries.net.DomainName;
import com.aoindustries.net.HostAddress;
import com.aoindustries.net.Port;
import com.aoindustries.util.IntArrayList;
import com.aoindustries.util.IntList;
import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 * A <code>TCPConnector</code> provides the connection between
 * the object layer and the data over a pool of un-secured sockets.
 *
 * @see  SocketConnection
 *
 * @author  AO Industries, Inc.
 */
public class TCPConnector extends AOServConnector {

	/**
	 * Close cache monitor after 90 minutes of inactivity.
	 */
	private static final long MAX_IDLE_LISTEN_CACHES = 90L*60*1000;

	class CacheMonitor extends Thread {

		CacheMonitor() {
			super("TCPConnector - CacheMonitor");
			setDaemon(true);
			start();
		}

		@Override
		public void run() {
			try {
				//System.err.println("DEBUG: TCPConnector("+connectAs+"-"+getConnectorID()+").CacheMonitor: run: Starting");
				boolean runMore=true;
				while(runMore) {
					try {
						AOServConnection conn=getConnection(1);
						try {
							try {
								//System.err.println("DEBUG: TCPConnector("+connectAs+"-"+getConnectorID()+").CacheMonitor: run: conn.identityHashCode="+System.identityHashCode(conn));
								CompressedDataOutputStream out = conn.getRequestOut(AoservProtocol.CommandID.LISTEN_CACHES);
								out.flush();

								CompressedDataInputStream in=conn.getResponseIn();
								IntList tableList=new IntArrayList();
								while(runMore) {
									synchronized(cacheMonitorLock) {
										long currentTime=System.currentTimeMillis();
										long timeSince=currentTime-connectionLastUsed;
										if(timeSince<0) connectionLastUsed=currentTime;
										else if(timeSince>=MAX_IDLE_LISTEN_CACHES) {
											// Must also not have any invalidate listeners
											boolean foundListener = false;
											for(AOServTable<?,?> table : getTables()) {
												if(table.hasAnyTableListener()) {
													foundListener = true;
													break;
												}
											}
											if(foundListener) {
												// Don't check again until MAX_IDLE_LISTEN_CACHES milliseconds pass
												connectionLastUsed=currentTime;
											} else {
												runMore=false;
											}
										}
									}
									if(runMore) {
										tableList.clear();
										boolean isSynchronous = in.readBoolean();
										int size = in.readCompressedInt();
										if(size!=-1) {
											for(int c=0;c<size;c++) {
												int tableID=in.readCompressedInt();
												tableList.add(tableID);
											}
										}
										// No tables listed for "ping"
										if(!tableList.isEmpty()) tablesUpdated(tableList);
										if(isSynchronous) {
											out.writeBoolean(true);
											out.flush();
										}
									}
								}
							} finally {
								conn.close();
							}
						} finally {
							releaseConnection(conn);
						}
					} catch(EOFException err) {
						if(isImmediateFail(err)) runMore = false;
						else {
							logger.log(Level.INFO, null, err);
							try {
								//System.err.println("DEBUG: TCPConnector("+connectAs+"-"+getConnectorID()+").CacheMonitor: run: Sleeping after exception");
								sleep(getRandom().nextInt(50000)+10000); // Wait between 10 and 60 seconds
							} catch(InterruptedException err2) {
								logger.log(Level.WARNING, null, err2);
							}
						}
					} catch(ThreadDeath TD) {
						throw TD;
					} catch(Throwable T) {
						if(isImmediateFail(T)) runMore = false;
						else {
							logger.log(Level.SEVERE, null, T);
							try {
								//System.err.println("DEBUG: TCPConnector("+connectAs+"-"+getConnectorID()+").CacheMonitor: run: Sleeping after exception");
								sleep(getRandom().nextInt(50000)+10000); // Wait between 10 and 60 seconds
							} catch(InterruptedException err2) {
								logger.log(Level.WARNING, null, err2);
							}
						}
					} finally {
						//System.err.println("DEBUG: TCPConnector("+connectAs+"-"+getConnectorID()+").CacheMonitor: run: Clearing caches");
						clearCaches();
					}
				}
			} finally {
				//System.err.println("DEBUG: TCPConnector("+connectAs+"-"+getConnectorID()+").CacheMonitor: run: Ending");
				synchronized(cacheMonitorLock) {
					if(cacheMonitor==this) {
						cacheMonitor=null;
						clearCaches();
					}
				}
			}
		}
	}

	/**
	 * The protocol of this type of connector.
	 */
	public static final String PROTOCOL="tcp";

	/**
	 * The connections to the server are pooled.
	 */
	private final SocketConnectionPool pool;

	/**
	 * Instances of connectors are created once and then reused.
	 */
	private static final List<TCPConnector> connectors=new ArrayList<>();

	/**
	 * The maximum size of the connection pool.
	 */
	final int poolSize;
	final long maxConnectionAge;

	private static class CacheMonitorLock {}
	final private CacheMonitorLock cacheMonitorLock=new CacheMonitorLock();
	private long connectionLastUsed;
	private CacheMonitor cacheMonitor;

	protected TCPConnector(
		HostAddress hostname,
		com.aoindustries.net.InetAddress local_ip,
		Port port,
		UserId connectAs,
		UserId authenticateAs,
		String password,
		DomainName daemonServer,
		int poolSize,
		long maxConnectionAge,
		Logger logger
	) throws IOException {
		super(hostname, local_ip, port, connectAs, authenticateAs, password, daemonServer, logger);
		if(port.getProtocol() != com.aoindustries.net.Protocol.TCP) throw new IllegalArgumentException("Only TCP supported: " + port);
		this.poolSize=poolSize;
		this.maxConnectionAge=maxConnectionAge;
		this.pool=new SocketConnectionPool(this, logger);
	}

	private void startCacheMonitor() {
		synchronized(cacheMonitorLock) {
			connectionLastUsed=System.currentTimeMillis();
			if(cacheMonitor==null) cacheMonitor=new CacheMonitor();
		}
	}

	@Override
	protected final AOServConnection getConnection(int maxConnections) throws InterruptedIOException, IOException {
		if(SwingUtilities.isEventDispatchThread()) {
			logger.log(Level.WARNING, null, new RuntimeException(accessor.getMessage("TCPConnector.getConnection.isEventDispatchThread")));
		}
		startCacheMonitor();
		SocketConnection conn = pool.getConnection(maxConnections);
		//System.err.println("DEBUG: TCPConnector("+connectAs+"-"+getConnectorID()+"): getConnection("+maxConnections+"): conn.identityHashCode="+System.identityHashCode(conn));
		return conn;
	}

	@Override
	public String getProtocol() {
		return PROTOCOL;
	}

	Socket getSocket() throws InterruptedIOException, IOException {
		if(Thread.interrupted()) throw new InterruptedIOException();
		Socket socket=new Socket();
		socket.setKeepAlive(true);
		socket.setSoLinger(true, AOPool.DEFAULT_SOCKET_SO_LINGER);
		socket.setTcpNoDelay(true);
		if(local_ip != null && !local_ip.isUnspecified()) socket.bind(new InetSocketAddress(local_ip.toString(), 0));
		socket.connect(new InetSocketAddress(hostname.toString(), port.getPort()), AOPool.DEFAULT_CONNECT_TIMEOUT);
		return socket;
	}

	public static synchronized TCPConnector getTCPConnector(
		HostAddress hostname,
		com.aoindustries.net.InetAddress local_ip,
		Port port,
		UserId connectAs,
		UserId authenticateAs,
		String password,
		DomainName daemonServer,
		int poolSize,
		long maxConnectionAge,
		Logger logger
	) throws IOException {
		if(connectAs==null) throw new NullPointerException("connectAs is null");
		if(authenticateAs==null) throw new NullPointerException("authenticateAs is null");
		if(password==null) throw new NullPointerException("password is null");
		int size=connectors.size();
		for(int c=0;c<size;c++) {
			TCPConnector connector=connectors.get(c);
			if(connector==null) throw new NullPointerException("connector is null");
			if(connector.connectAs==null) throw new NullPointerException("connector.connectAs is null");
			if(connector.authenticateAs==null) throw new NullPointerException("connector.authenticateAs is null");
			if(connector.password==null) throw new NullPointerException("connector.password is null");
			if(
				connector.hostname.equals(hostname)
				&& ObjectUtils.equals(local_ip, connector.local_ip)
				&& connector.port==port
				&& connector.connectAs.equals(connectAs)
				&& connector.authenticateAs.equals(authenticateAs)
				&& connector.password.equals(password)
				&& ObjectUtils.equals(daemonServer, connector.daemonServer)
				&& connector.poolSize==poolSize
				&& connector.maxConnectionAge==maxConnectionAge
			) return connector;
		}
		TCPConnector newConnector=new TCPConnector(
			hostname,
			local_ip,
			port,
			connectAs,
			authenticateAs,
			password,
			daemonServer,
			poolSize,
			maxConnectionAge,
			logger
		);
		connectors.add(newConnector);
		return newConnector;
	}

	@Override
	public boolean isSecure() throws UnknownHostException, IOException {
		byte[] address=InetAddress.getByName(hostname.toString()).getAddress();
		if(
			address[0]==(byte)127
			|| address[0]==(byte)10
			|| (
				address[0]==(byte)192
				&& address[1]==(byte)168
			)
		) return true;
		// Allow same class C subnet as this host
		SocketConnection conn=(SocketConnection)getConnection(1);
		try {
			InetAddress ia=conn.getLocalInetAddress();
			byte[] localAddress=ia.getAddress();
			return
				address[0]==localAddress[0]
				&& address[1]==localAddress[1]
				&& address[2]==localAddress[2]
			;
		} catch(IOException err) {
			conn.close();
			throw err;
		} finally {
			releaseConnection(conn);
		}
	}

	@Override
	final public void printConnectionStatsHTML(Appendable out) throws IOException {
		pool.printConnectionStats(out);
	}

	@Override
	protected final void releaseConnection(AOServConnection conn) throws IOException {
		//System.err.println("DEBUG: TCPConnector("+connectAs+"-"+getConnectorID()+"): releaseConnection("+System.identityHashCode(conn)+"): conn.identityHashCode="+System.identityHashCode(conn));
		pool.releaseConnection((SocketConnection)conn);
	}

	@Override
	public AOServConnector switchUsers(UserId username) throws IOException {
		if(username.equals(connectAs)) return this;
		return getTCPConnector(
			hostname,
			local_ip,
			port,
			username,
			authenticateAs,
			password,
			daemonServer,
			poolSize,
			maxConnectionAge,
			logger
		);
	}

	/**
	 * Start the CacheMonitor when a new table listener is added.
	 */
	@Override
	void addingTableListener() {
		startCacheMonitor();
	}
}
