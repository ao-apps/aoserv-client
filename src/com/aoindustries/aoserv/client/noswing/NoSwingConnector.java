package com.aoindustries.aoserv.client.noswing;

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
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @see NoSwingConnectorFactory
 *
 * @author  AO Industries, Inc.
 */
final public class NoSwingConnector implements AOServConnector<NoSwingConnector,NoSwingConnectorFactory> {

    final NoSwingConnectorFactory factory;
    final AOServConnector<?,?> wrapped;
    final NoSwingBusinessAdministratorService businessAdministrators;
    final NoSwingBusinessService businesses;
    final NoSwingDisableLogService disabledLogs;
    final NoSwingPackageCategoryService packageCategories;
    final NoSwingResourceTypeService resourceTypes;

    NoSwingConnector(NoSwingConnectorFactory factory, AOServConnector<?,?> wrapped) throws RemoteException, LoginException {
        this.factory = factory;
        this.wrapped = wrapped;
        businessAdministrators = new NoSwingBusinessAdministratorService(this, wrapped.getBusinessAdministrators());
        businesses = new NoSwingBusinessService(this, wrapped.getBusinesses());
        disabledLogs = new NoSwingDisableLogService(this, wrapped.getDisableLogs());
        packageCategories = new NoSwingPackageCategoryService(this, wrapped.getPackageCategories());
        resourceTypes = new NoSwingResourceTypeService(this, wrapped.getResourceTypes());
    }

    public NoSwingConnectorFactory getFactory() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return factory;
    }

    public Locale getLocale() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return wrapped.getLocale();
    }

    public void setLocale(Locale locale) throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        wrapped.setLocale(locale);
    }

    public String getConnectAs() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return wrapped.getConnectAs();
    }

    public BusinessAdministrator getThisBusinessAdministrator() throws RemoteException {
        String connectAs = getConnectAs();
        BusinessAdministrator obj = getBusinessAdministrators().get(connectAs);
        if(obj==null) throw new RemoteException("Unable to find BusinessAdministrator: "+connectAs);
        return obj;
    }

    private final AtomicReference<Map<ServiceName,AOServService<NoSwingConnector,NoSwingConnectorFactory,?,?>>> tables = new AtomicReference<Map<ServiceName,AOServService<NoSwingConnector,NoSwingConnectorFactory,?,?>>>();
    public Map<ServiceName,AOServService<NoSwingConnector,NoSwingConnectorFactory,?,?>> getServices() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        Map<ServiceName,AOServService<NoSwingConnector,NoSwingConnectorFactory,?,?>> ts = tables.get();
        if(ts==null) {
            ts = AOServConnectorUtils.createServiceMap(this);
            if(!tables.compareAndSet(null, ts)) ts = tables.get();
        }
        return ts;
    }

    public BusinessAdministratorService<NoSwingConnector,NoSwingConnectorFactory> getBusinessAdministrators() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return businessAdministrators;
    }

    public BusinessService<NoSwingConnector,NoSwingConnectorFactory> getBusinesses() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return businesses;
    }

    public DisableLogService<NoSwingConnector,NoSwingConnectorFactory> getDisableLogs() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return disabledLogs;
    }

    public PackageCategoryService<NoSwingConnector,NoSwingConnectorFactory> getPackageCategories() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return packageCategories;
    }

    public ResourceTypeService<NoSwingConnector,NoSwingConnectorFactory> getResourceTypes() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return resourceTypes;
    }
}
