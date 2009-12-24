package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.cache.CachedConnectorFactory;
import com.aoindustries.aoserv.client.noswing.NoSwingConnectorFactory;
import com.aoindustries.aoserv.client.retry.RetryConnectorFactory;
import com.aoindustries.aoserv.client.rmi.RmiClientConnectorFactory;
import com.aoindustries.security.LoginException;
import com.aoindustries.util.StringUtility;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

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
     * Gets the protocol.
     */
    static String getProtocol() throws IOException {
        return getProperty("aoserv.client.protocol");
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
     * Gets the retry flag.  Defaults to true.
     */
    static boolean getRetry() throws IOException {
        return !"false".equals(getProperty("aoserv.client.retry"));
    }

    /**
     * Gets the retry timeout.  Defaults to true.
     */
    static long getRetryTimeout() throws IOException {
        String timeout = getProperty("aoserv.client.retry.timeout");
        return timeout==null || timeout.length()==0 ? 0 : Long.parseLong(timeout);
    }

    /**
     * Gets the retry timeout unit.  Defaults to true.
     */
    static TimeUnit getRetryTimeoutUnit() throws IOException {
        String unit = getProperty("aoserv.client.retry.timeout.unit");
        return unit==null || unit.length()==0 ? TimeUnit.SECONDS : TimeUnit.valueOf(unit);
    }

    /**
     * Gets the use cache flag.  Defaults to true.
     */
    static boolean getUseCache() throws IOException {
        return !"false".equals(getProperty("aoserv.client.useCache"));
    }

    /**
     * Gets the no swing flag.  Defaults to false.
     */
    static boolean getNoSwing() throws IOException {
        return "true".equals(getProperty("aoserv.client.noSwing"));
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
     * Gets the switch user setting.
     */
    static String getSwitchUser() throws IOException {
        return getProperty("aoserv.client.switchUser");
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
     * Gets the AOServConnectorFactory that is configured in the aoserv-client.properties file.
     * Also initializes the truststore path and password if provided in the properties file.
     */
    public static AOServConnectorFactory<?,?> getAOServConnectorFactory() throws RemoteException {
        synchronized(factoryLock) {
            if(factory==null) {
                try {
                    String trustStorePath = getTrustStorePath();
                    if(trustStorePath!=null && trustStorePath.length()>0) System.setProperty("javax.net.ssl.trustStore", trustStorePath);
                    String trustStorePassword = getTrustStorePassword();
                    if(trustStorePassword!=null && trustStorePassword.length()>0) System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
                    AOServConnectorFactory<?,?> newFactory = new RmiClientConnectorFactory(
                        getHostname(),
                        getPort(),
                        getLocalIp(),
                        getUseSsl()
                    );
                    if(getRetry()) newFactory = new RetryConnectorFactory(getRetryTimeout(), getRetryTimeoutUnit(), newFactory);
                    if(getUseCache()) newFactory = new CachedConnectorFactory(newFactory);
                    if(getNoSwing()) newFactory = new NoSwingConnectorFactory(newFactory);
                    factory = newFactory;
                } catch(IOException err) {
                    throw new RemoteException(err.getMessage(), err);
                }
            }
            return factory;
        }
    }

    /**
     * Gets the default <code>AOServConnector</code> as defined in the
     * <code>com/aoindustries/aoserv/client/aoserv-client.properties</code>
     * resource, in the system default locale.  Only one instance will
     * be created and its locale will be reset back to the default locale.
     */
    public static AOServConnector<?,?> getConnector() throws RemoteException, LoginException {
        try {
            String username = getUsername();
            String password = getPassword();
            String switchUser = getSwitchUser();
            if(switchUser==null || switchUser.length()==0) switchUser = username;
            String daemonServer = getDaemonServer();
            if(daemonServer!=null && daemonServer.length()==0) daemonServer=null;
            return getConnector(Locale.getDefault(), username, password, switchUser, daemonServer);
        } catch(IOException err) {
            throw new RemoteException(err.getMessage(), err);
        }
    }

    /**
     * Gets the <code>AOServConnector</code> with the provided authentication
     * information.  The <code>com/aoindustries/aoserv/client/aoserv-client.properties</code>
     * resource determines the connection parameters.  Uses the default locale.
     * Only one instance will be created for each username/password pair and its
     * locale will be reset back to the default locale.
     *
     * @param  username  the username to connect as
     * @param  password  the password to connect with
     */
    public static AOServConnector<?,?> getConnector(String username, String password) throws RemoteException, LoginException {
        return getConnector(Locale.getDefault(), username, password, username, null);
    }

    static class CacheKey {

        final String username;
        final String password;
        final String switchUser;
        final String daemonServer;

        CacheKey(
            String username,
            String password,
            String switchUser,
            String daemonServer
        ) {
            this.username = username;
            this.password = password;
            this.switchUser = switchUser;
            this.daemonServer = daemonServer;
        }

        @Override
        public boolean equals(Object o) {
            if(o==null || !(o instanceof CacheKey)) return false;
            CacheKey other = (CacheKey)o;
            return
                username.equals(other.username)
                && password.equals(other.password)
                && switchUser.equals(other.switchUser)
                && StringUtility.equals(daemonServer, other.daemonServer)
            ;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + username.hashCode();
            hash = 29 * hash + password.hashCode();
            hash = 29 * hash + switchUser.hashCode();
            hash = 29 * hash + (daemonServer!=null ? daemonServer.hashCode() : 0);
            return hash;
        }
    }

    private static final Map<CacheKey,AOServConnector<?,?>> connectors = new HashMap<CacheKey,AOServConnector<?,?>>();

    /**
     * Gets the <code>AOServConnector</code> with the provided authentication
     * information.  The <code>com/aoindustries/aoserv/client/aoserv-client.properties</code>
     * resource determines the connection parameters.  Uses the default locale.
     * Only one instance will be created for each unique set of authentication parameters
     * and its locale will be reset back to the provided locale.
     *
     * @param  username  the username to connect as
     * @param  password  the password to connect with
     */
    public static AOServConnector<?,?> getConnector(Locale locale, String username, String password, String switchUser, String daemonServer) throws RemoteException, LoginException {
        CacheKey cacheKey = new CacheKey(username, password, switchUser, daemonServer);
        synchronized(connectors) {
            AOServConnector<?,?> connector = connectors.get(cacheKey);
            if(connector!=null) {
                connector.setLocale(locale);
            } else {
                connectors.put(
                    cacheKey,
                    connector = getAOServConnectorFactory().newConnector(
                        locale,
                        switchUser,
                        username,
                        password,
                        daemonServer
                    )
                );
            }
            return connector;
        }
    }
}
