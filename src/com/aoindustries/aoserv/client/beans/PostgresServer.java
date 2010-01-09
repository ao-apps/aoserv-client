/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class PostgresServer {

    private int aoServerResource;
    private PostgresServerName name;
    private int version;
    private int maxConnections;
    private int netBind;
    private int sortMem;
    private int sharedBuffers;
    private boolean fsync;

    public PostgresServer() {
    }

    public PostgresServer(
        int aoServerResource,
        PostgresServerName name,
        int version,
        int maxConnections,
        int netBind,
        int sortMem,
        int sharedBuffers,
        boolean fsync
    ) {
        this.aoServerResource = aoServerResource;
        this.name = name;
        this.version = version;
        this.maxConnections = maxConnections;
        this.netBind = netBind;
        this.sortMem = sortMem;
        this.sharedBuffers = sharedBuffers;
        this.fsync = fsync;
    }

    public int getAoServerResource() {
        return aoServerResource;
    }

    public void setAoServerResource(int aoServerResource) {
        this.aoServerResource = aoServerResource;
    }

    public PostgresServerName getName() {
        return name;
    }

    public void setName(PostgresServerName name) {
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

    public int getSortMem() {
        return sortMem;
    }

    public void setSortMem(int sortMem) {
        this.sortMem = sortMem;
    }

    public int getSharedBuffers() {
        return sharedBuffers;
    }

    public void setSharedBuffers(int sharedBuffers) {
        this.sharedBuffers = sharedBuffers;
    }

    public boolean isFsync() {
        return fsync;
    }

    public void setFsync(boolean fsync) {
        this.fsync = fsync;
    }
}
