/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.cache;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.security.LoginException;
import java.rmi.RemoteException;
import java.util.Locale;

/**
 * An implementation of <code>AOServConnectorFactory</code> that transfers entire
 * tables as a set and performs local lookups.
 *
 * @author  AO Industries, Inc.
 */
final public class CachedConnectorFactory implements AOServConnectorFactory {

    final AOServConnectorFactory wrapped;

    public CachedConnectorFactory(AOServConnectorFactory wrapped) {
        this.wrapped = wrapped;
    }

    private final AOServConnectorFactoryCache<CachedConnector> connectors = new AOServConnectorFactoryCache<CachedConnector>();

    @Override
    public CachedConnector getConnector(Locale locale, UserId username, String password, UserId switchUser, DomainName daemonServer, boolean readOnly) throws LoginException, RemoteException {
        synchronized(connectors) {
            CachedConnector connector = connectors.get(locale, username, password, switchUser, daemonServer, readOnly);
            if(connector==null) {
                connector = new CachedConnector(this, wrapped.getConnector(locale, username, password, switchUser, daemonServer, readOnly));
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
}
