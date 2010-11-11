/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class DaemonAccess {

    private String protocol;
    private Hostname host;
    private NetPort port;
    private long key;

    public DaemonAccess() {
    }

    public DaemonAccess(String protocol, Hostname host, NetPort port, long key) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.key = key;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Hostname getHost() {
        return host;
    }

    public void setHost(Hostname host) {
        this.host = host;
    }

    public NetPort getPort() {
        return port;
    }

    public void setPort(NetPort port) {
        this.port = port;
    }

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }
}
