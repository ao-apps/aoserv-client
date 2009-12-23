package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnectorFactory;
import com.aoindustries.aoserv.client.AbstractConnectorFactory;
import com.aoindustries.security.LoginException;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.UUID;

/**
 * An implementation of <code>AOServConnectorFactory</code> that transfers entire
 * tables as a set and performs local lookups.
 *
 * @author  AO Industries, Inc.
 */
final public class CachedConnectorFactory extends AbstractConnectorFactory<CachedConnector,CachedConnectorFactory> {

    final AOServConnectorFactory<?,?> wrapped;

    public CachedConnectorFactory(AOServConnectorFactory<?,?> wrapped) {
        this.wrapped = wrapped;
    }

    protected CachedConnector createConnector(final UUID connectorId, final Locale locale, final String connectAs, final String authenticateAs, final String password, final String daemonServer) throws LoginException, RemoteException {
        return new CachedConnector(this, wrapped.getConnector(connectorId, locale, connectAs, authenticateAs, password, daemonServer), authenticateAs, password, daemonServer);
    }
}
