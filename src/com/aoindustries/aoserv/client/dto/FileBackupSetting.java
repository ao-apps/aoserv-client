/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class FileBackupSetting {

    private int pkey;
    private int replication;
    private String path;
    private boolean backupEnabled;

    public FileBackupSetting() {
    }

    public FileBackupSetting(
        int pkey,
        int replication,
        String path,
        boolean backupEnabled
    ) {
        this.pkey = pkey;
        this.replication = replication;
        this.path = path;
        this.backupEnabled = backupEnabled;
    }

    public int getPkey() {
        return pkey;
    }

    public void setPkey(int pkey) {
        this.pkey = pkey;
    }

    public int getReplication() {
        return replication;
    }

    public void setReplication(int replication) {
        this.replication = replication;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isBackupEnabled() {
        return backupEnabled;
    }

    public void setBackupEnabled(boolean backupEnabled) {
        this.backupEnabled = backupEnabled;
    }
}
