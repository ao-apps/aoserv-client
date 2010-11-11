/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class NetDevice {

    private int pkey;
    private int server;
    private String deviceId;
    private String description;
    private InetAddress gateway;
    private InetAddress network;
    private InetAddress broadcast;
    private MacAddress macAddress;
    private Long maxBitRate;
    private Long monitoringBitRateLow;
    private Long monitoringBitRateMedium;
    private Long monitoringBitRateHigh;
    private Long monitoringBitRateCritical;

    public NetDevice() {
    }

    public NetDevice(
        int pkey,
        int server,
        String deviceId,
        String description,
        InetAddress gateway,
        InetAddress network,
        InetAddress broadcast,
        MacAddress macAddress,
        Long maxBitRate,
        Long monitoringBitRateLow,
        Long monitoringBitRateMedium,
        Long monitoringBitRateHigh,
        Long monitoringBitRateCritical
    ) {
        this.pkey = pkey;
        this.server = server;
        this.deviceId = deviceId;
        this.description = description;
        this.gateway = gateway;
        this.network = network;
        this.broadcast = broadcast;
        this.macAddress = macAddress;
        this.maxBitRate = maxBitRate;
        this.monitoringBitRateLow = monitoringBitRateLow;
        this.monitoringBitRateMedium = monitoringBitRateMedium;
        this.monitoringBitRateHigh = monitoringBitRateHigh;
        this.monitoringBitRateCritical = monitoringBitRateCritical;
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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public InetAddress getGateway() {
        return gateway;
    }

    public void setGateway(InetAddress gateway) {
        this.gateway = gateway;
    }

    public InetAddress getNetwork() {
        return network;
    }

    public void setNetwork(InetAddress network) {
        this.network = network;
    }

    public InetAddress getBroadcast() {
        return broadcast;
    }

    public void setBroadcast(InetAddress broadcast) {
        this.broadcast = broadcast;
    }

    public MacAddress getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(MacAddress macAddress) {
        this.macAddress = macAddress;
    }

    public Long getMaxBitRate() {
        return maxBitRate;
    }

    public void setMaxBitRate(Long maxBitRate) {
        this.maxBitRate = maxBitRate;
    }

    public Long getMonitoringBitRateLow() {
        return monitoringBitRateLow;
    }

    public void setMonitoringBitRateLow(Long monitoringBitRateLow) {
        this.monitoringBitRateLow = monitoringBitRateLow;
    }

    public Long getMonitoringBitRateMedium() {
        return monitoringBitRateMedium;
    }

    public void setMonitoringBitRateMedium(Long monitoringBitRateMedium) {
        this.monitoringBitRateMedium = monitoringBitRateMedium;
    }

    public Long getMonitoringBitRateHigh() {
        return monitoringBitRateHigh;
    }

    public void setMonitoringBitRateHigh(Long monitoringBitRateHigh) {
        this.monitoringBitRateHigh = monitoringBitRateHigh;
    }

    public Long getMonitoringBitRateCritical() {
        return monitoringBitRateCritical;
    }

    public void setMonitoringBitRateCritical(Long monitoringBitRateCritical) {
        this.monitoringBitRateCritical = monitoringBitRateCritical;
    }
}
