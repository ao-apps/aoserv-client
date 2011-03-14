/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class PhysicalServer extends Server {

    private int rack;
    private Short rackUnits;
    private Integer ram;
    private String processorType;
    private Integer processorSpeed;
    private Integer processorCores;
    private Float maxPower;
    private Boolean supportsHvm;

    public PhysicalServer() {
    }

    public PhysicalServer(
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
        boolean monitoringEnabled,
        int rack,
        Short rackUnits,
        Integer ram,
        String processorType,
        Integer processorSpeed,
        Integer processorCores,
        Float maxPower,
        Boolean supportsHvm
    ) {
        super(pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, farm, description, operatingSystemVersion, name, monitoringEnabled);
        this.rack = rack;
        this.rackUnits = rackUnits;
        this.ram = ram;
        this.processorType = processorType;
        this.processorSpeed = processorSpeed;
        this.processorCores = processorCores;
        this.maxPower = maxPower;
        this.supportsHvm = supportsHvm;
    }

    public int getRack() {
        return rack;
    }

    public void setRack(int rack) {
        this.rack = rack;
    }

    public Short getRackUnits() {
        return rackUnits;
    }

    public void setRackUnits(Short rackUnits) {
        this.rackUnits = rackUnits;
    }

    public Integer getRam() {
        return ram;
    }

    public void setRam(Integer ram) {
        this.ram = ram;
    }

    public String getProcessorType() {
        return processorType;
    }

    public void setProcessorType(String processorType) {
        this.processorType = processorType;
    }

    public Integer getProcessorSpeed() {
        return processorSpeed;
    }

    public void setProcessorSpeed(Integer processorSpeed) {
        this.processorSpeed = processorSpeed;
    }

    public Integer getProcessorCores() {
        return processorCores;
    }

    public void setProcessorCores(Integer processorCores) {
        this.processorCores = processorCores;
    }

    public Float getMaxPower() {
        return maxPower;
    }

    public void setMaxPower(Float maxPower) {
        this.maxPower = maxPower;
    }

    public Boolean getSupportsHvm() {
        return supportsHvm;
    }

    public void setSupportsHvm(Boolean supportsHvm) {
        this.supportsHvm = supportsHvm;
    }
}
