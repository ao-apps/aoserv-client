/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class FailoverMySQLReplication {

    private int pkey;
    private Integer aoServer;
    private Integer replication;
    private int mysqlServer;
    private int monitoringSecondsBehindLow;
    private int monitoringSecondsBehindMedium;
    private int monitoringSecondsBehindHigh;
    private int monitoringSecondsBehindCritical;

    public FailoverMySQLReplication() {
    }

    public FailoverMySQLReplication(
        int pkey,
        Integer aoServer,
        Integer replication,
        int mysqlServer,
        int monitoringSecondsNehindLow,
        int monitoringSecondsBehindMedium,
        int monitoringSecondsBehindHigh,
        int monitoringSecondsBehindCritical
    ) {
        this.pkey = pkey;
        this.aoServer = aoServer;
        this.replication = replication;
        this.mysqlServer = mysqlServer;
        this.monitoringSecondsBehindLow = monitoringSecondsNehindLow;
        this.monitoringSecondsBehindMedium = monitoringSecondsBehindMedium;
        this.monitoringSecondsBehindHigh = monitoringSecondsBehindHigh;
        this.monitoringSecondsBehindCritical = monitoringSecondsBehindCritical;
    }

    public int getPkey() {
        return pkey;
    }

    public void setPkey(int pkey) {
        this.pkey = pkey;
    }

    public Integer getAoServer() {
        return aoServer;
    }

    public void setAoServer(Integer aoServer) {
        this.aoServer = aoServer;
    }

    public Integer getReplication() {
        return replication;
    }

    public void setReplication(Integer replication) {
        this.replication = replication;
    }

    public int getMysqlServer() {
        return mysqlServer;
    }

    public void setMysqlServer(int mysqlServer) {
        this.mysqlServer = mysqlServer;
    }

    public int getMonitoringSecondsBehindLow() {
        return monitoringSecondsBehindLow;
    }

    public void setMonitoringSecondsBehindLow(int monitoringSecondsBehindLow) {
        this.monitoringSecondsBehindLow = monitoringSecondsBehindLow;
    }

    public int getMonitoringSecondsBehindMedium() {
        return monitoringSecondsBehindMedium;
    }

    public void setMonitoringSecondsBehindMedium(int monitoringSecondsBehindMedium) {
        this.monitoringSecondsBehindMedium = monitoringSecondsBehindMedium;
    }

    public int getMonitoringSecondsBehindHigh() {
        return monitoringSecondsBehindHigh;
    }

    public void setMonitoringSecondsBehindHigh(int monitoringSecondsBehindHigh) {
        this.monitoringSecondsBehindHigh = monitoringSecondsBehindHigh;
    }

    public int getMonitoringSecondsBehindCritical() {
        return monitoringSecondsBehindCritical;
    }

    public void setMonitoringSecondsBehindCritical(int monitoringSecondsBehindCritical) {
        this.monitoringSecondsBehindCritical = monitoringSecondsBehindCritical;
    }
}
