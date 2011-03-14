/*
 * Copyright 2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class Rack extends Resource {

    private int farm;
    private String name;
    private Float maxPower;
    private Integer totalRackUnits;

    public Rack() {
    }

    public Rack(
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        int farm,
        String name,
        Float maxPower,
        Integer totalRackUnits
    ) {
        super(pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled);
        this.farm = farm;
        this.name = name;
        this.maxPower = maxPower;
        this.totalRackUnits = totalRackUnits;
    }

    public int getFarm() {
        return farm;
    }

    public void setFarm(int farm) {
        this.farm = farm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getMaxPower() {
        return maxPower;
    }

    public void setMaxPower(Float maxPower) {
        this.maxPower = maxPower;
    }

    public Integer getTotalRackUnits() {
        return totalRackUnits;
    }

    public void setTotalRackUnits(Integer totalRackUnits) {
        this.totalRackUnits = totalRackUnits;
    }
}
