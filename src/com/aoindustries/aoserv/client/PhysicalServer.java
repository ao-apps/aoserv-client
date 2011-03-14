/*
 * Copyright 2008-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.table.IndexType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;

/**
 * A <code>PhysicalServer</code> consumes space and electricity in a rack
 * and provides resources either directly or through virtual servers.
 *
 * @author  AO Industries, Inc.
 */
final public class PhysicalServer extends Server implements DtoFactory<com.aoindustries.aoserv.client.dto.PhysicalServer> {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = -3610976367634402976L;

    final private int rack;
    final private Short rackUnits;
    final private Integer ram;
    private String processorType;
    final private Integer processorSpeed;
    final private Integer processorCores;
    final private Float maxPower;
    final private Boolean supportsHvm;

    public PhysicalServer(
        AOServConnector connector,
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
        super(connector, pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, farm, description, operatingSystemVersion, name, monitoringEnabled);
        this.rack = rack;
        this.rackUnits = rackUnits;
        this.ram = ram;
        this.processorType = processorType;
        this.processorSpeed = processorSpeed;
        this.processorCores = processorCores;
        this.maxPower = maxPower;
        this.supportsHvm = supportsHvm;
        intern();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        processorType = intern(processorType);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    public static final MethodColumn COLUMN_RACK = getMethodColumn(PhysicalServer.class, "rack");
    @DependencySingleton
    @SchemaColumn(order=SERVER_LAST_COLUMN+1, index=IndexType.INDEXED, description="the rack that houses this server")
    public Rack getRack() throws RemoteException {
        return getConnector().getRacks().get(rack);
    }

    @SchemaColumn(order=SERVER_LAST_COLUMN+2, description="the number of rack units")
    public Short getRackUnits() {
        return rackUnits;
    }

    @SchemaColumn(order=SERVER_LAST_COLUMN+3, description="the total number of megabytes of RAM in this server")
    public Integer getRam() {
        return ram;
    }

    public static final MethodColumn COLUMN_PROCESSOR_TYPE = getMethodColumn(PhysicalServer.class, "processorType");
    @DependencySingleton
    @SchemaColumn(order=SERVER_LAST_COLUMN+4, index=IndexType.INDEXED, description="the processor type")
    public ProcessorType getProcessorType() throws RemoteException {
        if(processorType==null) return null;
        return getConnector().getProcessorTypes().get(processorType);
    }

    @SchemaColumn(order=SERVER_LAST_COLUMN+5, description="the processor speed in MHz")
    public Integer getProcessorSpeed() {
        return processorSpeed;
    }

    @SchemaColumn(order=SERVER_LAST_COLUMN+6, description="the total number of processor cores, hyperthreads are counted as different cores")
    public Integer getProcessorCores() {
        return processorCores;
    }

    @SchemaColumn(order=SERVER_LAST_COLUMN+7, description="the number of amps this server consumes under peak load")
    public Float getMaxPower() {
        return maxPower;
    }

    @SchemaColumn(order=SERVER_LAST_COLUMN+8, description="indicates supports full hardware virtualization")
    public Boolean getSupportsHvm() {
        return supportsHvm;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public PhysicalServer(AOServConnector connector, com.aoindustries.aoserv.client.dto.PhysicalServer dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            dto.getResourceType(),
            getAccountingCode(dto.getAccounting()),
            getTimeMillis(dto.getCreated()),
            getUserId(dto.getCreatedBy()),
            dto.getDisableLog(),
            getTimeMillis(dto.getLastEnabled()),
            dto.getFarm(),
            dto.getDescription(),
            dto.getOperatingSystemVersion(),
            dto.getName(),
            dto.isMonitoringEnabled(),
            dto.getRack(),
            dto.getRackUnits(),
            dto.getRam(),
            dto.getProcessorType(),
            dto.getProcessorSpeed(),
            dto.getProcessorCores(),
            dto.getMaxPower(),
            dto.getSupportsHvm()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.PhysicalServer getDto() {
        return new com.aoindustries.aoserv.client.dto.PhysicalServer(
            key,
            getResourceTypeName(),
            getDto(getAccounting()),
            created,
            getDto(getCreatedByUsername()),
            disableLog,
            lastEnabled,
            farm,
            description,
            operatingSystemVersion,
            name,
            monitoringEnabled,
            rack,
            rackUnits,
            ram,
            processorType,
            processorSpeed,
            processorCores,
            maxPower,
            supportsHvm
        );
    }
    // </editor-fold>
}
