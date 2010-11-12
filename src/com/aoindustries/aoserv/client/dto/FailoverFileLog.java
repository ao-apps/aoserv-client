/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

import java.util.Date;

/**
 * @author  AO Industries, Inc.
 */
public class FailoverFileLog {

    private int pkey;
    private int replication;
    private long startTime;
    private long endTime;
    private int scanned;
    private int updated;
    private long bytes;
    private boolean isSuccessful;

    public FailoverFileLog() {
    }

    public FailoverFileLog(
        int pkey,
        int replication,
        long startTime,
        long endTime,
        int scanned,
        int updated,
        long bytes,
        boolean isSuccessful
    ) {
        this.pkey = pkey;
        this.replication = replication;
        this.startTime = startTime;
        this.endTime = endTime;
        this.scanned = scanned;
        this.updated = updated;
        this.bytes = bytes;
        this.isSuccessful = isSuccessful;
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

    public Date getStartTime() {
        return new Date(startTime);
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime.getTime();
    }

    public Date getEndTime() {
        return new Date(endTime);
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime.getTime();
    }

    public int getScanned() {
        return scanned;
    }

    public void setScanned(int scanned) {
        this.scanned = scanned;
    }

    public int getUpdated() {
        return updated;
    }

    public void setUpdated(int updated) {
        this.updated = updated;
    }

    public long getBytes() {
        return bytes;
    }

    public void setBytes(long bytes) {
        this.bytes = bytes;
    }

    public boolean isIsSuccessful() {
        return isSuccessful;
    }

    public void setIsSuccessful(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }
}
