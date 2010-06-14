package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.validator.DomainName;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.security.LoginException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Locale;

/**
 * <p>
 * An <code>AOServConnectorFactory</code> obtains instances of <code>AOServConnector</code>
 * for the provided connectAs, authenticateAs, and password values.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public interface AOServConnectorFactory<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends Remote {

    /**
     * Gets an existing <code>AOServConnector</code> or creates a new one if does not yet exist.
     */
    C getConnector(Locale locale, UserId connectAs, UserId authenticateAs, String password, DomainName daemonServer, boolean readOnly) throws RemoteException, LoginException;

    /**
     * Creates a new <code>AOServConnector</code> for the provided connectAs, authenticateAs, and password.
     */
    C newConnector(Locale locale, UserId connectAs, UserId authenticateAs, String password, DomainName daemonServer, boolean readOnly) throws RemoteException, LoginException;
}
