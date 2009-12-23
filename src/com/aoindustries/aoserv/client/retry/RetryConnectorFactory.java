package com.aoindustries.aoserv.client.retry;

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
 * An implementation of <code>AOServConnectorFactory</code> that obtains a new wrapped connector from the wrapped
 * factory when certain types of errors occur.  This is useful to automatically reconnect to a rebooted RMI server.
 *
 * @author  AO Industries, Inc.
 */
final public class RetryConnectorFactory implements AOServConnectorFactory<RetryConnector,RetryConnectorFactory> {

    final AOServConnectorFactory<?,?> wrapped;

    public RetryConnectorFactory(AOServConnectorFactory<?,?> wrapped) {
        this.wrapped = wrapped;
    }

    static interface RetryCallable<T> {
        T call() throws LoginException, RemoteException;
    }

    static <T> T retry(RetryCallable<T> callable) throws LoginException, RemoteException {
        int attempt = 1;
        while(!Thread.interrupted()) {
            try {
                return callable.call();
            } catch(RuntimeException err) {
                if(Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) throw err;
            } catch(LoginException err) {
                if(Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) throw err;
            } catch(RemoteException err) {
                if(Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) throw err;
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

    public RetryConnector newConnector(final Locale locale, final String connectAs, final String authenticateAs, final String password, final String daemonServer) throws LoginException, RemoteException {
        return retry(
            new RetryCallable<RetryConnector>() {
                public RetryConnector call() throws LoginException, RemoteException {
                    return new RetryConnector(RetryConnectorFactory.this, locale, connectAs, authenticateAs, password, daemonServer);
                }
            }
        );
    }
}
