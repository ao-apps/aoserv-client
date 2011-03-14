/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.table.IndexType;
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

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = 8351229155128688341L;

    final int server;
    final int businessServer;

    protected ServerResource(
        AOServConnector connector,
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
    public static final MethodColumn COLUMN_SERVER = getMethodColumn(ServerResource.class, "server");
    @DependencySingleton
    @SchemaColumn(order=RESOURCE_LAST_COLUMN+1, index=IndexType.INDEXED, description="the server that this resource is on")
    public Server getServer() throws RemoteException {
        return getConnector().getServers().get(server);
    }

    public static final MethodColumn COLUMN_BUSINESS_SERVER = getMethodColumn(ServerResource.class, "businessServer");
    @DependencySingleton
    @SchemaColumn(order=RESOURCE_LAST_COLUMN+2, index=IndexType.INDEXED, description="the business server that this resource depends on")
    public BusinessServer getBusinessServer() throws RemoteException {
        return getConnector().getBusinessServers().get(businessServer);
    }
    static final int SERVER_RESOURCE_LAST_COLUMN = RESOURCE_LAST_COLUMN+2;
    // </editor-fold>
}
