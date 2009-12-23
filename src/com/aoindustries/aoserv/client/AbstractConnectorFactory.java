package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.security.LoginException;
import com.aoindustries.util.StringUtility;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A base implementation of <code>AOServConnectorFactory</code>.
 *
 * @author  AO Industries, Inc.
 */
abstract public class AbstractConnectorFactory<C extends AbstractConnector<C,F>, F extends AbstractConnectorFactory<C,F>> implements AOServConnectorFactory<C,F> {

    protected AbstractConnectorFactory() {
    }

    private final ConcurrentMap<UUID,C> connectors = new ConcurrentHashMap<UUID,C>();

    final public C getConnector(UUID connectorId, Locale locale, String connectAs, String authenticateAs, String password, String daemonServer) throws LoginException, RemoteException {
        C connector = connectors.get(connectorId);
        if(connector==null) {
            connector = createConnector(connectorId, locale, connectAs, authenticateAs, password, daemonServer);
            C existing = connectors.putIfAbsent(connectorId, connector);
            if(existing!=null) {
                connector = existing;
            } else {
                // TODO: Register for table invalidation signals
            }
        } else {
            // connectAs and authenticateAs must match - password verified earlier.
            if(!connector.connectAs.equals(connectAs)) throw new LoginException(ApplicationResources.accessor.getMessage(locale, "AbstractConnectorFactory.getConnector.connectorIdConnectAsMismatch", connectAs));
            if(!connector.authenticateAs.equals(authenticateAs)) throw new LoginException(ApplicationResources.accessor.getMessage(locale, "AbstractConnectorFactory.getConnector.connectorIdAuthenticateAsMismatch", authenticateAs));
            if(!connector.password.equals(password)) throw new LoginException(ApplicationResources.accessor.getMessage(locale, "AbstractConnectorFactory.getConnector.connectorIdPasswordMismatch"));
            if(!StringUtility.equals(connector.daemonServer, daemonServer)) throw new LoginException(ApplicationResources.accessor.getMessage(locale, "AbstractConnectorFactory.getConnector.connectorIdDaemonServerMismatch", daemonServer));
        }
        return connector;
    }

    abstract protected C createConnector(UUID connectorId, Locale locale, String connectAs, String authenticateAs, String password, String daemonServer) throws LoginException, RemoteException;
}
