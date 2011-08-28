/*
 * Copyright 2001-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.security.LoginException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Locale;

/**
 * <p>
 * An <code>AOServConnectorFactory</code> obtains instances of <code>AOServConnector</code>
 * for the provided locale, username, password, switchUser, daemonServer, and readOnly values.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public interface AOServConnectorFactory extends Remote {

    /**
     * Gets an existing <code>AOServConnector</code> or creates a new one if does not yet exist.
     */
    AOServConnector getConnector(Locale locale, UserId username, String password, UserId switchUser, DomainName daemonServer, boolean readOnly) throws RemoteException, LoginException;
}
