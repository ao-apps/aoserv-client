/*
 * Copyright 2008-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.command.RequestVncConsoleAccessCommand;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;

/**
 * A <code>VirtualServer</code> consumes physical resources within the
 * virtualization layer.
 *
 * @author  AO Industries, Inc.
 */
final public class VirtualServer extends AOServObjectIntegerKey<VirtualServer> implements BeanFactory<com.aoindustries.aoserv.client.beans.VirtualServer> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
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
        VirtualServerService<?,?> service,
        int server,
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
        super(service, server);
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

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(VirtualServer other) throws RemoteException {
        return key==other.key? 0 : AOServObjectUtils.compare(getServer(), other.getServer());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    static final String COLUMN_SERVER = "server";
    @SchemaColumn(order=0, name=COLUMN_SERVER, index=IndexType.PRIMARY_KEY, description="the server that is virtualized")
    public Server getServer() throws RemoteException {
        return getService().getConnector().getServers().get(key);
    }

    @SchemaColumn(order=1, name="primary_ram", description="the amount of RAM required in primary mode in megabytes")
    public int getPrimaryRam() {
        return primaryRam;
    }

    @SchemaColumn(order=2, name="primary_ram_target", description="the amount of RAM required in primary mode in megabytes")
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
    @SchemaColumn(order=3, name="secondary_ram", description="the amount of RAM required in primary mode in megabytes or null if secondary not required")
    public Integer getSecondaryRam() {
        return secondaryRam;
    }

    @SchemaColumn(order=4, name="secondary_ram_target", description="the amount of RAM required in primary mode in megabytes or null if secondary not required")
    public Integer getSecondaryRamTarget() {
        return secondaryRamTarget;
    }

    static final String COLUMN_MINIMUM_PROCESSOR_TYPE = "minimum_processor_type";
    /**
     * Gets the minimum processor type or <code>null</code> if none.
     */
    @SchemaColumn(order=5, name=COLUMN_MINIMUM_PROCESSOR_TYPE, index=IndexType.INDEXED, description="the minimum processor type")
    public ProcessorType getMinimumProcessorType() throws RemoteException {
        if(minimumProcessorType==null) return null;
        return getService().getConnector().getProcessorTypes().get(minimumProcessorType);
    }

    static final String COLUMN_MINIMUM_PROCESSOR_ARCHITECTURE = "minimum_processor_architecture";
    /**
     * Gets the minimum processor architecture.
     */
    @SchemaColumn(order=6, name=COLUMN_MINIMUM_PROCESSOR_ARCHITECTURE, index=IndexType.INDEXED, description="the minimum processor architecture, compatible architectures may be substituted")
    public Architecture getMinimumProcessorArchitecture() throws RemoteException {
        return getService().getConnector().getArchitectures().get(minimumProcessorArchitecture);
    }

    /**
     * Gets the minimum processor speed or <code>null</code> for none.
     */
    @SchemaColumn(order=7, name="minimum_processor_speed", description="the minimum processor speed in MHz")
    public Integer getMinimumProcessorSpeed() {
        return minimumProcessorSpeed;
    }

    /**
     * Gets the minimum processor speed target or <code>null</code> for none.
     */
    @SchemaColumn(order=8, name="minimum_processor_speed_target", description="the minimum processor speed in MHz")
    public Integer getMinimumProcessorSpeedTarget() {
        return minimumProcessorSpeedTarget;
    }

    /**
     * Gets the processor cores.
     */
    @SchemaColumn(order=9, name="processor_cores", description="the number of processor cores")
    public short getProcessorCores() {
        return processorCores;
    }

    @SchemaColumn(order=10, name="processor_cores_target", description="the number of processor cores")
    public short getProcessorCoresTarget() {
        return processorCoresTarget;
    }

    /**
     * Gets the processor weight.
     */
    @SchemaColumn(order=11, name="processor_weight", description="the processor allocation weight on a scale of 1-1024")
    public short getProcessorWeight() {
        return processorWeight;
    }

    @SchemaColumn(order=12, name="processor_weight_target", description="the processor allocation weight on a scale of 1-1024")
    public short getProcessorWeightTarget() {
        return processorWeightTarget;
    }

    /**
     * Gets if the primary server is locked (manually set).
     */
    @SchemaColumn(order=13, name="primary_physical_server_locked", description="indicates the primary server is locked and should not be moved by automated means")
    public boolean isPrimaryPhysicalServerLocked() {
        return primaryPhysicalServerLocked;
    }

    /**
     * Gets if the secondary server is locked (manually set).
     */
    @SchemaColumn(order=14, name="secondary_physical_server_locked", description="indicates the secondary server is locked and should not be moved by automated means")
    public boolean isSecondaryPhysicalServerLocked() {
        return secondaryPhysicalServerLocked;
    }

    /**
     * Gets if this virtual requires full hardware virtualization support.
     */
    @SchemaColumn(order=15, name="requires_hvm", description="indicates requires full hardware virtualization")
    public boolean getRequiresHvm() {
        return requiresHvm;
    }

    /**
     * Gets the VNC password for this virtual server or <code>null</code> if VNC is disabled.
     * The password must be unique between virtual servers because the password is used
     * behind the scenes to resolve the actual IP and port for VNC proxying.
     */
    @SchemaColumn(order=16, name="vnc_password", description="the password for VNC console access or null to disable VNC access")
    public String getVncPassword() {
        return vncPassword;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    @Override
    public com.aoindustries.aoserv.client.beans.VirtualServer getBean() {
        return new com.aoindustries.aoserv.client.beans.VirtualServer(key, primaryRam, primaryRamTarget, secondaryRam, secondaryRamTarget, minimumProcessorType, minimumProcessorArchitecture, minimumProcessorSpeed, minimumProcessorSpeedTarget, processorCores, processorCoresTarget, processorWeight, processorWeightTarget, primaryPhysicalServerLocked, secondaryPhysicalServerLocked, requiresHvm, vncPassword);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getServer());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getMinimumProcessorType());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getMinimumProcessorArchitecture());
        return unionSet;
    }

    /* TODO
    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getVirtualDisks()
        );
    }
     */
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        return getServer().toStringImpl();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    public List<VirtualDisk> getVirtualDisks() throws IOException, SQLException {
        return getService().getConnector().getVirtualDisks().getVirtualDisks(this);
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

    // <editor-fold defaultstate="collapsed" desc="Commands">
    public AOServer.DaemonAccess requestVncConsoleAccess() throws RemoteException {
        return new RequestVncConsoleAccessCommand(key).execute(getService().getConnector());
    }
    // </editor-fold>
}
