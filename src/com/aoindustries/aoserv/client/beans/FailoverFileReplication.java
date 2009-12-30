/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class FailoverFileReplication {

    private int pkey;
    private int server;
    private int backupPartition;
    private int maxBitRate;
    private boolean useCompression;
    private short retention;
    private String connectAddress;
    private String connectFrom;
    private boolean enabled;
    private Integer quotaGid;

    public FailoverFileReplication() {
    }

    public FailoverFileReplication(
        int pkey,
        int server,
        int backupPartition,
        int maxBitRate,
        boolean useCompression,
        short retention,
        String connectAddress,
        String connectFrom,
        boolean enabled,
        Integer quotaGid
    ) {
        this.pkey = pkey;
        this.server = server;
        this.backupPartition = backupPartition;
        this.maxBitRate = maxBitRate;
        this.useCompression = useCompression;
        this.retention = retention;
        this.connectAddress = connectAddress;
        this.connectFrom = connectFrom;
        this.enabled = enabled;
        this.quotaGid = quotaGid;
    }

    public int getPkey() {
        return pkey;
    }

    public void setPkey(int pkey) {
        this.pkey = pkey;
    }

    public int getServer() {
        return server;
    }

    public void setServer(int server) {
        this.server = server;
    }

    public int getBackupPartition() {
        return backupPartition;
    }

    public void setBackupPartition(int backupPartition) {
        this.backupPartition = backupPartition;
    }

    public int getMaxBitRate() {
        return maxBitRate;
    }

    public void setMaxBitRate(int maxBitRate) {
        this.maxBitRate = maxBitRate;
    }

    public boolean isUseCompression() {
        return useCompression;
    }

    public void setUseCompression(boolean useCompression) {
        this.useCompression = useCompression;
    }

    public short getRetention() {
        return retention;
    }

    public void setRetention(short retention) {
        this.retention = retention;
    }

    public String getConnectAddress() {
        return connectAddress;
    }

    public void setConnectAddress(String connectAddress) {
        this.connectAddress = connectAddress;
    }

    public String getConnectFrom() {
        return connectFrom;
    }

    public void setConnectFrom(String connectFrom) {
        this.connectFrom = connectFrom;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getQuotaGid() {
        return quotaGid;
    }

    public void setQuotaGid(Integer quotaGid) {
        this.quotaGid = quotaGid;
    }
}