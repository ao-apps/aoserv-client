package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AbstractConnector;
import com.aoindustries.aoserv.client.BusinessAdministratorService;
import com.aoindustries.aoserv.client.BusinessService;
import com.aoindustries.aoserv.client.DisableLogService;
import com.aoindustries.aoserv.client.PackageCategoryService;
import com.aoindustries.aoserv.client.ResourceTypeService;
import com.aoindustries.security.LoginException;
import java.rmi.RemoteException;
import java.util.Locale;

/**
 * An implementation of <code>AOServConnector</code> that transfers entire
 * tables as a set and performs local lookups.
 *
 * @author  AO Industries, Inc.
 */
final public class CachedConnector extends AbstractConnector<CachedConnector,CachedConnectorFactory> {

    final AOServConnector<?,?> wrapped;
    final CachedBusinessAdministratorService businessAdministrators;
    final CachedBusinessService businesses;
    final CachedDisableLogService disabledLogs;
    final CachedPackageCategoryService packageCategories;
    final CachedResourceTypeService resourceTypes;

    CachedConnector(CachedConnectorFactory factory, AOServConnector<?,?> wrapped, String authenticateAs, String password, String daemonServer) throws RemoteException, LoginException {
        super(factory, wrapped.getConnectorId(), wrapped.getLocale(), wrapped.getConnectAs(), authenticateAs, password, daemonServer);
        this.wrapped = wrapped;
        businessAdministrators = new CachedBusinessAdministratorService(this, wrapped.getBusinessAdministrators());
        businesses = new CachedBusinessService(this, wrapped.getBusinesses());
        disabledLogs = new CachedDisableLogService(this, wrapped.getDisableLogs());
        packageCategories = new CachedPackageCategoryService(this, wrapped.getPackageCategories());
        resourceTypes = new CachedResourceTypeService(this, wrapped.getResourceTypes());
    }

    @Override
    public void setLocale(Locale locale) throws RemoteException {
        wrapped.setLocale(locale);
        super.setLocale(locale);
    }

    @Override
    public BusinessAdministratorService<CachedConnector,CachedConnectorFactory> getBusinessAdministrators() {
        return businessAdministrators;
    }

    @Override
    public BusinessService<CachedConnector,CachedConnectorFactory> getBusinesses() {
        return businesses;
    }

    @Override
    public DisableLogService<CachedConnector,CachedConnectorFactory> getDisableLogs() {
        return disabledLogs;
    }

    @Override
    public PackageCategoryService<CachedConnector,CachedConnectorFactory> getPackageCategories() {
        return packageCategories;
    }

    @Override
    public ResourceTypeService<CachedConnector,CachedConnectorFactory> getResourceTypes() {
        return resourceTypes;
    }
}
