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
    private Integer monitoringSecondsBehindLow;
    private Integer monitoringSecondsBehindMedium;
    private Integer monitoringSecondsBehindHigh;
    private Integer monitoringSecondsBehindCritical;

    public FailoverMySQLReplication() {
    }

    public FailoverMySQLReplication(
        int pkey,
        Integer aoServer,
        Integer replication,
        int mysqlServer,
        Integer monitoringSecondsNehindLow,
        Integer monitoringSecondsBehindMedium,
        Integer monitoringSecondsBehindHigh,
        Integer monitoringSecondsBehindCritical
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

    public Integer getMonitoringSecondsBehindLow() {
        return monitoringSecondsBehindLow;
    }

    public void setMonitoringSecondsBehindLow(Integer monitoringSecondsBehindLow) {
        this.monitoringSecondsBehindLow = monitoringSecondsBehindLow;
    }

    public Integer getMonitoringSecondsBehindMedium() {
        return monitoringSecondsBehindMedium;
    }

    public void setMonitoringSecondsBehindMedium(Integer monitoringSecondsBehindMedium) {
        this.monitoringSecondsBehindMedium = monitoringSecondsBehindMedium;
    }

    public Integer getMonitoringSecondsBehindHigh() {
        return monitoringSecondsBehindHigh;
    }

    public void setMonitoringSecondsBehindHigh(Integer monitoringSecondsBehindHigh) {
        this.monitoringSecondsBehindHigh = monitoringSecondsBehindHigh;
    }

    public Integer getMonitoringSecondsBehindCritical() {
        return monitoringSecondsBehindCritical;
    }

    public void setMonitoringSecondsBehindCritical(Integer monitoringSecondsBehindCritical) {
        this.monitoringSecondsBehindCritical = monitoringSecondsBehindCritical;
    }
}
