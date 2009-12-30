/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

import java.util.Date;

/**
 * @author  AO Industries, Inc.
 */
public class AOServer {

    private int server;
    private String hostname;
    private Integer daemonBind;
    private String daemonKey;
    private int poolSize;
    private int distroHour;
    private Date lastDistroTime;
    private Integer failoverServer;
    private String daemonDeviceId;
    private Integer daemonConnectBind;
    private String timeZone;
    private Integer jilterBind;
    private boolean restrictOutboundEmail;
    private String daemonConnectAddress;
    private int failoverBatchSize;
    private Float monitoringLoadLow;
    private Float monitoringLoadMedium;
    private Float monitoringLoadHigh;
    private Float monitoringLoadCritical;

    public AOServer() {
    }

    public AOServer(
        int server,
        String hostname,
        Integer daemonBind,
        String daemonKey,
        int poolSize,
        int distroHour,
        Date lastDistroTime,
        Integer failoverServer,
        String daemonDeviceId,
        Integer daemonConnectBind,
        String timeZone,
        Integer jilterBind,
        boolean restrictOutboundEmail,
        String daemonConnectAddress,
        int failoverBatchSize,
        Float monitoringLoadLow,
        Float monitoringLoadMedium,
        Float monitoringLoadHigh,
        Float monitoringLoadCritical
    ) {
        this.server = server;
        this.hostname = hostname;
        this.daemonBind = daemonBind;
        this.daemonKey = daemonKey;
        this.poolSize = poolSize;
        this.distroHour = distroHour;
        this.lastDistroTime = lastDistroTime;
        this.failoverServer = failoverServer;
        this.daemonDeviceId = daemonDeviceId;
        this.daemonConnectBind = daemonConnectBind;
        this.timeZone = timeZone;
        this.jilterBind = jilterBind;
        this.restrictOutboundEmail = restrictOutboundEmail;
        this.daemonConnectAddress = daemonConnectAddress;
        this.failoverBatchSize = failoverBatchSize;
        this.monitoringLoadLow = monitoringLoadLow;
        this.monitoringLoadMedium = monitoringLoadMedium;
        this.monitoringLoadHigh = monitoringLoadHigh;
        this.monitoringLoadCritical = monitoringLoadCritical;
    }

    public int getServer() {
        return server;
    }

    public void setServer(int server) {
        this.server = server;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getDaemonBind() {
        return daemonBind;
    }

    public void setDaemonBind(Integer daemonBind) {
        this.daemonBind = daemonBind;
    }

    public String getDaemonKey() {
        return daemonKey;
    }

    public void setDaemonKey(String daemonKey) {
        this.daemonKey = daemonKey;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public int getDistroHour() {
        return distroHour;
    }

    public void setDistroHour(int distroHour) {
        this.distroHour = distroHour;
    }

    public Date getLastDistroTime() {
        return lastDistroTime;
    }

    public void setLastDistroTime(Date lastDistroTime) {
        this.lastDistroTime = lastDistroTime;
    }

    public Integer getFailoverServer() {
        return failoverServer;
    }

    public void setFailoverServer(Integer failoverServer) {
        this.failoverServer = failoverServer;
    }

    public String getDaemonDeviceId() {
        return daemonDeviceId;
    }

    public void setDaemonDeviceId(String daemonDeviceId) {
        this.daemonDeviceId = daemonDeviceId;
    }

    public Integer getDaemonConnectBind() {
        return daemonConnectBind;
    }

    public void setDaemonConnectBind(Integer daemonConnectBind) {
        this.daemonConnectBind = daemonConnectBind;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Integer getJilterBind() {
        return jilterBind;
    }

    public void setJilterBind(Integer jilterBind) {
        this.jilterBind = jilterBind;
    }

    public boolean isRestrictOutboundEmail() {
        return restrictOutboundEmail;
    }

    public void setRestrictOutboundEmail(boolean restrictOutboundEmail) {
        this.restrictOutboundEmail = restrictOutboundEmail;
    }

    public String getDaemonConnectAddress() {
        return daemonConnectAddress;
    }

    public void setDaemonConnectAddress(String daemonConnectAddress) {
        this.daemonConnectAddress = daemonConnectAddress;
    }

    public int getFailoverBatchSize() {
        return failoverBatchSize;
    }

    public void setFailoverBatchSize(int failoverBatchSize) {
        this.failoverBatchSize = failoverBatchSize;
    }

    public Float getMonitoringLoadLow() {
        return monitoringLoadLow;
    }

    public void setMonitoringLoadLow(Float monitoringLoadLow) {
        this.monitoringLoadLow = monitoringLoadLow;
    }

    public Float getMonitoringLoadMedium() {
        return monitoringLoadMedium;
    }

    public void setMonitoringLoadMedium(Float monitoringLoadMedium) {
        this.monitoringLoadMedium = monitoringLoadMedium;
    }

    public Float getMonitoringLoadHigh() {
        return monitoringLoadHigh;
    }

    public void setMonitoringLoadHigh(Float monitoringLoadHigh) {
        this.monitoringLoadHigh = monitoringLoadHigh;
    }

    public Float getMonitoringLoadCritical() {
        return monitoringLoadCritical;
    }

    public void setMonitoringLoadCritical(Float monitoringLoadCritical) {
        this.monitoringLoadCritical = monitoringLoadCritical;
    }
}
