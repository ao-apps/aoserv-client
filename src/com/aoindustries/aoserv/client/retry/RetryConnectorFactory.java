/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.retry;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.aoserv.client.wrapped.*;
import com.aoindustries.security.LoginException;
import java.rmi.RemoteException;
import java.util.Locale;

/**
 * An implementation of <code>AOServConnectorFactory</code> that obtains a new wrapped connector from the wrapped
 * factory when certain types of errors occur.  This is useful to automatically reconnect to a rebooted RMI server.
 *
 * @author  AO Industries, Inc.
 */
final public class RetryConnectorFactory extends WrappedConnectorFactory<RetryConnector,RetryConnectorFactory> {

    public RetryConnectorFactory(AOServConnectorFactory wrapped) {
        super(wrapped);
    }

    @Override
    protected RetryConnector newWrappedConnector(final Locale locale, final UserId connectAs, final UserId authenticateAs, final String password, final DomainName daemonServer) throws LoginException, RemoteException {
        int attempt = 1;
        while(!Thread.interrupted()) {
            try {
                return new RetryConnector(RetryConnectorFactory.this, locale, connectAs, authenticateAs, password, daemonServer);
            } catch(LoginException err) {
                throw err;
            } catch(RemoteException err) {
                if(Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS) throw err;
            } catch(RuntimeException err) {
                if(Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS) throw err;
            } catch(Exception err) {
                if(Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS) throw new RemoteException(err.getMessage(), err);
            }
            try {
                Thread.sleep(RetryUtils.retryAttemptDelays[attempt-1]);
            } catch(InterruptedException err) {
                throw new RemoteException(err.getMessage(), err);
            }
            attempt++;
        }
        throw new RemoteException("interrupted", new InterruptedException("interrupted"));
    }
}
