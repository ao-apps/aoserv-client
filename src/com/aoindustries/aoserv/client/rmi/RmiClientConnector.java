package com.aoindustries.aoserv.client.rmi;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.validator.DomainName;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.aoserv.client.wrapped.WrappedConnector;
import com.aoindustries.security.LoginException;
import java.rmi.ConnectException;
import java.rmi.MarshalException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @see RmiClientConnectorFactory
 *
 * @author  AO Industries, Inc.
 */
final public class RmiClientConnector extends WrappedConnector<RmiClientConnector,RmiClientConnectorFactory> {

    private static final Logger logger = Logger.getLogger(RmiClientConnector.class.getName());

    RmiClientConnector(RmiClientConnectorFactory factory, Locale locale, UserId connectAs, UserId authenticateAs, String password, DomainName daemonServer) throws RemoteException, LoginException {
        super(factory, locale, connectAs, authenticateAs, password, daemonServer);
    }

    @Override
    protected <T> T call(Callable<T> callable, boolean allowRetry) throws RemoteException, NoSuchElementException {
        try {
            return callable.call();
        } catch(NoSuchObjectException err) {
            try {
                disconnect();
            } catch(RemoteException err2) {
                logger.log(Level.SEVERE, null, err2);
            }
            throw err;
        } catch(ConnectException err) {
            try {
                disconnect();
            } catch(RemoteException err2) {
                logger.log(Level.SEVERE, null, err2);
            }
            throw err;
        } catch(MarshalException err) {
            try {
                disconnect();
            } catch(RemoteException err2) {
                logger.log(Level.SEVERE, null, err2);
            }
            throw err;
        } catch(RemoteException err) {
            throw err;
        } catch(NoSuchElementException err) {
            throw err;
        } catch(Exception err) {
            throw new RemoteException(err.getMessage(), err);
        }
    }

    protected boolean isAoServObjectServiceSettable() {
        return true;
    }
}
