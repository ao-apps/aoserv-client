package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnectorFactory;
import com.aoindustries.security.LoginException;
import java.rmi.RemoteException;
import java.util.Locale;

/**
 * An implementation of <code>AOServConnectorFactory</code> that transfers entire
 * tables as a set and performs local lookups.
 *
 * @author  AO Industries, Inc.
 */
final public class CachedConnectorFactory implements AOServConnectorFactory<CachedConnector,CachedConnectorFactory> {

    final AOServConnectorFactory<?,?> wrapped;

    public CachedConnectorFactory(AOServConnectorFactory<?,?> wrapped) {
        this.wrapped = wrapped;
    }

    public CachedConnector newConnector(Locale locale, String connectAs, String authenticateAs, String password, String daemonServer) throws LoginException, RemoteException {
        return new CachedConnector(this, wrapped.newConnector(locale, connectAs, authenticateAs, password, daemonServer));
    }
}
