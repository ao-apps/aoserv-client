package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.AOPool;
import com.aoindustries.util.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;
import javax.net.ssl.*;

/**
 * A <code>SSLConnector</code> provides the connection between
 * the client and server over secured SSL sockets.
 *
 * @see  AOServConnector
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public class SSLConnector extends TCPConnector {

    /**
     * Only loads the SSL provider once.
     */
    public final static boolean[] sslProviderLoaded=new boolean[1];

    /**
     * The trust store used for this connector.
     */
    private static String trustStorePath;
    
    /**
     * The password for the trust store.
     */
    private static String trustStorePassword;

    /**
     * The protocol for this connector.
     */
    public static final String PROTOCOL="ssl";

    /**
     * Instances of connectors are created once and then reused.
     */
    private static final List<SSLConnector> connectors=new ArrayList<SSLConnector>();

    protected SSLConnector(
        String hostname,
        int port,
        String connectAs,
        String authenticateAs,
        String password,
        String daemonServer,
        int poolSize,
        long maxConnectionAge,
        String trustStorePath,
        String trustStorePassword,
        ErrorHandler errorHandler
    ) throws IOException {
	super(hostname, port, connectAs, authenticateAs, password, daemonServer, poolSize, maxConnectionAge, errorHandler);
        if(
            (
                SSLConnector.trustStorePath!=null
                && !SSLConnector.trustStorePath.equals(trustStorePath)
            ) || (
                SSLConnector.trustStorePassword!=null
                && !SSLConnector.trustStorePassword.equals(trustStorePassword)
            )
        ) throw new IllegalArgumentException(
            "Trust store path and password may only be set once, currently '"
            + SSLConnector.trustStorePath
            + "', trying to set to '"
            + trustStorePath
            + "'"
        );
        if(SSLConnector.trustStorePath==null) {
            SSLConnector.trustStorePath=trustStorePath;
            SSLConnector.trustStorePassword=trustStorePassword;
        }
    }

    public String getProtocol() {
	return PROTOCOL;
    }

    Socket getSocket() throws IOException {
        synchronized(SSLConnector.class) {
            if(!sslProviderLoaded[0]) {
                if(trustStorePath!=null && trustStorePath.length()>0) {
                    System.setProperty("javax.net.ssl.trustStore", trustStorePath);
                }
                if(trustStorePassword!=null && trustStorePassword.length()>0) {
                    System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
                }
                Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
                sslProviderLoaded[0]=true;
            }
        }

        SSLSocketFactory sslFact=(SSLSocketFactory)SSLSocketFactory.getDefault();
        Socket regSocket = new Socket();
        regSocket.connect(new InetSocketAddress(hostname, port), AOPool.DEFAULT_CONNECT_TIMEOUT);
        regSocket.setKeepAlive(true);
        regSocket.setSoLinger(true, AOPool.DEFAULT_SOCKET_SO_LINGER);
        //regSocket.setTcpNoDelay(true);
        return sslFact.createSocket(regSocket, hostname, port, true);
    }

    public static synchronized SSLConnector getSSLConnector(
	String hostname,
	int port,
	String connectAs,
	String authenticateAs,
	String password,
        String daemonServer,
	int poolSize,
        long maxConnectionAge,
        String trustStorePath,
        String trustStorePassword,
        ErrorHandler errorHandler
    ) throws IOException {
	int size=connectors.size();
	for(int c=0;c<size;c++) {
            SSLConnector connector=connectors.get(c);
            if(
                connector.hostname.equals(hostname)
                && connector.port==port
                && connector.connectAs.equals(connectAs)
                && connector.authenticateAs.equals(authenticateAs)
                && connector.password.equals(password)
                && StringUtility.equals(daemonServer, connector.daemonServer)
                && connector.poolSize==poolSize
                && connector.maxConnectionAge==maxConnectionAge
                && StringUtility.equals(SSLConnector.trustStorePath, trustStorePath)
                && StringUtility.equals(SSLConnector.trustStorePassword, trustStorePassword)
            ) return connector;
	}
	SSLConnector newConnector=new SSLConnector(
            hostname,
            port,
            connectAs,
            authenticateAs,
            password,
            daemonServer,
            poolSize,
            maxConnectionAge,
            trustStorePath,
            trustStorePassword,
            errorHandler
	);
	connectors.add(newConnector);
	return newConnector;
    }

    public boolean isSecure() {
	return true;
    }

    public AOServConnector switchUsers(String username) throws IOException {
	if(username.equals(connectAs)) return this;
	return getSSLConnector(
            hostname,
            port,
            username,
            authenticateAs,
            password,
            daemonServer,
            poolSize,
            maxConnectionAge,
            trustStorePath,
            trustStorePassword,
            errorHandler
	);
    }
}
