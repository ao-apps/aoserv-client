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
 * A <code>VirtualServer</code> consumes physical resources within the
 * virtualization layer.
 *
 * @author  AO Industries, Inc.
 */
final public class VirtualServer extends Server implements DtoFactory<com.aoindustries.aoserv.client.dto.VirtualServer> {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = 3405172634580756287L;

    final private int primaryRam;
    final private int primaryRamTarget;
    final private Integer secondaryRam;
    final private Integer secondaryRamTarget;
    private String minimumProcessorType;
    private String minimumProcessorArchitecture;
    final private Integer minimumProcessorSpeed;
    final private Integer minimumProcessorSpeedTarget;
    final private short processorCores;
    final private short processorCoresTarget;
    final private short processorWeight;
    final private short processorWeightTarget;
    final private boolean primaryPhysicalServerLocked;
    final private boolean secondaryPhysicalServerLocked;
    final private boolean requiresHvm;
    final private String vncPassword;

    public VirtualServer(
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
        int primaryRam,
        int primaryRamTarget,
        Integer secondaryRam,
        Integer secondaryRamTarget,
        String minimumProcessorType,
        String minimumProcessorArchitecture,
        Integer minimumProcessorSpeed,
        Integer minimumProcessorSpeedTarget,
        short processorCores,
        short processorCoresTarget,
        short processorWeight,
        short processorWeightTarget,
        boolean primaryPhysicalServerLocked,
        boolean secondaryPhysicalServerLocked,
        boolean requiresHvm,
        String vncPassword
    ) {
        super(connector, pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, farm, description, operatingSystemVersion, name, monitoringEnabled);
        this.primaryRam = primaryRam;
        this.primaryRamTarget = primaryRamTarget;
        this.secondaryRam = secondaryRam;
        this.secondaryRamTarget = secondaryRamTarget;
        this.minimumProcessorType = minimumProcessorType;
        this.minimumProcessorArchitecture = minimumProcessorArchitecture;
        this.minimumProcessorSpeed = minimumProcessorSpeed;
        this.minimumProcessorSpeedTarget = minimumProcessorSpeedTarget;
        this.processorCores = processorCores;
        this.processorCoresTarget = processorCoresTarget;
        this.processorWeight = processorWeight;
        this.processorWeightTarget = processorWeightTarget;
        this.primaryPhysicalServerLocked = primaryPhysicalServerLocked;
        this.secondaryPhysicalServerLocked = secondaryPhysicalServerLocked;
        this.requiresHvm = requiresHvm;
        this.vncPassword = vncPassword;
        intern();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        minimumProcessorType = intern(minimumProcessorType);
        minimumProcessorArchitecture = intern(minimumProcessorArchitecture);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=SERVER_LAST_COLUMN+1, description="the amount of RAM required in primary mode in megabytes")
    public int getPrimaryRam() {
        return primaryRam;
    }

    @SchemaColumn(order=SERVER_LAST_COLUMN+2, description="the amount of RAM required in primary mode in megabytes")
    public int getPrimaryRamTarget() {
        return primaryRamTarget;
    }

    /**
     * Gets the secondary RAM allocation or <code>null</code> if no secondary required.
     * When RAM allocation is <code>null</code>, the VM will not be able to run on the
     * secondary server - it only provides block device replication.  Therefore,
     * other things like processor type, speed, architecture, processor cores and
     * processor weights will also not be allocated.
     */
    @SchemaColumn(order=SERVER_LAST_COLUMN+3, description="the amount of RAM required in primary mode in megabytes or null if secondary not required")
    public Integer getSecondaryRam() {
        return secondaryRam;
    }

    @SchemaColumn(order=SERVER_LAST_COLUMN+4, description="the amount of RAM required in primary mode in megabytes or null if secondary not required")
    public Integer getSecondaryRamTarget() {
        return secondaryRamTarget;
    }

    public static final MethodColumn COLUMN_MINIMUM_PROCESSOR_TYPE = getMethodColumn(VirtualServer.class, "minimumProcessorType");
    /**
     * Gets the minimum processor type or <code>null</code> if none.
     */
    @DependencySingleton
    @SchemaColumn(order=SERVER_LAST_COLUMN+5, index=IndexType.INDEXED, description="the minimum processor type")
    public ProcessorType getMinimumProcessorType() throws RemoteException {
        if(minimumProcessorType==null) return null;
        return getConnector().getProcessorTypes().get(minimumProcessorType);
    }

    public static final MethodColumn COLUMN_MINIMUM_PROCESSOR_ARCHITECTURE = getMethodColumn(VirtualServer.class, "minimumProcessorArchitecture");
    @DependencySingleton
    @SchemaColumn(order=SERVER_LAST_COLUMN+6, index=IndexType.INDEXED, description="the minimum processor architecture, compatible architectures may be substituted")
    public Architecture getMinimumProcessorArchitecture() throws RemoteException {
        return getConnector().getArchitectures().get(minimumProcessorArchitecture);
    }

