/*
 * Copyright 2009-2010 by AO Industries, Inc.,
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
    private Long maxBitRate;
    private boolean useCompression;
    private short retention;
    private InetAddress connectAddress;
    private InetAddress connectFrom;
    private boolean enabled;
    private LinuxID quotaGid;

    public FailoverFileReplication() {
    }

    public FailoverFileReplication(
        int pkey,
        int server,
        int backupPartition,
        Long maxBitRate,
        boolean useCompression,
        short retention,
        InetAddress connectAddress,
        InetAddress connectFrom,
        boolean enabled,
        LinuxID quotaGid
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

    public Long getMaxBitRate() {
        return maxBitRate;
    }

    public void setMaxBitRate(Long maxBitRate) {
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

    public InetAddress getConnectAddress() {
        return connectAddress;
    }

    public void setConnectAddress(InetAddress connectAddress) {
        this.connectAddress = connectAddress;
    }

    public InetAddress getConnectFrom() {
        return connectFrom;
    }

    public void setConnectFrom(InetAddress connectFrom) {
        this.connectFrom = connectFrom;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LinuxID getQuotaGid() {
        return quotaGid;
    }

    public void setQuotaGid(LinuxID quotaGid) {
        this.quotaGid = quotaGid;
    }
}
