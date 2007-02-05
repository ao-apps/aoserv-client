package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.AOPool;
import com.aoindustries.profiler.*;
import com.aoindustries.util.StringUtility;
import java.io.*;
import java.util.*;

/**
 * The default client configuration is stored in a properties resource named
 * <code>/com/aoindustries/aoserv/client/aoesrv-client.properties</code>.
 *
 * @see  AOServConnector#getConnector()
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class AOServClientConfiguration {

    private static Properties props;

    private static String getProperty(String name) throws IOException {
        Profiler.startProfile(Profiler.IO, AOServClientConfiguration.class, "getProperty(String)", null);
        try {
            if (props == null) {
                synchronized (AOServClientConfiguration.class) {
		    Properties newProps = new Properties();
                    if (props == null) {
                        InputStream in = new BufferedInputStream(AOServClientConfiguration.class.getResourceAsStream("aoserv-client.properties"));
                        try {
                            newProps.load(in);
                        } finally {
                            in.close();
                        }
                        props = newProps;
                    }
                }
            }
            return props.getProperty(name);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Gets the list of protocols in preferred order.
     */
    static List<String> getProtocols() throws IOException {
        return StringUtility.splitStringCommaSpace(getProperty("aoserv.client.protocols"));
    }
    
    /**
     * Gets the non-SSL hostname.
     */
    static String getTcpHostname() throws IOException {
        return getProperty("aoserv.client.tcp.hostname");
    }
    
    /**
     * Gets the non-SSL local IP to connect from or <code>null</code> if not configured.
     */
    static String getTcpLocalIp() throws IOException {
        String S = getProperty("aoserv.client.tcp.local_ip");
        if(
            S==null
            || (S=S.trim()).length()==0
        ) return null;
        return S;
    }
    
    /**
     * Gets the non-SSL port.
     */
    static int getTcpPort() throws IOException {
        return Integer.parseInt(getProperty("aoserv.client.tcp.port"));
    }

    /**
     * Gets the non-SSL pool size.
     */
    static int getTcpConnectionPoolSize() throws IOException {
        return Integer.parseInt(getProperty("aoserv.client.tcp.connection.pool.size"));
    }
    
    /**
     * Gets the non-SSL connection max age in milliseconds.
     */
    static long getTcpConnectionMaxAge() throws IOException {
        String S = getProperty("aoserv.client.tcp.connection.max_age");
        return S==null || S.length()==0 ? AOPool.DEFAULT_MAX_CONNECTION_AGE : Long.parseLong(S);
    }

    /**
     * Gets the SSL hostname to connect to.
     */
    static String getSslHostname() throws IOException {
        return getProperty("aoserv.client.ssl.hostname");
    }
    
    /**
     * Gets the SSL local IP to connect from or <code>null</code> if not configured.
     */
    static String getSslLocalIp() throws IOException {
        String S = getProperty("aoserv.client.ssl.local_ip");
        if(
            S==null
            || (S=S.trim()).length()==0
        ) return null;
        return S;
    }
    
    /**
     * Gets the SSL port to connect to.
     */
    static int getSslPort() throws IOException {
        return Integer.parseInt(getProperty("aoserv.client.ssl.port"));
    }
    
    /**
     * Gets the SSL connection pool size.
     */
    static int getSslConnectionPoolSize() throws IOException {
        return Integer.parseInt(getProperty("aoserv.client.ssl.connection.pool.size"));
    }
    
    /**
     * Gets the SSL connection max age in milliseconds.
     */
    static long getSslConnectionMaxAge() throws IOException {
        String S = getProperty("aoserv.client.ssl.connection.max_age");
        return S==null || S.length()==0 ? AOPool.DEFAULT_MAX_CONNECTION_AGE : Long.parseLong(S);
    }
    
    /**
     * Gets the optional SSL truststore path.
     *
     * For use by aoserv-daemon and aoserv-backup only.
     */
    public static String getSslTruststorePath() throws IOException {
        return getProperty("aoserv.client.ssl.truststore.path");
    }
    
    /**
     * Gets the optional SSL truststore password.
     *
     * For use by aoserv-daemon and aoserv-backup only.
     */
    public static String getSslTruststorePassword() throws IOException {
        return getProperty("aoserv.client.ssl.truststore.password");
    }
    
    /**
     * Gets the optional default username.
     */
    public static String getUsername() throws IOException {
        return getProperty("aoserv.client.username");
    }
    
    /**
     * Gets the optional default password.
     */
    public static String getPassword() throws IOException {
        return getProperty("aoserv.client.password");
    }

    /**
     * Gets the hostname of this daemon for daemon-specific locking.  Leave
     * this blank for non-AOServDaemon connections.
     */
    static String getDaemonServer() throws IOException {
        return getProperty("aoserv.client.daemon.server");
    }
}
