/*
 * Copyright 2010-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class AOServerDaemonHost extends AOServObject {

    private int pkey;
    private int aoServer;
    private InetAddress host;

    public AOServerDaemonHost() {
    }

    public AOServerDaemonHost(
        int pkey,
        int aoServer,
        InetAddress host
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

    public InetAddress getHost() {
        return host;
    }

    public void setHost(InetAddress host) {
        this.host = host;
    }
}
