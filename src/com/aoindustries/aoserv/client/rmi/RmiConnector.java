package com.aoindustries.aoserv.client.rmi;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServConnectorFactory;
import com.aoindustries.aoserv.client.AOServService;
import com.aoindustries.aoserv.client.AbstractConnector;
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
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation of <code>AOServConnector</code> that connects to the master over RMI server.
 *
 * @author  AO Industries, Inc.
 */
final public class RmiConnector extends AbstractConnector<RmiConnector,RmiConnectorFactory> {

    private static final Logger logger = Logger.getLogger(RmiConnector.class.getName());

    final RmiBusinessAdministratorService businessAdministratorsRmi;
    final RmiBusinessService businessesRmi;
    final RmiDisableLogService disabledLogsRmi;
    final RmiPackageCategoryService packageCategoriesRmi;
    final RmiResourceTypeService resourceTypesRmi;

    final Object connectionLock = new Object();
    private AOServConnector<?,?> wrapped;

    RmiConnector(RmiConnectorFactory factory, UUID connectorId, Locale locale, String connectAs, String authenticateAs, String password, String daemonServer) throws RemoteException, NotBoundException, LoginException {
        super(factory, connectorId, locale, connectAs, authenticateAs, password, daemonServer);
        businessAdministratorsRmi = new RmiBusinessAdministratorService(this);
        businessesRmi = new RmiBusinessService(this);
        disabledLogsRmi = new RmiDisableLogService(this);
        packageCategoriesRmi = new RmiPackageCategoryService(this);
        resourceTypesRmi = new RmiResourceTypeService(this);
        // Connect immediately in order to have the chance to throw exceptions that will occur during connection
        synchronized(connectionLock) {
            connect();
        }
    }

    @Override
    public void setLocale(Locale locale) throws RemoteException {
        wrapped.setLocale(locale);
        super.setLocale(locale);
    }

    @SuppressWarnings("unchecked")
    void connect() throws RemoteException, NotBoundException, LoginException {
        assert Thread.holdsLock(connectionLock);
        // Connect to the remote registry and get each of the stubs
        Registry remoteRegistry = LocateRegistry.getRegistry(factory.serverAddress, factory.serverPort, factory.csf);
        AOServConnectorFactory<?,?> serverFactory = (AOServConnectorFactory)remoteRegistry.lookup(AOServConnectorFactory.class.getName()+"_Stub");
        AOServConnector<?,?> newWrapped = serverFactory.getConnector(connectorId, locale, connectAs, authenticateAs, password, daemonServer);

        // Now that each stub has been successfully received, store as the current connection
        this.wrapped = newWrapped;
        for(ServiceName serviceName : ServiceName.values) {
            ((RmiService)getServices().get(serviceName)).wrapped = newWrapped.getServices().get(serviceName);
        }
    }

    /**
     * Disconnects if appropriate for the provided type of RemoteException.
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
            for(AOServService<RmiConnector,RmiConnectorFactory,?,?> service : getServices().values()) {
                ((RmiService<?,?>)service).wrapped = null;
            }
        }
    }

    @Override
    public BusinessAdministratorService<RmiConnector,RmiConnectorFactory> getBusinessAdministrators() {
        return businessAdministratorsRmi;
    }

    @Override
    public BusinessService<RmiConnector,RmiConnectorFactory> getBusinesses() {
        return businessesRmi;
    }

    @Override
    public DisableLogService<RmiConnector,RmiConnectorFactory> getDisableLogs() {
        return disabledLogsRmi;
    }

    @Override
    public PackageCategoryService<RmiConnector,RmiConnectorFactory> getPackageCategories() {
        return packageCategoriesRmi;
    }

    @Override
    public ResourceTypeService<RmiConnector,RmiConnectorFactory> getResourceTypes() {
        return resourceTypesRmi;
    }
}
