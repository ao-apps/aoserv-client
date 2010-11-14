/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
abstract public class ServerResource extends Resource {

    private int server;
    private int businessServer;

    public ServerResource() {
    }

    public ServerResource(
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
        super(pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled);
        this.server = server;
        this.businessServer = businessServer;
    }

    public int getServer() {
        return server;
    }

    public void setServer(int server) {
        this.server = server;
    }

    public int getBusinessServer() {
        return businessServer;
    }

    public void setBusinessServer(int businessServer) {
        this.businessServer = businessServer;
    }
}
