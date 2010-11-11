/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.timeout;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.aoserv.client.wrapped.*;
import com.aoindustries.security.LoginException;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * @see NoSwingConnectorFactory
 *
 * @author  AO Industries, Inc.
 */
final public class TimeoutConnector extends WrappedConnector<TimeoutConnector,TimeoutConnectorFactory> {

    TimeoutConnector(TimeoutConnectorFactory factory, Locale locale, UserId connectAs, UserId authenticateAs, String password, DomainName daemonServer) throws RemoteException, LoginException {
        super(factory, locale, connectAs, authenticateAs, password, daemonServer);
    }

    @Override
    protected <T> T call(Callable<T> callable, boolean allowRetry) throws RemoteException, NoSuchElementException {
        Future<T> future = TimeoutConnectorFactory.executorService.submit(callable);
        try {
            return future.get(factory.timeout, factory.unit);
        } catch(RuntimeException err) {
            throw err;
        } catch(ExecutionException err) {
            Throwable cause = err.getCause();
            if(cause instanceof RemoteException) throw (RemoteException)cause;
            if(cause instanceof NoSuchElementException) throw (NoSuchElementException)cause;
            throw new RemoteException(err.getMessage(), err);
        } catch(TimeoutException err) {
            future.cancel(true);
            throw new RemoteException(err.getMessage(), err);
        } catch(InterruptedException err) {
            throw new RemoteException(err.getMessage(), err);
        }
    }
}
