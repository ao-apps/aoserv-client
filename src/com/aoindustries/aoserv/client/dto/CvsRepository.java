/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class CvsRepository extends AOServerResource {

    private UnixPath path;
    private int linuxAccountGroup;
    private long mode;

    public CvsRepository() {
    }

    public CvsRepository(
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        int aoServer,
        int businessServer,
        UnixPath path,
        int linuxAccountGroup,
        long mode
    ) {
        super(pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, aoServer, businessServer);
        this.path = path;
        this.linuxAccountGroup = linuxAccountGroup;
        this.mode = mode;
    }

    public UnixPath getPath() {
        return path;
    }

    public void setPath(UnixPath path) {
        this.path = path;
    }

    public int getLinuxAccountGroup() {
        return linuxAccountGroup;
    }

    public void setLinuxAccountGroup(int linuxAccountGroup) {
        this.linuxAccountGroup = linuxAccountGroup;
    }

    public long getMode() {
        return mode;
    }

    public void setMode(long mode) {
        this.mode = mode;
    }
}
