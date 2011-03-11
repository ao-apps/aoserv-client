/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.rmi;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.rmi.*;
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
final public class RmiServerConnectorFactory implements AOServConnectorFactory {

    private final NetPort port;
    private final RMIClientSocketFactory csf;
    private final RMIServerSocketFactory ssf;
    private final AOServConnectorFactory wrapped;

    public RmiServerConnectorFactory(
        Hostname publicAddress,
        InetAddress listenAddress,
        NetPort port,
        boolean useSsl,
        AOServConnectorFactory wrapped
    ) throws RemoteException {
        // Setup the RMI system properties
        if(publicAddress!=null) {
            System.setProperty("java.rmi.server.hostname", publicAddress.toString());
        } else if(listenAddress!=null) {
            System.setProperty("java.rmi.server.hostname", listenAddress.toString());
        } else {
            System.clearProperty("java.rmi.server.hostname");
        }
        System.setProperty("java.rmi.server.randomIDs", "true");
        System.setProperty("java.rmi.server.useCodebaseOnly", "true");
        System.clearProperty("java.rmi.server.codebase");
        System.setProperty("java.rmi.server.disableHttp", "true");
        // System.setProperty("sun.rmi.server.suppressStackTraces", "true");

        if(useSsl) {
            // SSL
            if(listenAddress!=null) {
                csf = new RMIClientSocketFactorySSL();
                ssf = new RMIServerSocketFactorySSL(listenAddress.toString());
            } else {
                csf = new RMIClientSocketFactorySSL();
                ssf = new RMIServerSocketFactorySSL();
            }
        } else {
            // Non-SSL
            if(listenAddress!=null) {
                csf = new RMIClientSocketFactoryTCP();
                ssf = new RMIServerSocketFactoryTCP(listenAddress.toString());
            } else {
                csf = new RMIClientSocketFactoryTCP();
                ssf = new RMIServerSocketFactoryTCP();
            }
        }

        Registry registry = RegistryManager.createRegistry(port.getPort(), csf, ssf);
        Remote stub = UnicastRemoteObject.exportObject(this, port.getPort(), csf, ssf);
        //try {
        //    registry.bind(AOServConnectorFactory.class.getName()+"_Stub", stub);
        //} catch(AlreadyBoundException err) {
            //throw new RemoteException(err.getMessage(), err);
            registry.rebind(AOServConnectorFactory.class.getName()+"_Stub", stub);
        //}
        this.port = port;
        this.wrapped = wrapped;
    }

    private final AOServConnectorFactoryCache<AOServConnector> connectors = new AOServConnectorFactoryCache<AOServConnector>();

    @Override
    public AOServConnector getConnector(Locale locale, UserId connectAs, UserId authenticateAs, String password, DomainName daemonServer) throws LoginException, RemoteException {
        synchronized(connectors) {
            AOServConnector connector = connectors.get(connectAs, authenticateAs, password, daemonServer);
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
    //@Override
    private AOServConnector newConnector(Locale locale, UserId connectAs, UserId authenticateAs, String password, DomainName daemonServer) throws LoginException, RemoteException {
        synchronized(connectors) {
            AOServConnector connector = wrapped.getConnector(locale, connectAs, authenticateAs, password, daemonServer);
            UnicastRemoteObject.exportObject(connector, port.getPort(), csf, ssf);
            for(AOServService<?,?> service : connector.getServices().values()) {
                UnicastRemoteObject.exportObject(service, port.getPort(), csf, ssf);
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