    /**
     * Gets the minimum processor speed or <code>null</code> for none.
     */
    @SchemaColumn(order=SERVER_LAST_COLUMN+7, description="the minimum processor speed in MHz")
    public Integer getMinimumProcessorSpeed() {
        return minimumProcessorSpeed;
    }

    /**
     * Gets the minimum processor speed target or <code>null</code> for none.
     */
    @SchemaColumn(order=SERVER_LAST_COLUMN+8, description="the minimum processor speed in MHz")
    public Integer getMinimumProcessorSpeedTarget() {
        return minimumProcessorSpeedTarget;
    }

    /**
     * Gets the processor cores.
     */
    @SchemaColumn(order=SERVER_LAST_COLUMN+9, description="the number of processor cores")
    public short getProcessorCores() {
        return processorCores;
    }

    @SchemaColumn(order=SERVER_LAST_COLUMN+10, description="the number of processor cores")
    public short getProcessorCoresTarget() {
        return processorCoresTarget;
    }

    /**
     * Gets the processor weight.
     */
    @SchemaColumn(order=SERVER_LAST_COLUMN+11, description="the processor allocation weight on a scale of 1-1024")
    public short getProcessorWeight() {
        return processorWeight;
    }

    @SchemaColumn(order=SERVER_LAST_COLUMN+12, description="the processor allocation weight on a scale of 1-1024")
    public short getProcessorWeightTarget() {
        return processorWeightTarget;
    }

    /**
     * Gets if the primary server is locked (manually set).
     */
    @SchemaColumn(order=SERVER_LAST_COLUMN+13, description="indicates the primary server is locked and should not be moved by automated means")
    public boolean isPrimaryPhysicalServerLocked() {
        return primaryPhysicalServerLocked;
    }

    /**
     * Gets if the secondary server is locked (manually set).
     */
    @SchemaColumn(order=SERVER_LAST_COLUMN+14, description="indicates the secondary server is locked and should not be moved by automated means")
    public boolean isSecondaryPhysicalServerLocked() {
        return secondaryPhysicalServerLocked;
    }

    /**
     * Gets if this virtual requires full hardware virtualization support.
     */
    @SchemaColumn(order=SERVER_LAST_COLUMN+15, description="indicates requires full hardware virtualization")
    public boolean getRequiresHvm() {
        return requiresHvm;
    }

    /**
     * Gets the VNC password for this virtual server or <code>null</code> if VNC is disabled.
     * The password must be unique between virtual servers because the password is used
     * behind the scenes to resolve the actual IP and port for VNC proxying.
     */
    @SchemaColumn(order=SERVER_LAST_COLUMN+16, description="the password for VNC console access or null to disable VNC access")
    public String getVncPassword() {
        return vncPassword;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public VirtualServer(AOServConnector connector, com.aoindustries.aoserv.client.dto.VirtualServer dto) throws ValidationException {
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
            dto.getPrimaryRam(),
            dto.getPrimaryRamTarget(),
            dto.getSecondaryRam(),
            dto.getSecondaryRamTarget(),
            dto.getMinimumProcessorType(),
            dto.getMinimumProcessorArchitecture(),
            dto.getMinimumProcessorSpeed(),
            dto.getMinimumProcessorSpeedTarget(),
            dto.getProcessorCores(),
            dto.getProcessorCoresTarget(),
            dto.getProcessorWeight(),
            dto.getProcessorWeightTarget(),
            dto.isPrimaryPhysicalServerLocked(),
            dto.isSecondaryPhysicalServerLocked(),
            dto.isRequiresHvm(),
            dto.getVncPassword()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.VirtualServer getDto() {
        return new com.aoindustries.aoserv.client.dto.VirtualServer(
            getKeyInt(),
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
            primaryRam,
            primaryRamTarget,
            secondaryRam,
            secondaryRamTarget,
            minimumProcessorType,
            minimumProcessorArchitecture,
            minimumProcessorSpeed,
            minimumProcessorSpeedTarget,
            processorCores,
            processorCoresTarget,
            processorWeight,
            processorWeightTarget,
            primaryPhysicalServerLocked,
            secondaryPhysicalServerLocked,
            requiresHvm,
            vncPassword
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    @DependentObjectSet
    public List<VirtualDisk> getVirtualDisks() throws IOException, SQLException {
        return getConnector().getVirtualDisks().getVirtualDisks(this);
    }
    */
    /**
     * Gets the virtual disk for this virtual server and the provided device
     * name.
     * @param device should be <code>xvd[a-z]</code>
     * @return the disk or <code>null</code> if not found
     */
    /* TODO
    public VirtualDisk getVirtualDisk(String device) throws IOException, SQLException {
        for(VirtualDisk vd : getVirtualDisks()) {
            if(vd.getDevice().equals(device)) return vd;
        }
        return null;
    }
    */
    // </editor-fold>
}
