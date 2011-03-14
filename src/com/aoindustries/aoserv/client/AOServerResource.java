/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;

/**
 * A <code>Resource</code> that exists on an <code>AOServer</code>.  All resources on a server must
 * be removed before the related <code>BusinessServer</code> may be removed.
 *
 * @see  BusinessServer
 *
 * @author  AO Industries, Inc.
 */
public abstract class AOServerResource extends Resource {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    // TODO: private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final int aoServer;
    final int businessServer;

    protected AOServerResource(
        AOServConnector connector,
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        int aoServer,
        int businessServer
    ) {
        super(connector, pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled);
        this.aoServer = aoServer;
        this.businessServer = businessServer;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    /**
     * Gets the resource that this represents.
     */
    public static final MethodColumn COLUMN_AO_SERVER = getMethodColumn(AOServerResource.class, "aoServer");
    @DependencySingleton
    @SchemaColumn(order=RESOURCE_LAST_COLUMN+1, index=IndexType.INDEXED, description="the server that this resource is on")
    public AOServer getAoServer() throws RemoteException {
        return getConnector().getAoServers().get(aoServer);
    }

    public static final MethodColumn COLUMN_BUSINESS_SERVER = getMethodColumn(AOServerResource.class, "businessServer");
    @DependencySingleton
    @SchemaColumn(order=RESOURCE_LAST_COLUMN+2, index=IndexType.INDEXED, description="the business server that this resource depends on")
    public BusinessServer getBusinessServer() throws RemoteException {
        return getConnector().getBusinessServers().get(businessServer);
    }
    static final int AOSERVER_RESOURCE_LAST_COLUMN = RESOURCE_LAST_COLUMN+2;
    // </editor-fold>
}
