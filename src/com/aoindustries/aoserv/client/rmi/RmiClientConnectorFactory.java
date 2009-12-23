package com.aoindustries.aoserv.client.rmi;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServConnectorFactory;
import com.aoindustries.rmi.RMIClientSocketFactorySSL;
import com.aoindustries.rmi.RMIClientSocketFactoryTCP;
import com.aoindustries.security.LoginException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.util.Locale;

/**
 * An implementation of <code>AOServConnectorFactory</code> that connects to an RMI server.
 *
 * @author  AO Industries, Inc.
 */
final public class RmiClientConnectorFactory implements AOServConnectorFactory {

    final String serverAddress;
    final int serverPort;
    final RMIClientSocketFactory csf;

    public RmiClientConnectorFactory(
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

    public AOServConnector<?,?> newConnector(Locale locale, String connectAs, String authenticateAs, String password, String daemonServer) throws LoginException, RemoteException {
        try {
            // Connect to the remote registry and get each of the stubs
            Registry remoteRegistry = LocateRegistry.getRegistry(serverAddress, serverPort, csf);
            AOServConnectorFactory<?,?> serverFactory = (AOServConnectorFactory)remoteRegistry.lookup(AOServConnectorFactory.class.getName()+"_Stub");
            return serverFactory.newConnector(locale, connectAs, authenticateAs, password, daemonServer);
        } catch(NotBoundException err) {
            throw new RemoteException(err.getMessage(), err);
        }
    }
}
