package com.aoindustries.aoserv.client.rmi;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnectorFactory;
import com.aoindustries.rmi.RMIClientSocketFactorySSL;
import com.aoindustries.rmi.RMIClientSocketFactoryTCP;
import com.aoindustries.security.LoginException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.util.Locale;

/**
 * An implementation of <code>AOServConnectorFactory</code> that connects to the master
 * server over RMI.
 *
 * @author  AO Industries, Inc.
 */
final public class RmiConnectorFactory implements AOServConnectorFactory<RmiConnector,RmiConnectorFactory> {

    final String serverAddress;
    final int serverPort;
    final RMIClientSocketFactory csf;

    public RmiConnectorFactory(
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

    static interface RetryCallable<T> {
        T call() throws LoginException, RemoteException;
    }

    static <T> T retry(RetryCallable<T> callable) throws LoginException, RemoteException {
        int attempt = 1;
        while(!Thread.interrupted()) {
            try {
                return callable.call();
            } catch(RuntimeException err) {
                if(Thread.interrupted() || attempt>=RmiUtils.RETRY_ATTEMPTS || RmiUtils.isImmediateFail(err)) throw err;
            } catch(LoginException err) {
                if(Thread.interrupted() || attempt>=RmiUtils.RETRY_ATTEMPTS || RmiUtils.isImmediateFail(err)) throw err;
            } catch(RemoteException err) {
                if(Thread.interrupted() || attempt>=RmiUtils.RETRY_ATTEMPTS || RmiUtils.isImmediateFail(err)) throw err;
            }
            try {
                Thread.sleep(RmiUtils.retryAttemptDelays[attempt-1]);
            } catch(InterruptedException err) {
                throw new RemoteException(err.getMessage(), err);
            }
            attempt++;
        }
        throw new RemoteException("interrupted", new InterruptedException("interrupted"));
    }

    public RmiConnector getConnector(final Locale locale, final String connectAs, final String authenticateAs, final String password, final String daemonServer) throws LoginException, RemoteException {
        return retry(
            new RetryCallable<RmiConnector>() {
                public RmiConnector call() throws LoginException, RemoteException {
                    try {
                        return new RmiConnector(RmiConnectorFactory.this, locale, connectAs, authenticateAs, password, daemonServer);
                    } catch(NotBoundException err) {
                        throw new RemoteException(err.getMessage(), err);
                    }
                }
            }
        );
    }
}
