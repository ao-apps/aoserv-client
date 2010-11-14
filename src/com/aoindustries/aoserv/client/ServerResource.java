/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;

/**
 * A <code>Resource</code> that exists on a <code>Server</code>.  All resources on a server must
 * be removed before the related <code>BusinessServer</code> may be removed.
 *
 * @see  BusinessServer
 *
 * @author  AO Industries, Inc.
 */
public abstract class ServerResource extends Resource {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final int server;
    final int businessServer;

    protected ServerResource(
        AOServConnector<?,?> connector,
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        int server,
        int businessServer
    ) {
        super(connector, pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled);
        this.server = server;
        this.businessServer = businessServer;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    /**
     * Gets the resource that this represents.
     */
    static final String COLUMN_SERVER = "server";
    @SchemaColumn(order=RESOURCE_LAST_COLUMN+1, name=COLUMN_SERVER, index=IndexType.INDEXED, description="the server that this resource is on")
    public Server getServer() throws RemoteException {
        return getConnector().getServers().get(server);
    }

    static final String COLUMN_BUSINESS_SERVER = "business_server";
    @SchemaColumn(order=RESOURCE_LAST_COLUMN+2, name=COLUMN_BUSINESS_SERVER, index=IndexType.INDEXED, description="the business server that this resource depends on")
    public BusinessServer getBusinessServer() throws RemoteException {
        return getConnector().getBusinessServers().get(businessServer);
    }
    static final int SERVER_RESOURCE_LAST_COLUMN = RESOURCE_LAST_COLUMN+2;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependencies(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getServer());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getBusinessServer());
        return unionSet;
    }
    // </editor-fold>
}
