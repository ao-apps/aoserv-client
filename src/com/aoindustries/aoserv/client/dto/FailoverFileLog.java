/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

import java.util.Calendar;

/**
 * @author  AO Industries, Inc.
 */
public class FailoverFileLog extends AOServObject {

    private int pkey;
    private int replication;
    private long startTime;
    private long endTime;
    private int scanned;
    private int updated;
    private long bytes;
    private boolean successful;

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
        boolean successful
    ) {
        this.pkey = pkey;
        this.replication = replication;
        this.startTime = startTime;
        this.endTime = endTime;
        this.scanned = scanned;
        this.updated = updated;
        this.bytes = bytes;
        this.successful = successful;
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

    public Calendar getStartTime() {
        return DtoUtils.getCalendar(startTime);
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime.getTimeInMillis();
    }

    public Calendar getEndTime() {
        return DtoUtils.getCalendar(endTime);
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime.getTimeInMillis();
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

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
}
