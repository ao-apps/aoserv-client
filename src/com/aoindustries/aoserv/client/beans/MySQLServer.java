/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class MySQLServer {

    private int aoServerResource;
    private MySQLServerName name;
    private int version;
    private int maxConnections;
    private int netBind;

    public MySQLServer() {
    }

    public MySQLServer(int aoServerResource, MySQLServerName name, int version, int maxConnections, int netBind) {
        this.aoServerResource = aoServerResource;
        this.name = name;
        this.version = version;
        this.maxConnections = maxConnections;
        this.netBind = netBind;
    }

    public int getAoServerResource() {
        return aoServerResource;
    }

    public void setAoServerResource(int aoServerResource) {
        this.aoServerResource = aoServerResource;
    }

    public MySQLServerName getName() {
        return name;
    }

    public void setName(MySQLServerName name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getNetBind() {
        return netBind;
    }

    public void setNetBind(int netBind) {
        this.netBind = netBind;
    }
}
