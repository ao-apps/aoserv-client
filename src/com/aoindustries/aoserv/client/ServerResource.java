/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * A <code>Resource</code> that exists on a <code>Server</code>.  All resources on a server must
 * be removed before the related <code>BusinessServer</code> may be removed.
 *
 * @see  BusinessServer
 *
 * @author  AO Industries, Inc.
 */
final public class ServerResource extends AOServObjectIntegerKey<ServerResource> implements BeanFactory<com.aoindustries.aoserv.client.beans.ServerResource> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final int server;
    final int businessServer;

    public ServerResource(ServerResourceService<?,?> service, int resource, int server, int businessServer) {
        super(service, resource);
        this.server = server;
        this.businessServer = businessServer;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(ServerResource other) throws RemoteException {
        return key==other.key ? 0 : getResource().compareTo(other.getResource());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    /**
     * Gets the resource that this represents.
     */
    static final String COLUMN_RESOURCE = "resource";
    @SchemaColumn(order=0, name=COLUMN_RESOURCE, index=IndexType.PRIMARY_KEY, description="the resource id")
    public Resource getResource() throws RemoteException {
        return getService().getConnector().getResources().get(key);
    }

    static final String COLUMN_SERVER = "server";
    @SchemaColumn(order=1, name=COLUMN_SERVER, index=IndexType.INDEXED, description="the server that this resource is on")
    public Server getServer() throws RemoteException {
        return getService().getConnector().getServers().get(server);
    }

    static final String COLUMN_BUSINESS_SERVER = "business_server";
    @SchemaColumn(order=2, name=COLUMN_BUSINESS_SERVER, index=IndexType.INDEXED, description="the business server that this resource depends on")
    public BusinessServer getBusinessServer() throws RemoteException {
        return getService().getConnector().getBusinessServers().get(businessServer);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.ServerResource getBean() {
        return new com.aoindustries.aoserv.client.beans.ServerResource(key, server, businessServer);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getResource(),
            getServer(),
            getBusinessServer()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getDependentObjectByResourceType()
        );
    }

    private AOServObject getDependentObjectByResourceType() throws RemoteException {
        String resourceType = getResource().resourceType;
        AOServObject obj;
        if(resourceType.equals(ResourceType.IP_ADDRESS)) obj = getIpAddress();
        else throw new AssertionError("Unexpected resource type: "+resourceType);
        if(obj==null) throw new RemoteException("Type-specific server resource object not found: "+key);
        return obj;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IPAddress getIpAddress() throws RemoteException {
        return getService().getConnector().getIpAddresses().get(key);
    }
    // </editor-fold>
}
