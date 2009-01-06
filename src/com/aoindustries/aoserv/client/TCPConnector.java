package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import javax.swing.SwingUtilities;

/**
 * A <code>TCPConnector</code> provides the connection between
 * the object layer and the data over a pool of un-secured sockets.
 *
 * @see  SocketConnection
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public class TCPConnector extends AOServConnector {

    private static final int MAX_IDLE_LISTEN_CACHES=15*60*1000;
    
    /** Avoid repeated copies using static final int. */
    private static final int numTables = SchemaTable.TableID.values().length;

    class CacheMonitor extends Thread {

        public CacheMonitor() {
            super("TCPConnector - CacheMonitor");
            setDaemon(true);
            start();
        }

        @Override
        public void run() {
            try {
                TCPConnector.this.testConnect();
                boolean runMore=true;
                while(runMore) {
                    try {
                        AOServConnection conn=getConnection();
                        try {
                            CompressedDataOutputStream out=conn.getOutputStream();
                            out.writeCompressedInt(AOServProtocol.CommandID.LISTEN_CACHES.ordinal());
                            out.flush();

                            CompressedDataInputStream in=conn.getInputStream();
                            IntList tableList=new IntArrayList();
                            while(true) {
                                synchronized(cacheMonitorLock) {
                                    long currentTime=System.currentTimeMillis();
                                    long timeSince=currentTime-connectionLastUsed;
                                    if(timeSince<0) connectionLastUsed=currentTime;
                                    else if(timeSince>=MAX_IDLE_LISTEN_CACHES) {
                                        // Must also not have any invalidate listeners
                                        boolean foundListener=false;
                                        if(tables!=null) {
                                            for(int c=0;c<numTables;c++) {
                                                AOServTable table=tables.get(c);
                                                if(table!=null) {
                                                    List listeners=table.tableListeners;
                                                    if(listeners!=null && listeners.size()>0) {
                                                        foundListener=true;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                        if(foundListener) connectionLastUsed=currentTime;
                                        else {
                                            runMore=false;
                                            break;
                                        }
                                    }
                                }
                                tableList.clear();
                                int size=in.readCompressedInt();
                                if(size!=-1) {
                                    for(int c=0;c<size;c++) {
                                        int tableID=in.readCompressedInt();
                                        tableList.add(tableID);
                                    }
                                }
                                // No tables listed for "ping"
                                if(!tableList.isEmpty()) tablesUpdated(tableList);
                                out.writeBoolean(true);
                                out.flush();
                            }
                        } finally {
                            conn.close();
                            releaseConnection(conn);
                        }
                    } catch(IOException err) {
                        errorHandler.reportError(err, null);
                        try {
                            sleep(60000);
                        } catch(InterruptedException err2) {
                            errorHandler.reportWarning(err2, null);
                        }
                    } finally {
                        clearCaches();
                    }
                }
            } catch(RuntimeException err) {
                errorHandler.reportError(err, null);
            } finally {
                synchronized(cacheMonitorLock) {
                    if(cacheMonitor==this) cacheMonitor=null;
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
    private static final List<TCPConnector> connectors=new ArrayList<TCPConnector>();

    /**
     * The maximum size of the connection pool.
     */
    final int poolSize;
    final long maxConnectionAge;

    private Object cacheMonitorLock=new Object();
    private long connectionLastUsed;
    private CacheMonitor cacheMonitor;

    protected TCPConnector(
	String hostname,
        String local_ip,
	int port,
	String connectAs,
	String authenticateAs,
	String password,
        String daemonServer,
	int poolSize,
        long maxConnectionAge,
        ErrorHandler errorHandler
    ) throws IOException {
	super(hostname, local_ip, port, connectAs, authenticateAs, password, daemonServer, errorHandler);
	this.poolSize=poolSize;
        this.maxConnectionAge=maxConnectionAge;
	this.pool=new SocketConnectionPool(this, errorHandler);
    }

    private void startCacheMonitor() {
        synchronized(cacheMonitorLock) {
            connectionLastUsed=System.currentTimeMillis();
            if(cacheMonitor==null) cacheMonitor=new CacheMonitor();
        }
    }

    final AOServConnection getConnection(int maxConnections) throws IOException {
        if(SwingUtilities.isEventDispatchThread()) {
            errorHandler.reportWarning(
                new RuntimeException(ApplicationResourcesAccessor.getMessage(Locale.getDefault(), "TCPConnector.getConnection.isEventDispatchThread")),
                null
            );
        }
        startCacheMonitor();
	return pool.getConnection(maxConnections);
    }

    public String getProtocol() {
	return PROTOCOL;
    }

    Socket getSocket() throws IOException {
        Socket socket=new Socket();
        socket.setKeepAlive(true);
        socket.setSoLinger(true, AOPool.DEFAULT_SOCKET_SO_LINGER);
        //socket.setTcpNoDelay(true);
        if(local_ip!=null) socket.bind(new InetSocketAddress(local_ip, 0));
        socket.connect(new InetSocketAddress(hostname, port), AOPool.DEFAULT_CONNECT_TIMEOUT);
        return socket;
    }

    public static synchronized TCPConnector getTCPConnector(
	String hostname,
        String local_ip,
	int port,
	String connectAs,
	String authenticateAs,
	String password,
        String daemonServer,
	int poolSize,
        long maxConnectionAge,
        ErrorHandler errorHandler
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
                && StringUtility.equals(local_ip, connector.local_ip)
                && connector.port==port
                && connector.connectAs.equals(connectAs)
                && connector.authenticateAs.equals(authenticateAs)
                && connector.password.equals(password)
                && StringUtility.equals(daemonServer, connector.daemonServer)
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
            errorHandler
	);
	connectors.add(newConnector);
	return newConnector;
    }

    public boolean isSecure() {
        try {
            byte[] address=InetAddress.getByName(hostname).getAddress();
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
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    final public void printConnectionStatsHTML(ChainWriter out) throws IOException {
	pool.printConnectionStats(out);
    }

    final void releaseConnection(AOServConnection connection) throws IOException {
	pool.releaseConnection((SocketConnection)connection);
    }

    public AOServConnector switchUsers(String username) throws IOException {
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
            errorHandler
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
