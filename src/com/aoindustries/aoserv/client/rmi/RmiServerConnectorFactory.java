package com.aoindustries.aoserv.client.rmi;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServConnectorFactory;
import com.aoindustries.aoserv.client.AOServConnectorFactoryCache;
import com.aoindustries.aoserv.client.AOServService;
import com.aoindustries.rmi.RMIClientSocketFactorySSL;
import com.aoindustries.rmi.RMIClientSocketFactoryTCP;
import com.aoindustries.rmi.RMIServerSocketFactorySSL;
import com.aoindustries.rmi.RMIServerSocketFactoryTCP;
import com.aoindustries.rmi.RegistryManager;
import com.aoindustries.security.LoginException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.Locale;

/**
 * An implementation of <code>AOServConnectorFactory</code> that exports the wrapped
 * connector and its services for RMI.  This allows the decoupling of processes to
 * different Java virtual machines and different physical servers.
 *
 * @author  AO Industries, Inc.
 */
final public class RmiServerConnectorFactory<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> implements AOServConnectorFactory<C,F> {

    private final int port;
    private final RMIClientSocketFactory csf;
    private final RMIServerSocketFactory ssf;
    private final AOServConnectorFactory<C,F> wrapped;

    public RmiServerConnectorFactory(
        String publicAddress,
        String listenAddress,
        int port,
        boolean useSsl,
        AOServConnectorFactory<C,F> wrapped
    ) throws RemoteException {
        // Setup the RMI system properties
        if(publicAddress!=null && publicAddress.length()>0) {
            System.setProperty("java.rmi.server.hostname", publicAddress);
        } else if(listenAddress!=null && listenAddress.length()>0) {
            System.setProperty("java.rmi.server.hostname", listenAddress);
        } else {
            System.clearProperty("java.rmi.server.hostname");
        }
        System.setProperty("java.rmi.server.randomIDs", "true");
        System.setProperty("java.rmi.server.useCodebaseOnly", "true");
        System.clearProperty("java.rmi.server.codebase");
        System.setProperty("java.rmi.server.disableHttp", "true");

        if(useSsl) {
            // SSL
            if(listenAddress!=null && listenAddress.length()>0) {
                csf = new RMIClientSocketFactorySSL();
                ssf = new RMIServerSocketFactorySSL(listenAddress);
            } else {
                csf = new RMIClientSocketFactorySSL();
                ssf = new RMIServerSocketFactorySSL();
            }
        } else {
            // Non-SSL
            if(listenAddress!=null && listenAddress.length()>0) {
                csf = new RMIClientSocketFactoryTCP();
                ssf = new RMIServerSocketFactoryTCP(listenAddress);
            } else {
                csf = new RMIClientSocketFactoryTCP();
                ssf = new RMIServerSocketFactoryTCP();
            }
        }

        Registry registry = RegistryManager.createRegistry(port, csf, ssf);
        Remote stub = UnicastRemoteObject.exportObject(this, port, csf, ssf);
        registry.rebind(AOServConnectorFactory.class.getName()+"_Stub", stub);
        this.port = port;
        this.wrapped = wrapped;
    }

    private final AOServConnectorFactoryCache<C,F> connectors = new AOServConnectorFactoryCache<C,F>();

    public C getConnector(Locale locale, String connectAs, String authenticateAs, String password, String daemonServer) throws LoginException, RemoteException {
        synchronized(connectors) {
            C connector = connectors.get(connectAs, authenticateAs, password, daemonServer);
            if(connector!=null) {
                connector.setLocale(locale);
            } else {
                connector = newConnector(
                    locale,
                    connectAs,
                    authenticateAs,
                    password,
                    daemonServer
                );
            }
            return connector;
        }
    }

    /**
     * Connectors are exported as they are created.
     */
    public C newConnector(Locale locale, String connectAs, String authenticateAs, String password, String daemonServer) throws LoginException, RemoteException {
        synchronized(connectors) {
            C connector = wrapped.newConnector(locale, connectAs, authenticateAs, password, daemonServer);
            UnicastRemoteObject.exportObject(connector, port, csf, ssf);
            for(AOServService<C,F,?,?> service : connector.getServices().values()) {
                UnicastRemoteObject.exportObject(service, port, csf, ssf);
            }
            connectors.put(
                connectAs,
                authenticateAs,
                password,
                daemonServer,
                connector
            );
            return connector;
        }
    }

    // TODO: Unexport and shutdown idle connectors.
}
