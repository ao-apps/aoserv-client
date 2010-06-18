package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.validator.DomainName;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.aoserv.client.wrapped.WrappedConnector;
import com.aoindustries.security.LoginException;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

/**
 * @see  RetryConnectorFactory
 *
 * @author  AO Industries, Inc.
 */
final public class RetryConnector extends WrappedConnector<RetryConnector,RetryConnectorFactory> {

    RetryConnector(RetryConnectorFactory factory, Locale locale, UserId connectAs, UserId authenticateAs, String password, DomainName daemonServer) throws RemoteException, LoginException {
        super(factory, locale, connectAs, authenticateAs, password, daemonServer);
    }

    @Override
    protected <T> T call(Callable<T> callable, boolean allowRetry) throws RemoteException, NoSuchElementException {
        int attempt = 1;
        while(!Thread.interrupted()) {
            try {
                return callable.call();
            } catch(RemoteException err) {
                if(!allowRetry || Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS) throw err;
            } catch(NoSuchElementException err) {
                throw err;
            } catch(RuntimeException err) {
                if(!allowRetry || Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS) throw err;
            } catch(Exception err) {
                if(!allowRetry || Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS) throw new RemoteException(err.getMessage(), err);
            }
            assert allowRetry : "allowRetry==false";
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
