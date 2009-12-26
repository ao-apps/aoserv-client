package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnectorFactory;
import com.aoindustries.aoserv.client.AOServConnectorFactoryCache;
import com.aoindustries.security.LoginException;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * An implementation of <code>AOServConnectorFactory</code> that obtains a new wrapped connector from the wrapped
 * factory when certain types of errors occur.  This is useful to automatically reconnect to a rebooted RMI server.
 *
 * @author  AO Industries, Inc.
 */
final public class RetryConnectorFactory implements AOServConnectorFactory<RetryConnector,RetryConnectorFactory> {

    final long timeout;
    final TimeUnit unit;
    final AOServConnectorFactory<?,?> wrapped;

    public RetryConnectorFactory(long timeout, TimeUnit unit, AOServConnectorFactory<?,?> wrapped) {
        this.timeout = timeout;
        this.unit = unit;
        this.wrapped = wrapped;
    }

    private <T> T retry(Callable<T> callable) throws LoginException, RemoteException {
        int attempt = 1;
        while(!Thread.interrupted()) {
            if(timeout>0) {
                Future<T> future = RetryUtils.executorService.submit(callable);
                try {
                    return future.get(timeout, unit);
                } catch(RuntimeException err) {
                    if(Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) throw err;
                } catch(ExecutionException err) {
                    if(Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) {
                        Throwable cause = err.getCause();
                        if(cause instanceof LoginException) throw (LoginException)cause;
                        if(cause instanceof RemoteException) throw (RemoteException)cause;
                        throw new RemoteException(err.getMessage(), err);
                    }
                } catch(TimeoutException err) {
                    future.cancel(true);
                    if(Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) throw new RemoteException(err.getMessage(), err);
                } catch(Exception err) {
                    if(Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) throw new RemoteException(err.getMessage(), err);
                }
            } else {
                try {
                    return callable.call();
                } catch(RuntimeException err) {
                    if(Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) throw err;
                } catch(LoginException err) {
                    if(Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) throw err;
                } catch(RemoteException err) {
                    if(Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) throw err;
                } catch(Exception err) {
                    if(Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) throw new RemoteException(err.getMessage(), err);
                }
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

    private final AOServConnectorFactoryCache<RetryConnector,RetryConnectorFactory> connectors = new AOServConnectorFactoryCache<RetryConnector,RetryConnectorFactory>();

    public RetryConnector getConnector(Locale locale, String connectAs, String authenticateAs, String password, String daemonServer) throws LoginException, RemoteException {
        synchronized(connectors) {
            RetryConnector connector = connectors.get(connectAs, authenticateAs, password, daemonServer);
            if(connector!=null) {
                connector.setLocale(locale);
            } else {
                connector = newConnector(
                    locale,
                    connectAs,
                    authenticateAs,
                    password,
                    daemonServer
                );
            }
            return connector;
        }
    }

    public RetryConnector newConnector(final Locale locale, final String connectAs, final String authenticateAs, final String password, final String daemonServer) throws LoginException, RemoteException {
        synchronized(connectors) {
            RetryConnector connector = retry(
                new Callable<RetryConnector>() {
                    public RetryConnector call() throws LoginException, RemoteException {
                        return new RetryConnector(RetryConnectorFactory.this, locale, connectAs, authenticateAs, password, daemonServer);
                    }
                }
            );
            connectors.put(
                connectAs,
                authenticateAs,
                password,
                daemonServer,
                connector
            );
            return connector;
        }
    }
}
