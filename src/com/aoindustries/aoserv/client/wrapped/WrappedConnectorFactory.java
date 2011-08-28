/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.wrapped;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.security.LoginException;
import java.rmi.RemoteException;
import java.util.Locale;

/**
 * An implementation of <code>AOServConnectorFactory</code> that wraps another factory and intercepts all calls.
 *
 * @author  AO Industries, Inc.
 */
abstract public class WrappedConnectorFactory<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>> implements AOServConnectorFactory {

    final AOServConnectorFactory wrapped;

    public WrappedConnectorFactory(AOServConnectorFactory wrapped) {
        this.wrapped = wrapped;
    }

    private final AOServConnectorFactoryCache<C> connectors = new AOServConnectorFactoryCache<C>();

    @Override
    final public C getConnector(Locale locale, UserId username, String password, UserId switchUser, DomainName daemonServer, boolean readOnly) throws LoginException, RemoteException {
        synchronized(connectors) {
            C connector = connectors.get(locale, username, password, switchUser, daemonServer, readOnly);
            if(connector==null) {
                connector = newWrappedConnector(locale, username, password, switchUser, daemonServer, readOnly);
                connectors.put(
                    locale,
                    username,
                    password,
                    switchUser,
                    daemonServer,
                    readOnly,
                    connector
                );
            }
            return connector;
        }
    }

    /**
     * Creates a new connector for this factory.
     */
    abstract protected C newWrappedConnector(Locale locale, UserId username, String password, UserId switchUser, DomainName daemonServer, boolean readOnly) throws LoginException, RemoteException;
}
