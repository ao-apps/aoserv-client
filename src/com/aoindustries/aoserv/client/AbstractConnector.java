/*
 * Copyright 2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.DomainName;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.util.graph.Edge;
import com.aoindustries.util.graph.SymmetricGraph;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A base implementation of AOServConnector to avoid repetative implementation details.
 *
 * @author  AO Industries, Inc.
 */
abstract public class AbstractConnector implements AOServConnector {

    private Locale locale;
    private final UserId connectAs;
    private final UserId authenticateAs;
    private final String password;
    private final DomainName daemonServer;

    protected AbstractConnector(Locale locale, UserId connectAs, UserId authenticateAs, String password, DomainName daemonServer) {
        this.locale = locale;
        this.connectAs = connectAs;
        this.authenticateAs = authenticateAs;
        this.password = password;
        this.daemonServer = daemonServer;
    }

    /**
     * Defaults to <code>true</code>.
     */
    @Override
    public boolean isAoServObjectConnectorSettable() throws RemoteException {
        return true;
    }

    @Override
    final public Locale getLocale() {
        return locale;
    }

    @Override
    public void setLocale(Locale locale) throws RemoteException {
        this.locale = locale;
    }

    @Override
    final public UserId getConnectAs() {
        return connectAs;
    }

    @Override
    final public BusinessAdministrator getThisBusinessAdministrator() throws RemoteException {
        return getBusinessAdministrators().get(connectAs);
    }

    @Override
    final public UserId getAuthenticateAs() {
        return authenticateAs;
    }

    @Override
    final public String getPassword() {
        return password;
    }

    @Override
    final public DomainName getDaemonServer() {
        return daemonServer;
    }

    private final AtomicReference<Map<ServiceName,AOServService<?,?>>> tables = new AtomicReference<Map<ServiceName,AOServService<?,?>>>();
    @Override
    final public Map<ServiceName,AOServService<?,?>> getServices() throws RemoteException {
        Map<ServiceName,AOServService<?,?>> ts = tables.get();
        if(ts==null) {
            ts = AOServConnectorUtils.createServiceMap(this);
            if(!tables.compareAndSet(null, ts)) ts = tables.get();
        }
        return ts;
    }

    private final AtomicReference<SymmetricGraph<AOServObject<?>,Edge<AOServObject<?>>,RemoteException>> dependencyGraph = new AtomicReference<SymmetricGraph<AOServObject<?>,Edge<AOServObject<?>>,RemoteException>>();
    @Override
    final public SymmetricGraph<AOServObject<?>,Edge<AOServObject<?>>,RemoteException> getDependencyGraph() {
        SymmetricGraph<AOServObject<?>,Edge<AOServObject<?>>,RemoteException> graph = dependencyGraph.get();
        if(graph==null) {
            graph = AOServConnectorUtils.createDependencyMap(this);
            if(!dependencyGraph.compareAndSet(null, graph)) graph = dependencyGraph.get();
        }
        return graph;
    }

    // <editor-fold defaultstate="collapsed" desc="AOServerResourceService">
    final AOServerResourceService aoserverResources = new AOServerResourceService(this);
    @Override
    final public AOServerResourceService getAoServerResources() {
        return aoserverResources;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ResourceService">
    final ResourceService resources = new ResourceService(this);
    @Override
    final public ResourceService getResources() {
        return resources;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ServerResourceService">
    final ServerResourceService serverResources = new ServerResourceService(this);
    @Override
    final public ServerResourceService getServerResources() {
        return serverResources;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ServerService">
    final ServerService servers = new ServerService(this);
    @Override
    final public ServerService getServers() {
        return servers;
    }
    // </editor-fold>
}
