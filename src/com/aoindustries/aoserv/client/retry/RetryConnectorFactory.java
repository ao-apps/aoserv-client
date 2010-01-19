package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnectorFactory;
import com.aoindustries.aoserv.client.validator.DomainName;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.aoserv.client.wrapped.WrappedConnectorFactory;
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

    public RetryConnectorFactory(AOServConnectorFactory<?,?> wrapped) {
        super(wrapped);
    }

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
