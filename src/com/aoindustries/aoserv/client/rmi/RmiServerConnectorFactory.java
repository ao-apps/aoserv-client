package com.aoindustries.aoserv.client.rmi;

/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServConnectorFactory;
import com.aoindustries.aoserv.client.AOServConnectorFactoryCache;
import com.aoindustries.aoserv.client.AOServService;
import com.aoindustries.aoserv.client.validator.DomainName;
import com.aoindustries.aoserv.client.validator.Hostname;
import com.aoindustries.aoserv.client.validator.InetAddress;
import com.aoindustries.aoserv.client.validator.NetPort;
import com.aoindustries.aoserv.client.validator.UserId;
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

    private final NetPort port;
    private final RMIClientSocketFactory csf;
    private final RMIServerSocketFactory ssf;
    private final AOServConnectorFactory<C,F> wrapped;

    public RmiServerConnectorFactory(
        Hostname publicAddress,
        InetAddress listenAddress,
        NetPort port,
        boolean useSsl,
        AOServConnectorFactory<C,F> wrapped
    ) throws RemoteException {
        // Setup the RMI system properties
        if(publicAddress!=null) {
            System.setProperty("java.rmi.server.hostname", publicAddress.toString());
        } else if(listenAddress!=null) {
            System.setProperty("java.rmi.server.hostname", listenAddress.getAddress());
        } else {
            System.clearProperty("java.rmi.server.hostname");
        }
        System.setProperty("java.rmi.server.randomIDs", "true");
        System.setProperty("java.rmi.server.useCodebaseOnly", "true");
        System.clearProperty("java.rmi.server.codebase");
        System.setProperty("java.rmi.server.disableHttp", "true");
        System.setProperty("sun.rmi.server.suppressStackTraces", "true");

        if(useSsl) {
            // SSL
            if(listenAddress!=null) {
                csf = new RMIClientSocketFactorySSL();
                ssf = new RMIServerSocketFactorySSL(listenAddress.getAddress());
            } else {
                csf = new RMIClientSocketFactorySSL();
                ssf = new RMIServerSocketFactorySSL();
            }
        } else {
            // Non-SSL
            if(listenAddress!=null) {
                csf = new RMIClientSocketFactoryTCP();
                ssf = new RMIServerSocketFactoryTCP(listenAddress.getAddress());
            } else {
                csf = new RMIClientSocketFactoryTCP();
                ssf = new RMIServerSocketFactoryTCP();
            }
        }

        Registry registry = RegistryManager.createRegistry(port.getPort(), csf, ssf);
        Remote stub = UnicastRemoteObject.exportObject(this, port.getPort(), csf, ssf);
        registry.rebind(AOServConnectorFactory.class.getName()+"_Stub", stub);
        this.port = port;
        this.wrapped = wrapped;
    }

    private final AOServConnectorFactoryCache<C,F> connectors = new AOServConnectorFactoryCache<C,F>();

    @Override
    public C getConnector(Locale locale, UserId connectAs, UserId authenticateAs, String password, DomainName daemonServer, boolean readOnly) throws LoginException, RemoteException {
        synchronized(connectors) {
            C connector = connectors.get(connectAs, authenticateAs, password, daemonServer, readOnly);
            if(connector!=null) {
                connector.setLocale(locale);
            } else {
                connector = newConnector(
                    locale,
                    connectAs,
                    authenticateAs,
                    password,
                    daemonServer,
                    readOnly
                );
            }
            return connector;
        }
    }

    /**
     * Connectors are exported as they are created.
     */
    @Override
    public C newConnector(Locale locale, UserId connectAs, UserId authenticateAs, String password, DomainName daemonServer, boolean readOnly) throws LoginException, RemoteException {
        synchronized(connectors) {
            C connector = wrapped.newConnector(locale, connectAs, authenticateAs, password, daemonServer, readOnly);
            UnicastRemoteObject.exportObject(connector, port.getPort(), csf, ssf);
            for(AOServService<C,F,?,?> service : connector.getServices().values()) {
                UnicastRemoteObject.exportObject(service, port.getPort(), csf, ssf);
            }
            connectors.put(
                connectAs,
                authenticateAs,
                password,
                daemonServer,
                readOnly,
                connector
            );
            return connector;
        }
    }

    // TODO: Unexport and shutdown idle connectors.
}
