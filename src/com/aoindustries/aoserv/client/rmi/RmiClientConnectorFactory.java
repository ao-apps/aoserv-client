/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.rmi;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.aoserv.client.wrapped.*;
import com.aoindustries.rmi.*;
import com.aoindustries.security.LoginException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.util.Locale;

/**
 * An implementation of <code>AOServConnectorFactory</code> that performs a direct RMI connection to the server.
 *
 * @author  AO Industries, Inc.
 */
final public class RmiClientConnectorFactory extends WrappedConnectorFactory<RmiClientConnector,RmiClientConnectorFactory> {


    /**
     * Makes the direct, underlying connections.  The reconnect is performed at the outer layer.
     */
    static final class DirectRmiClientConnectorFactory implements AOServConnectorFactory {

        private final String serverAddress;
        private final int serverPort;
        private final RMIClientSocketFactory csf;

        private final AOServConnectorFactoryCache<AOServConnector> connectors = new AOServConnectorFactoryCache<AOServConnector>();

        DirectRmiClientConnectorFactory(
            String serverAddress,
            int serverPort,
            String localAddress,
            boolean useSsl
        ) {
            this.serverAddress = serverAddress;
            this.serverPort = serverPort;

            // Setup the RMI system properties
            System.setProperty("java.rmi.server.randomIDs", "true");
            System.setProperty("java.rmi.server.useCodebaseOnly", "true");
            System.clearProperty("java.rmi.server.codebase");
            System.setProperty("java.rmi.server.disableHttp", "true");
            // System.setProperty("sun.rmi.server.suppressStackTraces", "true");

            if(useSsl) {
                // SSL
                if(localAddress!=null && localAddress.length()>0) {
                    csf = new RMIClientSocketFactorySSL();
                } else {
                    csf = new RMIClientSocketFactorySSL();
                }
            } else {
                // Non-SSL
                if(localAddress!=null && localAddress.length()>0) {
                    csf = new RMIClientSocketFactoryTCP();
                } else {
                    csf = new RMIClientSocketFactoryTCP();
                }
            }
        }

        @Override
        public AOServConnector getConnector(Locale locale, UserId username, String password, UserId switchUser, DomainName daemonServer, boolean readOnly) throws LoginException, RemoteException {
            synchronized(connectors) {
                AOServConnector connector = connectors.get(locale, username, password, switchUser, daemonServer, readOnly);
                if(connector==null) {
                    try {
                        // Connect to the remote registry and get each of the stubs
                        Registry remoteRegistry = LocateRegistry.getRegistry(serverAddress, serverPort, csf);
                        AOServConnectorFactory serverFactory = (AOServConnectorFactory)remoteRegistry.lookup(AOServConnectorFactory.class.getName()+"_Stub");
                        connector = serverFactory.getConnector(locale, username, password, switchUser, daemonServer, readOnly);
                        connectors.put(
                            locale,
                            username,
                            password,
                            switchUser,
                            daemonServer,
                            readOnly,
                            connector
                        );
                    } catch(NotBoundException err) {
                        throw new RemoteException(err.getMessage(), err);
                    }
                }
                return connector;
            }
        }
    }

    public RmiClientConnectorFactory(
        String serverAddress,
        int serverPort,
        String localAddress,
        boolean useSsl
    ) {
        super(new DirectRmiClientConnectorFactory(serverAddress, serverPort, localAddress, useSsl));
    }

    @Override
    protected RmiClientConnector newWrappedConnector(Locale locale, UserId username, String password, UserId switchUser, DomainName daemonServer, boolean readOnly) throws LoginException, RemoteException {
        return new RmiClientConnector(this, locale, username, password, switchUser, daemonServer, readOnly);
    }
}
