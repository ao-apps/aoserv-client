package com.aoindustries.aoserv.client.timeout;

/*
 * Copyright 2010 by AO Industries, Inc.,
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * An implementation of <code>AOServConnectorFactory</code> that times-out on calls to the wrapped
 * factory.
 *
 * @author  AO Industries, Inc.
 */
final public class TimeoutConnectorFactory extends WrappedConnectorFactory<TimeoutConnector,TimeoutConnectorFactory> {

    static final ExecutorService executorService = Executors.newCachedThreadPool(
        new ThreadFactory() {
            public Thread newThread(Runnable r) {
                return new Thread(r, TimeoutConnectorFactory.class.getName());
            }
        }
    );

    final long timeout;
    final TimeUnit unit;

    public TimeoutConnectorFactory(AOServConnectorFactory<?,?> wrapped, long timeout, TimeUnit unit) {
        super(wrapped);
        this.timeout = timeout;
        this.unit = unit;
    }

    protected TimeoutConnector newWrappedConnector(final Locale locale, final UserId connectAs, final UserId authenticateAs, final String password, final DomainName daemonServer) throws LoginException, RemoteException {
        Future<TimeoutConnector> future = executorService.submit(
            new Callable<TimeoutConnector>() {
                public TimeoutConnector call() throws RemoteException, LoginException {
                    return new TimeoutConnector(TimeoutConnectorFactory.this, locale, connectAs, authenticateAs, password, daemonServer);
                }
            }
        );
        try {
            return future.get(timeout, unit);
        } catch(RuntimeException err) {
            throw err;
        } catch(ExecutionException err) {
            Throwable cause = err.getCause();
            if(cause instanceof LoginException) throw (LoginException)cause;
            if(cause instanceof RemoteException) throw (RemoteException)cause;
            throw new RemoteException(err.getMessage(), err);
        } catch(TimeoutException err) {
            future.cancel(true);
            throw new RemoteException(err.getMessage(), err);
        } catch(InterruptedException err) {
            throw new RemoteException(err.getMessage(), err);
        }
    }
}
