/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
abstract public class AOServerResource extends Resource {

    private int aoServer;
    private int businessServer;

    public AOServerResource() {
    }

    public AOServerResource(
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
        super(pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled);
        this.aoServer = aoServer;
        this.businessServer = businessServer;
    }

    public int getAoServer() {
        return aoServer;
    }

    public void setAoServer(int aoServer) {
        this.aoServer = aoServer;
    }

    public int getBusinessServer() {
        return businessServer;
    }

    public void setBusinessServer(int businessServer) {
        this.businessServer = businessServer;
    }
}
