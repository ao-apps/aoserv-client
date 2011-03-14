/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
abstract public class Server extends Resource {

    private int farm;
    private String description;
    private Integer operatingSystemVersion;
    private String name;
    private boolean monitoringEnabled;

    public Server() {
    }

    public Server(
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        int farm,
        String description,
        Integer operatingSystemVersion,
        String name,
        boolean monitoringEnabled
    ) {
        super(pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled);
        this.farm = farm;
        this.description = description;
        this.operatingSystemVersion = operatingSystemVersion;
        this.name = name;
        this.monitoringEnabled = monitoringEnabled;
    }

    public int getFarm() {
        return farm;
    }

    public void setFarm(int farm) {
        this.farm = farm;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOperatingSystemVersion() {
        return operatingSystemVersion;
    }

    public void setOperatingSystemVersion(Integer operatingSystemVersion) {
        this.operatingSystemVersion = operatingSystemVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMonitoringEnabled() {
        return monitoringEnabled;
    }

    public void setMonitoringEnabled(boolean monitoringEnabled) {
        this.monitoringEnabled = monitoringEnabled;
    }
}
