/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class PostgresServer extends AOServerResource {

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
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        int aoServer,
        int businessServer,
        PostgresServerName name,
        int version,
        int maxConnections,
        int netBind,
        int sortMem,
        int sharedBuffers,
        boolean fsync
    ) {
        super(pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, aoServer, businessServer);
        this.name = name;
        this.version = version;
        this.maxConnections = maxConnections;
        this.netBind = netBind;
        this.sortMem = sortMem;
        this.sharedBuffers = sharedBuffers;
        this.fsync = fsync;
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
