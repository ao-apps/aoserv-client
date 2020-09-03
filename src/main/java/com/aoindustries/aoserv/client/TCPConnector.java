/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017, 2018, 2019, 2020  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.collections.IntArrayList;
import com.aoindustries.collections.IntList;
import com.aoindustries.io.AOPool;
import com.aoindustries.io.stream.StreamableInput;
import com.aoindustries.io.stream.StreamableOutput;
import com.aoindustries.net.DomainName;
import com.aoindustries.net.HostAddress;
import com.aoindustries.net.Port;
import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
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
	 * Close cache monitor after 90 minutes of inactivity,
	 * when there is some sort of non-global-cached data.
	 * <p>
	 * The cache monitor is only shutdown when there are no registered
	 * {@linkplain AOServTable#addTableListener(com.aoindustries.table.TableListener) table listeners}.
	 * </p>
	 */
	private static final long MAX_IDLE_LISTEN_CACHES = 90L * 60 * 1000;

	/**
	 * Close cache monitor after 60 seconds of inactivity,
	 * when there is no non-global-cached data, such as after a call to
	 * {@link #clearCaches()}.
	 * <p>
	 * This helps support a more timely shutdown.
	 * TODO: Expose a "stop" method that will clear all caches, immediately stop
	 * the cache listener, and remove this connector from the pool of connectors?
	 * </p>
	 * <p>
	 * The cache monitor is only shutdown when there are no registered
	 * {@linkplain AOServTable#addTableListener(com.aoindustries.table.TableListener) table listeners}.
	 * </p>
	 */
	// TODO: Use this value, somehow, in a meaningful way.
	// TODO: private static final long MAX_IDLE_LISTEN_CACHES_NOTHING_CACHED = 60L * 1000;

	class CacheMonitor extends Thread {

		CacheMonitor() {
			super("TCPConnector - CacheMonitor");
			setDaemon(true);
		}

		@Override
		@SuppressWarnings({"SleepWhileInLoop", "UseSpecificCatch", "TooBroadCatch"})
		public void run() {
			try {
				//System.err.println("DEBUG: TCPConnector("+connectAs+"-"+getConnectorId()+").CacheMonitor: run: Starting");
				boolean runMore=true;
				while(runMore) {
					try {
						AOServConnection conn=getConnection(1);
						try {
							try {
								//System.err.println("DEBUG: TCPConnector("+connectAs+"-"+getConnectorId()+").CacheMonitor: run: conn.identityHashCode="+System.identityHashCode(conn));
								StreamableOutput out = conn.getRequestOut(AoservProtocol.CommandID.LISTEN_CACHES);
								// TODO: Only listen for caches on tables where there is either something currently cached
								// TODO: (how to handle shared caches of global cached tables?) or where there is
								// TODO: at least one registered table listener.  Otherwise the cache signals are
								// TODO: of no value.
								// TODO:
								// TODO: This would also help a connector shutdown be meaningful after clearCaches()
								// TODO: As-is, the connector may continue to receive incoming cache notifications,
								// TODO: Thus keeping itself active potentially forever.
								out.flush();

								StreamableInput in = conn.getResponseIn();
								IntList tableList=new IntArrayList();
								while(runMore) {
									synchronized(cacheMonitorLock) {
										long currentTime = System.currentTimeMillis();
										long timeSince = currentTime - connectionLastUsed;
										if(timeSince < 0) {
											// System time reset to the past
											connectionLastUsed = currentTime;
										} else if(timeSince >= MAX_IDLE_LISTEN_CACHES) {
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
												connectionLastUsed = currentTime;
											} else {
												runMore = false;
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
							getLogger().log(Level.INFO, null, err);
							try {
								//System.err.println("DEBUG: TCPConnector("+connectAs+"-"+getConnectorId()+").CacheMonitor: run: Sleeping after exception");
								sleep(getFastRandom().nextInt(50000)+10000); // Wait between 10 and 60 seconds
							} catch(InterruptedException err2) {
								getLogger().log(Level.WARNING, null, err2);
							}
						}
					} catch(ThreadDeath td) {
						throw td;
					} catch(Throwable t) {
						if(isImmediateFail(t)) runMore = false;
						else {
							getLogger().log(Level.SEVERE, null, t);
							try {
								//System.err.println("DEBUG: TCPConnector("+connectAs+"-"+getConnectorId()+").CacheMonitor: run: Sleeping after exception");
								sleep(getFastRandom().nextInt(50000)+10000); // Wait between 10 and 60 seconds
							} catch(InterruptedException err2) {
								getLogger().log(Level.WARNING, null, err2);
							}
						}
					} finally {
						//System.err.println("DEBUG: TCPConnector("+connectAs+"-"+getConnectorId()+").CacheMonitor: run: Clearing caches");
						clearCaches();
					}
				}
			} finally {
				//System.err.println("DEBUG: TCPConnector("+connectAs+"-"+getConnectorId()+").CacheMonitor: run: Ending");
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
	public static final String TCP_PROTOCOL = "tcp";

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
		User.Name connectAs,
		User.Name authenticateAs,
		String password,
		DomainName daemonServer,
		int poolSize,
		long maxConnectionAge
	) {
		super(hostname, local_ip, port, connectAs, authenticateAs, password, daemonServer);
		if(port.getProtocol() != com.aoindustries.net.Protocol.TCP) throw new IllegalArgumentException("Only TCP supported: " + port);
		this.poolSize = poolSize;
		this.maxConnectionAge = maxConnectionAge;
		this.pool = new SocketConnectionPool(this, getLogger());
	}

	private void startCacheMonitor() {
		synchronized(cacheMonitorLock) {
			connectionLastUsed = System.currentTimeMillis();
			if(cacheMonitor == null) {
				(cacheMonitor = new CacheMonitor()).start();
			}
		}
	}

	@Override
	protected final AOServConnection getConnection(int maxConnections) throws InterruptedIOException, IOException {
		if(SwingUtilities.isEventDispatchThread()) {
			getLogger().log(Level.WARNING, null, new RuntimeException(accessor.getMessage("TCPConnector.getConnection.isEventDispatchThread")));
		}
		startCacheMonitor();
		SocketConnection conn = pool.getConnection(maxConnections);
		//System.err.println("DEBUG: TCPConnector("+connectAs+"-"+getConnectorId()+"): getConnection("+maxConnections+"): conn.identityHashCode="+System.identityHashCode(conn));
		return conn;
	}

	@Override
	public String getProtocol() {
		return TCP_PROTOCOL;
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
		User.Name connectAs,
		User.Name authenticateAs,
		String password,
		DomainName daemonServer,
		int poolSize,
		long maxConnectionAge
	) {
		if(connectAs==null) throw new IllegalArgumentException("connectAs is null");
		if(authenticateAs==null) throw new IllegalArgumentException("authenticateAs is null");
		if(password==null) throw new IllegalArgumentException("password is null");
		int size=connectors.size();
		for(int c=0;c<size;c++) {
			TCPConnector connector=connectors.get(c);
			if(connector==null) throw new NullPointerException("connector is null");
			if(connector.connectAs==null) throw new NullPointerException("connector.connectAs is null");
			if(connector.authenticateAs==null) throw new NullPointerException("connector.authenticateAs is null");
			if(connector.password==null) throw new NullPointerException("connector.password is null");
			if(
				connector.hostname.equals(hostname)
				&& Objects.equals(local_ip, connector.local_ip)
				&& connector.port==port
				&& connector.connectAs.equals(connectAs)
				&& connector.authenticateAs.equals(authenticateAs)
				&& connector.password.equals(password)
				&& Objects.equals(daemonServer, connector.daemonServer)
				&& connector.poolSize==poolSize
				&& connector.maxConnectionAge==maxConnectionAge
			) return connector;
		}
		TCPConnector newConnector = new TCPConnector(
			hostname,
			local_ip,
			port,
			connectAs,
			authenticateAs,
			password,
			daemonServer,
			poolSize,
			maxConnectionAge
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
	final public void printConnectionStatsHTML(Appendable out, boolean isXhtml) throws IOException {
		pool.printStatisticsHTML(out, isXhtml);
	}

	@Override
	protected final void releaseConnection(AOServConnection conn) throws IOException {
		//System.err.println("DEBUG: TCPConnector("+connectAs+"-"+getConnectorId()+"): releaseConnection("+System.identityHashCode(conn)+"): conn.identityHashCode="+System.identityHashCode(conn));
		pool.releaseConnection((SocketConnection)conn);
	}

	@Override
	public AOServConnector switchUsers(User.Name username) throws IOException {
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
			maxConnectionAge
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
