/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class NetBind {

    private int pkey;
    private int businessServer;
    private int ipAddress;
    private NetPort port;
    private String netProtocol;
    private String appProtocol;
    private boolean openFirewall;
    private boolean monitoringEnabled;
    private String monitoringParameters;

    public NetBind() {
    }

    public NetBind(
        int pkey,
        int businessServer,
        int ipAddress,
        NetPort port,
        String netProtocol,
        String appProtocol,
        boolean openFirewall,
        boolean monitoringEnabled,
        String monitoringParameters
    ) {
        this.pkey = pkey;
        this.businessServer = businessServer;
        this.ipAddress = ipAddress;
        this.port = port;
        this.netProtocol = netProtocol;
        this.appProtocol = appProtocol;
        this.openFirewall = openFirewall;
        this.monitoringEnabled = monitoringEnabled;
        this.monitoringParameters = monitoringParameters;
    }

    public int getPkey() {
        return pkey;
    }

    public void setPkey(int pkey) {
        this.pkey = pkey;
    }

    public int getBusinessServer() {
        return businessServer;
    }

    public void setBusinessServer(int businessServer) {
        this.businessServer = businessServer;
    }

    public int getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(int ipAddress) {
        this.ipAddress = ipAddress;
    }

    public NetPort getPort() {
        return port;
    }

    public void setPort(NetPort port) {
        this.port = port;
    }

    public String getNetProtocol() {
        return netProtocol;
    }

    public void setNetProtocol(String netProtocol) {
        this.netProtocol = netProtocol;
    }

    public String getAppProtocol() {
        return appProtocol;
    }

    public void setAppProtocol(String appProtocol) {
        this.appProtocol = appProtocol;
    }

    public boolean isOpenFirewall() {
        return openFirewall;
    }

    public void setOpenFirewall(boolean openFirewall) {
        this.openFirewall = openFirewall;
    }

    public boolean isMonitoringEnabled() {
        return monitoringEnabled;
    }

    public void setMonitoringEnabled(boolean monitoringEnabled) {
        this.monitoringEnabled = monitoringEnabled;
    }

    public String getMonitoringParameters() {
        return monitoringParameters;
    }

    public void setMonitoringParameters(String monitoringParameters) {
        this.monitoringParameters = monitoringParameters;
    }
}
