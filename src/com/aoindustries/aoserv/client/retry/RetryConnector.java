package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServConnectorUtils;
import com.aoindustries.aoserv.client.AOServService;
import com.aoindustries.aoserv.client.BusinessAdministrator;
import com.aoindustries.aoserv.client.BusinessAdministratorService;
import com.aoindustries.aoserv.client.BusinessService;
import com.aoindustries.aoserv.client.DisableLogService;
import com.aoindustries.aoserv.client.PackageCategoryService;
import com.aoindustries.aoserv.client.ResourceTypeService;
import com.aoindustries.aoserv.client.ServiceName;
import com.aoindustries.security.LoginException;
import java.rmi.ConnectException;
import java.rmi.MarshalException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @see  RetryConnectorFactory
 *
 * @author  AO Industries, Inc.
 */
final public class RetryConnector implements AOServConnector<RetryConnector,RetryConnectorFactory> {

    private static final Logger logger = Logger.getLogger(RetryConnector.class.getName());

    final RetryConnectorFactory factory;
    Locale locale;
    final String connectAs;
    private final String authenticateAs;
    private final String password;
    private final String daemonServer;
    final RetryBusinessAdministratorService businessAdministrators;
    final RetryBusinessService businesses;
    final RetryDisableLogService disabledLogs;
    final RetryPackageCategoryService packageCategories;
    final RetryResourceTypeService resourceTypes;

    final Object connectionLock = new Object();
    private AOServConnector<?,?> wrapped;

    RetryConnector(RetryConnectorFactory factory, Locale locale, String connectAs, String authenticateAs, String password, String daemonServer) throws RemoteException, LoginException {
        this.factory = factory;
        this.locale = locale;
        this.connectAs = connectAs;
        this.authenticateAs = authenticateAs;
        this.password = password;
        this.daemonServer = daemonServer;
        businessAdministrators = new RetryBusinessAdministratorService(this);
        businesses = new RetryBusinessService(this);
        disabledLogs = new RetryDisableLogService(this);
        packageCategories = new RetryPackageCategoryService(this);
        resourceTypes = new RetryResourceTypeService(this);
        // Connect immediately in order to have the chance to throw exceptions that will occur during connection
        synchronized(connectionLock) {
            connect();
        }
    }

    @SuppressWarnings("unchecked")
    void connect() throws RemoteException, LoginException {
        assert Thread.holdsLock(connectionLock);

        // Connect to the remote registry and get each of the stubs
        AOServConnector<?,?> newWrapped = factory.wrapped.newConnector(locale, connectAs, authenticateAs, password, daemonServer);

        // Now that each stub has been successfully received, store as the current connection
        this.wrapped = newWrapped;
        for(ServiceName serviceName : ServiceName.values) {
            ((RetryService)getServices().get(serviceName)).wrapped = newWrapped.getServices().get(serviceName);
        }
    }

    /**
     * Disconnects if appropriate for the provided type of RemoteException.
     * TODO: Clear all caches on disconnect, how to signal outer cache layers?
     */
    void disconnectIfNeeded(Throwable err) {
        while(err!=null) {
            if(
                (err instanceof NoSuchObjectException)
                || (err instanceof ConnectException)
                || (err instanceof MarshalException)
            ) {
                try {
                    disconnect();
                } catch(RemoteException err2) {
                    logger.log(Level.SEVERE, null, err2);
                }
                break;
            }
            err = err.getCause();
        }
    }

    /**
     * Disconnects this client.  The client will automatically reconnect on the next use.
     */
    void disconnect() throws RemoteException {
        synchronized(connectionLock) {
            wrapped = null;
            for(AOServService<RetryConnector,RetryConnectorFactory,?,?> service : getServices().values()) {
                ((RetryService<?,?>)service).wrapped = null;
            }
        }
    }

    AOServConnector<?,?> getWrapped() throws RemoteException {
        synchronized(connectionLock) {
            if(wrapped==null) {
                try {
                    connect();
                } catch(Exception err) {
                    throw new RemoteException(err.getMessage(), err);
                }
            }
            return wrapped;
        }
    }

    static interface RetryCallable<T> {
        T call() throws RemoteException;
    }

    <T> T retry(RetryCallable<T> callable) throws RemoteException {
        int attempt = 1;
        while(!Thread.interrupted()) {
            try {
                return callable.call();
            } catch(RuntimeException err) {
                disconnectIfNeeded(err);
                if(Thread.interrupted() || attempt>=RetryUtils.RETRY_ATTEMPTS || RetryUtils.isImmediateFail(err)) throw err;
            } catch(RemoteException err) {
                disconnectIfNeeded(err);
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

    public RetryConnectorFactory getFactory() {
        return factory;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(final Locale locale) throws RemoteException {
        if(!this.locale.equals(locale)) {
            this.locale = locale;
            retry(
                new RetryCallable<Object>() {
                    public Object call() throws RemoteException {
                        getWrapped().setLocale(locale);
                        return null;
                    }
                }
            );
        }
    }

    public String getConnectAs() {
        return connectAs;
    }

    public BusinessAdministrator getThisBusinessAdministrator() throws RemoteException {
        BusinessAdministrator obj = getBusinessAdministrators().get(connectAs);
        if(obj==null) throw new RemoteException("Unable to find BusinessAdministrator: "+connectAs);
        return obj;
    }

    private final AtomicReference<Map<ServiceName,AOServService<RetryConnector,RetryConnectorFactory,?,?>>> tables = new AtomicReference<Map<ServiceName,AOServService<RetryConnector,RetryConnectorFactory,?,?>>>();
    public Map<ServiceName,AOServService<RetryConnector,RetryConnectorFactory,?,?>> getServices() throws RemoteException {
        Map<ServiceName,AOServService<RetryConnector,RetryConnectorFactory,?,?>> ts = tables.get();
        if(ts==null) {
            ts = AOServConnectorUtils.createServiceMap(this);
            if(!tables.compareAndSet(null, ts)) ts = tables.get();
        }
        return ts;
    }

    public BusinessAdministratorService<RetryConnector,RetryConnectorFactory> getBusinessAdministrators() {
        return businessAdministrators;
    }

    public BusinessService<RetryConnector,RetryConnectorFactory> getBusinesses() {
        return businesses;
    }

    public DisableLogService<RetryConnector,RetryConnectorFactory> getDisableLogs() {
        return disabledLogs;
    }

    public PackageCategoryService<RetryConnector,RetryConnectorFactory> getPackageCategories() {
        return packageCategories;
    }

    public ResourceTypeService<RetryConnector,RetryConnectorFactory> getResourceTypes() {
        return resourceTypes;
    }
}
