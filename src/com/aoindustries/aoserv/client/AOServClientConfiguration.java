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
import com.aoindustries.aoserv.client.timeout.TimeoutConnectorFactory;
import com.aoindustries.aoserv.client.validator.DomainName;
import com.aoindustries.aoserv.client.validator.Hostname;
import com.aoindustries.aoserv.client.validator.InetAddress;
import com.aoindustries.aoserv.client.validator.NetPort;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.security.LoginException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * The default client configuration is stored in a properties resource named
 * <code>/com/aoindustries/aoserv/client/aoesrv-client.properties</code>.
 *
 * @see  #getConnector()
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
                if(in==null) throw new IOException(ApplicationResources.accessor.getMessage(Locale.getDefault(), "AOServClientConfiguration.unableToFindConfiguration", "aoserv-client.properties"));
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
    public static String getTrustStorePath() throws IOException {
        String s = getProperty("aoserv.client.truststore.path");
        return s==null || s.length()==0 ? null : s;
    }

    /**
     * Gets the optional SSL truststore password.
     */
    public static String getTrustStorePassword() throws IOException {
        String s = getProperty("aoserv.client.truststore.password");
        return s==null || s.length()==0 ? null : s;
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
    static Hostname getRmiHostname() throws IOException, ValidationException {
        return Hostname.valueOf(getProperty("aoserv.client.rmi.hostname"));
    }

    /**
     * Gets the local IP to connect from or <code>null</code> if not configured.
     */
    static InetAddress getRmiLocalIp() throws IOException, ValidationException {
        String S = getProperty("aoserv.client.rmi.local_ip");
        if(
            S==null
            || (S=S.trim()).length()==0
        ) return null;
        return InetAddress.valueOf(S);
    }
    
    /**
     * Gets the server port.
     */
    static NetPort getRmiPort() throws IOException, ValidationException {
        return NetPort.valueOf(Integer.parseInt(getProperty("aoserv.client.rmi.port")));
    }

    /**
     * Gets the useSsl flag.  Defaults to true.
     */
    static boolean getRmiUseSsl() throws IOException {
        return !"false".equals(getProperty("aoserv.client.rmi.useSsl"));
    }
    
    /**
     * Gets the soap target endpoint.  If empty or missing, will use the default.
     */
    static String getSoapTargetEndpoint() throws IOException {
        return getProperty("aoserv.client.soap.targetEndpoint");
    }

    /**
     * Gets the retry flag.  Defaults to true.
     */
    static boolean getRetry() throws IOException {
        return !"false".equals(getProperty("aoserv.client.retry"));
    }

    /**
     * Gets the timeout.  Defaults to zero (off).
     */
    static long getTimeout() throws IOException {
        String timeout = getProperty("aoserv.client.timeout");
        return timeout==null || timeout.length()==0 ? 0 : Long.parseLong(timeout);
    }

    /**
     * Gets the timeout unit.  Defaults to SECONDS.
     */
    static TimeUnit getTimeoutUnit() throws IOException {
        String unit = getProperty("aoserv.client.timeout.unit");
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
    static UserId getUsername() throws IOException, ValidationException {
        String id = getProperty("aoserv.client.username");
        if(id==null || id.length()==0) return null;
        return UserId.valueOf(id);
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
    static UserId getSwitchUser() throws IOException, ValidationException {
        String id = getProperty("aoserv.client.switchUser");
        if(id==null || id.length()==0) return null;
        return UserId.valueOf(id);
    }

    /**
     * Gets the hostname of this daemon for daemon-specific locking.  Leave
     * this blank for non-AOServDaemon connections.
     */
    static DomainName getDaemonServer() throws IOException, ValidationException {
        String domain = getProperty("aoserv.client.daemon.server");
        if(domain==null || domain.length()==0) return null;
        return DomainName.valueOf(domain);
    }

    /**
     * Gets the read-only flag.  If empty or missing will be read-write.
     */
    static boolean getReadOnly() throws IOException {
        String readOnly = getProperty("aoserv.client.readOnly");
        if(readOnly==null || readOnly.length()==0) return false;
        return Boolean.valueOf(readOnly);
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
                    String protocol = getProtocol();
                    AOServConnectorFactory<?,?> newFactory;
                    if(protocol.equalsIgnoreCase(Protocol.RMI)) {
                        String trustStorePath = getTrustStorePath();
                        if(trustStorePath!=null) System.setProperty("javax.net.ssl.trustStore", trustStorePath);
                        String trustStorePassword = getTrustStorePassword();
                        if(trustStorePassword!=null) System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
                        try {
                            InetAddress localIp = getRmiLocalIp();
                            newFactory = new RmiClientConnectorFactory(
                                getRmiHostname().toString(),
                                getRmiPort().getPort(),
                                localIp==null ? null : localIp.getAddress(),
                                getRmiUseSsl()
                            );
                        } catch(ValidationException err) {
                            throw new RemoteException(err.getLocalizedMessage(Locale.getDefault()), err);
                        }
                    } else if(protocol.equalsIgnoreCase(Protocol.SOAP)) {
                        // Load through reflection to avoid dependency relationship
                        final String classname = "com.aoindustries.aoserv.client.ws.WsConnectorFactory";
                        try {
                            Class<? extends AOServConnectorFactory> clazz = Class.forName(classname).asSubclass(AOServConnectorFactory.class);
                            String targetEndpoint = getSoapTargetEndpoint();
                            if(targetEndpoint==null || targetEndpoint.length()==0) {
                                // Default constructor
                                newFactory = clazz.getConstructor().newInstance();
                            } else {
                                newFactory = clazz.getConstructor(String.class).newInstance(targetEndpoint);
                            }
                        } catch(ClassNotFoundException err) {
                            throw new RemoteException(ApplicationResources.accessor.getMessage(Locale.getDefault(), "AOServClientConfiguration.unableToLoad", classname, "aoserv-client-ws.jar"), err);
                        } catch(ClassCastException err) {
                            throw new RemoteException(ApplicationResources.accessor.getMessage(Locale.getDefault(), "AOServClientConfiguration.unableToLoad", classname, "aoserv-client-ws.jar"), err);
                        } catch(NoSuchMethodException err) {
                            throw new RemoteException(ApplicationResources.accessor.getMessage(Locale.getDefault(), "AOServClientConfiguration.unableToLoad", classname, "aoserv-client-ws.jar"), err);
                        } catch(InstantiationException err) {
                            throw new RemoteException(ApplicationResources.accessor.getMessage(Locale.getDefault(), "AOServClientConfiguration.unableToLoad", classname, "aoserv-client-ws.jar"), err);
                        } catch(IllegalAccessException err) {
                            throw new RemoteException(ApplicationResources.accessor.getMessage(Locale.getDefault(), "AOServClientConfiguration.unableToLoad", classname, "aoserv-client-ws.jar"), err);
                        } catch(InvocationTargetException err) {
                            throw new RemoteException(ApplicationResources.accessor.getMessage(Locale.getDefault(), "AOServClientConfiguration.unableToLoad", classname, "aoserv-client-ws.jar"), err);
                        }
                    } else {
                        throw new RemoteException(ApplicationResources.accessor.getMessage(Locale.getDefault(), "AOServClientConfiguration.unsupportedProtocol", protocol));
                    }
                    long timeout = getTimeout();
                    if(timeout>0) newFactory = new TimeoutConnectorFactory(newFactory, timeout, getTimeoutUnit());
                    if(getRetry()) newFactory = new RetryConnectorFactory(newFactory);
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
            UserId username = getUsername();
            String password = getPassword();
            UserId switchUser = getSwitchUser();
            if(switchUser==null) switchUser = username;
            DomainName daemonServer = getDaemonServer();
            boolean readOnly = getReadOnly();
            return getConnector(Locale.getDefault(), username, password, switchUser, daemonServer, readOnly);
        } catch(IOException err) {
            throw new RemoteException(err.getMessage(), err);
        } catch(ValidationException err) {
            throw new LoginException(err.getLocalizedMessage(Locale.getDefault()), err);
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
    public static AOServConnector<?,?> getConnector(UserId username, String password, boolean readOnly) throws RemoteException, LoginException {
        return getConnector(Locale.getDefault(), username, password, username, null, readOnly);
    }

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
    public static AOServConnector<?,?> getConnector(Locale locale, UserId username, String password, UserId switchUser, DomainName daemonServer, boolean readOnly) throws RemoteException, LoginException {
        return getAOServConnectorFactory().getConnector(
            locale,
            switchUser,
            username,
            password,
            daemonServer,
            readOnly
        );
    }
}
