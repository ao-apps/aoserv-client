package com.aoindustries.aoserv.client.cache;

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
 * An implementation of <code>AOServConnector</code> that transfers entire
 * tables as a set and performs local lookups.
 *
 * @author  AO Industries, Inc.
 */
final public class CachedConnector implements AOServConnector<CachedConnector,CachedConnectorFactory> {

    final CachedConnectorFactory factory;
    final AOServConnector<?,?> wrapped;
    Locale locale;
    final String connectAs;
    final CachedBusinessAdministratorService businessAdministrators;
    final CachedBusinessService businesses;
    final CachedDisableLogService disabledLogs;
    final CachedPackageCategoryService packageCategories;
    final CachedResourceTypeService resourceTypes;

    CachedConnector(CachedConnectorFactory factory, AOServConnector<?,?> wrapped) throws RemoteException, LoginException {
        this.factory = factory;
        this.wrapped = wrapped;
        locale = wrapped.getLocale();
        connectAs = wrapped.getConnectAs();
        businessAdministrators = new CachedBusinessAdministratorService(this, wrapped.getBusinessAdministrators());
        businesses = new CachedBusinessService(this, wrapped.getBusinesses());
        disabledLogs = new CachedDisableLogService(this, wrapped.getDisableLogs());
        packageCategories = new CachedPackageCategoryService(this, wrapped.getPackageCategories());
        resourceTypes = new CachedResourceTypeService(this, wrapped.getResourceTypes());
    }

    public CachedConnectorFactory getFactory() {
        return factory;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) throws RemoteException {
        if(!this.locale.equals(locale)) {
            wrapped.setLocale(locale);
            this.locale = locale;
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

    private final AtomicReference<Map<ServiceName,AOServService<CachedConnector,CachedConnectorFactory,?,?>>> tables = new AtomicReference<Map<ServiceName,AOServService<CachedConnector,CachedConnectorFactory,?,?>>>();
    final public Map<ServiceName,AOServService<CachedConnector,CachedConnectorFactory,?,?>> getServices() throws RemoteException {
        Map<ServiceName,AOServService<CachedConnector,CachedConnectorFactory,?,?>> ts = tables.get();
        if(ts==null) {
            ts = AOServConnectorUtils.createServiceMap(this);
            if(!tables.compareAndSet(null, ts)) ts = tables.get();
        }
        return ts;
    }

    public BusinessAdministratorService<CachedConnector,CachedConnectorFactory> getBusinessAdministrators() {
        return businessAdministrators;
    }

    public BusinessService<CachedConnector,CachedConnectorFactory> getBusinesses() {
        return businesses;
    }

    public DisableLogService<CachedConnector,CachedConnectorFactory> getDisableLogs() {
        return disabledLogs;
    }

    public PackageCategoryService<CachedConnector,CachedConnectorFactory> getPackageCategories() {
        return packageCategories;
    }

    public ResourceTypeService<CachedConnector,CachedConnectorFactory> getResourceTypes() {
        return resourceTypes;
    }
}
