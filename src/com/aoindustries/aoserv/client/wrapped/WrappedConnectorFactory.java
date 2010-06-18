package com.aoindustries.aoserv.client.wrapped;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnectorFactory;
import com.aoindustries.aoserv.client.AOServConnectorFactoryCache;
import com.aoindustries.aoserv.client.validator.DomainName;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.security.LoginException;
import java.rmi.RemoteException;
import java.util.Locale;

/**
 * An implementation of <code>AOServConnectorFactory</code> that wraps another factory and intercepts all calls.
 *
 * @author  AO Industries, Inc.
 */
abstract public class WrappedConnectorFactory<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> implements AOServConnectorFactory<C,F> {

    final AOServConnectorFactory<?,?> wrapped;

    public WrappedConnectorFactory(AOServConnectorFactory<?,?> wrapped) {
        this.wrapped = wrapped;
    }

    private final AOServConnectorFactoryCache<C,F> connectors = new AOServConnectorFactoryCache<C,F>();

    @Override
    final public C getConnector(Locale locale, UserId connectAs, UserId authenticateAs, String password, DomainName daemonServer) throws LoginException, RemoteException {
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

    @Override
    final public C newConnector(Locale locale, UserId connectAs, UserId authenticateAs, String password, DomainName daemonServer) throws LoginException, RemoteException {
        synchronized(connectors) {
            C connector = newWrappedConnector(locale, connectAs, authenticateAs, password, daemonServer);
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

    /**
     * Creates a new connector for this factory.
     */
    abstract protected C newWrappedConnector(Locale locale, UserId connectAs, UserId authenticateAs, String password, DomainName daemonServer) throws LoginException, RemoteException;
}
