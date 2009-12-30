/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class Server {

    private int pkey;
    private String farm;
    private String description;
    private Integer operatingSystemVersion;
    private String accounting;
    private String name;
    private boolean monitoringEnabled;

    public Server() {
    }

    public Server(
        int pkey,
        String farm,
        String description,
        Integer operatingSystemVersion,
        String accounting,
        String name,
        boolean monitoringEnabled
    ) {
        this.pkey = pkey;
        this.farm = farm;
        this.description = description;
        this.operatingSystemVersion = operatingSystemVersion;
        this.accounting = accounting;
        this.name = name;
        this.monitoringEnabled = monitoringEnabled;
    }

    public int getPkey() {
        return pkey;
    }

    public void setPkey(int pkey) {
        this.pkey = pkey;
    }

    public String getFarm() {
        return farm;
    }

    public void setFarm(String farm) {
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

    public String getAccounting() {
        return accounting;
    }

    public void setAccounting(String accounting) {
        this.accounting = accounting;
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
