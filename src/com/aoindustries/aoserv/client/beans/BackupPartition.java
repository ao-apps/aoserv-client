/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class BackupPartition {

    private int pkey;
    private int aoServer;
    private UnixPath path;
    private boolean enabled;
    private boolean quotaEnabled;

    public BackupPartition() {
    }

    public BackupPartition(
        int pkey,
        int aoServer,
        UnixPath path,
        boolean enabled,
        boolean quotaEnabled
    ) {
        this.pkey = pkey;
        this.aoServer = aoServer;
        this.path = path;
        this.enabled = enabled;
        this.quotaEnabled = quotaEnabled;
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

    public UnixPath getPath() {
        return path;
    }

    public void setPath(UnixPath path) {
        this.path = path;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isQuotaEnabled() {
        return quotaEnabled;
    }

    public void setQuotaEnabled(boolean quotaEnabled) {
        this.quotaEnabled = quotaEnabled;
    }
}
