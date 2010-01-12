/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class AOServerDaemonHost {

    private int pkey;
    private int aoServer;
    private Hostname host;

    public AOServerDaemonHost() {
    }

    public AOServerDaemonHost(
        int pkey,
        int aoServer,
        Hostname host
    ) {
        this.pkey = pkey;
        this.aoServer = aoServer;
        this.host = host;
    }

    public int getPkey() {
        return pkey;
    }

    public void setPkey(int pkey) {
        this.pkey = pkey;
    }

    public int getAoServer() {
        return aoServer;
    }

    public void setAoServer(int aoServer) {
        this.aoServer = aoServer;
    }

    public Hostname getHost() {
        return host;
    }

    public void setHost(Hostname host) {
        this.host = host;
    }
}
