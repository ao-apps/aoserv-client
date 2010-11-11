/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class ServerResource {

    private int resource;
    private int server;
    private int businessServer;

    public ServerResource() {
    }

    public ServerResource(int resource, int server, int businessServer) {
        this.resource = resource;
        this.server = server;
        this.businessServer = businessServer;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
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
