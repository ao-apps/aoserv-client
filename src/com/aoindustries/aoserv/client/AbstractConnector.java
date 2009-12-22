package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.security.LoginException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A base implementation of <code>AOServConnector</code>.
 *
 * @author  AO Industries, Inc.
 */
abstract public class AbstractConnector<C extends AbstractConnector<C,F>, F extends AbstractConnectorFactory<C,F>> implements AOServConnector<C,F> {

    protected final F factory;
    protected final UUID connectorId;
    protected Locale locale;
    protected final String connectAs;
    protected final String authenticateAs;
    protected final String password;
    protected final String daemonServer;

    protected AbstractConnector(F factory, UUID connectorId, Locale locale, String connectAs, String authenticateAs, String password, String daemonServer) throws RemoteException, NotBoundException, LoginException {
        this.factory = factory;
        this.connectorId = connectorId;
        this.locale = locale;
        this.connectAs = connectAs;
        this.authenticateAs = authenticateAs;
        this.password = password;
        this.daemonServer = daemonServer;
    }

    final public F getFactory() {
        return factory;
    }

    final public UUID getConnectorId() {
        return connectorId;
    }

    final public Locale getLocale() {
        return locale;
    }

    final public void setLocale(Locale locale) {
        this.locale = locale;
    }

    final public String getConnectAs() {
        return connectAs;
    }

    final public BusinessAdministrator getThisBusinessAdministrator() throws RemoteException {
        BusinessAdministrator obj = getBusinessAdministrators().get(connectAs);
        if(obj==null) throw new RemoteException("Unable to find BusinessAdministrator: "+connectAs);
        return obj;
    }

    private final AtomicReference<Map<ServiceName,AOServService<C,F,?,?>>> tables = new AtomicReference<Map<ServiceName,AOServService<C,F,?,?>>>();
    private void addTable(Map<ServiceName,AOServService<C,F,?,?>> ts, AOServService<C,F,?,?> table) throws RemoteException {
        ServiceName tableName = table.getServiceName();
        if(ts.put(tableName, table)!=null) throw new AssertionError("Table found more than once: "+tableName);
    }
    final public Map<ServiceName,AOServService<C,F,?,?>> getServices() throws RemoteException {
        Map<ServiceName,AOServService<C,F,?,?>> ts = tables.get();
        if(ts==null) {
            ts = new EnumMap<ServiceName,AOServService<C,F,?,?>>(ServiceName.class);
            addTable(ts, getBusinessAdministrators());
            addTable(ts, getDisableLogs());
            addTable(ts, getBusinesses());
            addTable(ts, getPackageCategories());
            addTable(ts, getResourceTypes());
            // Make sure every table has been added
            for(ServiceName tableName : ServiceName.values) {
                if(!ts.containsKey(tableName)) throw new AssertionError("Table not found: "+tableName);
            }
            ts = Collections.unmodifiableMap(ts);
            if(!tables.compareAndSet(null, ts)) ts = tables.get();
        }
        return ts;
    }
}
