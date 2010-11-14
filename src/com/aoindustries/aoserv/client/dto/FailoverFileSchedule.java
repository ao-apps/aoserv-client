/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class FailoverFileSchedule extends AOServObject {

    private int pkey;
    private int replication;
    private short hour;
    private short minute;
    private boolean enabled;

    public FailoverFileSchedule() {
    }

    public FailoverFileSchedule(
        int pkey,
        int replication,
        short hour,
        short minute,
        boolean enabled
    ) {
        this.pkey = pkey;
        this.replication = replication;
        this.hour = hour;
        this.minute = minute;
        this.enabled = enabled;
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

    public short getHour() {
        return hour;
    }

    public void setHour(short hour) {
        this.hour = hour;
    }

    public short getMinute() {
        return minute;
    }

    public void setMinute(short minute) {
        this.minute = minute;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
