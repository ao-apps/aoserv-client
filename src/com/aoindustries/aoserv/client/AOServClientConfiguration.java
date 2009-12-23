package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.cache.CachedConnectorFactory;
import com.aoindustries.aoserv.client.rmi.RmiConnectorFactory;
import com.aoindustries.security.LoginException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;

/**
 * The default client configuration is stored in a properties resource named
 * <code>/com/aoindustries/aoserv/client/aoesrv-client.properties</code>.
 *
 * @see  AOServConnector#getConnector()
 *
 * @author  AO Industries, Inc.
 */
final public class AOServClientConfiguration {

    private AOServClientConfiguration() {
    }

    private static final Object propsLock = new Object();
    private static Properties props;

    private static String getProperty(String name) throws IOException {
        synchronized (propsLock) {
            Properties newProps = new Properties();
            if (props == null) {
                InputStream in = AOServClientConfiguration.class.getResourceAsStream("aoserv-client.properties");
                if(in==null) throw new IOException("Unable to find configuration: aoserv-client.properties");
                try {
                    in = new BufferedInputStream(in);
                    newProps.load(in);
                } finally {
                    in.close();
                }
                props = newProps;
            }
            return props.getProperty(name);
        }
    }

    /**
     * Gets the server hostname.
     */
    static String getHostname() throws IOException {
        return getProperty("aoserv.client.hostname");
    }

    /**
     * Gets the local IP to connect from or <code>null</code> if not configured.
     */
    static String getLocalIp() throws IOException {
        String S = getProperty("aoserv.client.local_ip");
        if(
            S==null
            || (S=S.trim()).length()==0
        ) return null;
        return S;
    }
    
    /**
     * Gets the server port.
     */
    static int getPort() throws IOException {
        return Integer.parseInt(getProperty("aoserv.client.port"));
    }

    /**
     * Gets the useSsl flag.  Defaults to true.
     */
    static boolean getUseSsl() throws IOException {
        return !"false".equals(getProperty("aoserv.client.useSsl"));
    }
    
    /**
     * Gets the optional SSL truststore path.
     */
    static String getTrustStorePath() throws IOException {
        return getProperty("aoserv.client.truststore.path");
    }
    
    /**
     * Gets the optional SSL truststore password.
     */
    static String getTrustStorePassword() throws IOException {
        return getProperty("aoserv.client.truststore.password");
    }

    /**
     * Gets the use cache flag.  Defaults to true.
     */
    static boolean getUseCache() throws IOException {
        return !"false".equals(getProperty("aoserv.client.useCache"));
    }

    /**
     * Gets the optional default username.
     */
    static String getUsername() throws IOException {
        return getProperty("aoserv.client.username");
    }
    
    /**
     * Gets the optional default password.
     */
    static String getPassword() throws IOException {
        return getProperty("aoserv.client.password");
    }

    /**
     * Gets the hostname of this daemon for daemon-specific locking.  Leave
     * this blank for non-AOServDaemon connections.
     */
    static String getDaemonServer() throws IOException {
        return getProperty("aoserv.client.daemon.server");
    }

    private static final Object factoryLock = new Object();
    private static AOServConnectorFactory<?,?> factory;

    /**
     * Gets the AOServConnector that is configured in the aoserv-client.properties file.
     */
    public static AOServConnectorFactory<?,?> getAOServConnectorFactory() throws IOException {
        synchronized(factoryLock) {
            if(factory==null) {
                String trustStorePath = getTrustStorePath();
                if(trustStorePath!=null && trustStorePath.length()>0) System.setProperty("javax.net.ssl.trustStore", trustStorePath);
                String trustStorePassword = getTrustStorePassword();
                if(trustStorePassword!=null && trustStorePassword.length()>0) System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
                AOServConnectorFactory<?,?> newFactory = new RmiConnectorFactory(
                    getHostname(),
                    getPort(),
                    getLocalIp(),
                    getUseSsl()
                );
                if(getUseCache()) newFactory = new CachedConnectorFactory(newFactory);
                factory = newFactory;
            }
            return factory;
        }
    }

    /**
     * Gets the default <code>AOServConnector</code> as defined in the
     * <code>com/aoindustries/aoserv/client/aoserv-client.properties</code>
     * resource, in the system default locale.
     */
    public static AOServConnector<?,?> getConnector() throws IOException, RemoteException, LoginException {
        String username = getUsername();
        String daemonServer = getDaemonServer();
        if(daemonServer==null || daemonServer.length()==0) daemonServer=null;
        return getAOServConnectorFactory().getConnector(
            UUID.randomUUID(),
            Locale.getDefault(),
            username,
            username,
            getPassword(),
            daemonServer
        );
    }

    /**
     * Gets the <code>AOServConnector</code> with the provided authentication
     * information.  The <code>com/aoindustries/aoserv/client/aoserv-client.properties</code>
     * resource determines the connection parameters.  Uses the default locale.
     *
     * @param  username  the username to connect as
     * @param  password  the password to connect with
     *
     * @return  the first <code>AOServConnector</code> to successfully connect
     *          to the server
     *
     * @exception  IOException  if no connection can be established
     */
    public static AOServConnector<?,?> getConnector(String username, String password) throws IOException, RemoteException, LoginException {
        return getAOServConnectorFactory().getConnector(
            UUID.randomUUID(),
            Locale.getDefault(),
            username,
            username,
            password,
            null
        );
    }
}
