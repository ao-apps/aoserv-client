/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class AOServerResource {

    private int resource;
    private int aoServer;
    private int businessServer;

    public AOServerResource() {
    }

    public AOServerResource(int resource, int aoServer, int businessServer) {
        this.resource = resource;
        this.aoServer = aoServer;
        this.businessServer = businessServer;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
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
