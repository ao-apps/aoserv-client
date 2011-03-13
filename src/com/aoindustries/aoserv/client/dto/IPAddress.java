/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class IPAddress extends ServerResource {

    private InetAddress ipAddress;
    private Integer netDevice;
    private boolean isAlias;
    private DomainName hostname;
    private boolean available;
    private boolean isOverflow;
    private boolean isDhcp;
    private boolean pingMonitorEnabled;
    private InetAddress externalIpAddress;
    private short netmask;

    public IPAddress() {
    }

    public IPAddress(
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        int server,
        int businessServer,
        InetAddress ipAddress,
        Integer netDevice,
        boolean isAlias,
        DomainName hostname,
        boolean available,
        boolean isOverflow,
        boolean isDhcp,
        boolean pingMonitorEnabled,
        InetAddress externalIpAddress,
        short netmask
    ) {
        super(pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, server, businessServer);
        this.ipAddress = ipAddress;
        this.netDevice = netDevice;
        this.isAlias = isAlias;
        this.hostname = hostname;
        this.available = available;
        this.isOverflow = isOverflow;
        this.isDhcp = isDhcp;
        this.pingMonitorEnabled = pingMonitorEnabled;
        this.externalIpAddress = externalIpAddress;
        this.netmask = netmask;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getNetDevice() {
        return netDevice;
    }

    public void setNetDevice(Integer netDevice) {
        this.netDevice = netDevice;
    }

    public boolean isIsAlias() {
        return isAlias;
    }

    public void setIsAlias(boolean isAlias) {
        this.isAlias = isAlias;
    }

    public DomainName getHostname() {
        return hostname;
    }

    public void setHostname(DomainName hostname) {
        this.hostname = hostname;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isIsOverflow() {
        return isOverflow;
    }

    public void setIsOverflow(boolean isOverflow) {
        this.isOverflow = isOverflow;
    }

    public boolean isIsDhcp() {
        return isDhcp;
    }

    public void setIsDhcp(boolean isDhcp) {
        this.isDhcp = isDhcp;
    }

    public boolean isPingMonitorEnabled() {
        return pingMonitorEnabled;
    }

    public void setPingMonitorEnabled(boolean pingMonitorEnabled) {
        this.pingMonitorEnabled = pingMonitorEnabled;
    }

    public InetAddress getExternalIpAddress() {
        return externalIpAddress;
    }

    public void setExternalIpAddress(InetAddress externalIpAddress) {
        this.externalIpAddress = externalIpAddress;
    }

    public short getNetmask() {
        return netmask;
    }

    public void setNetmask(short netmask) {
        this.netmask = netmask;
    }
}
