package com.aoindustries.aoserv.client;

/*
 * Copyright 2008-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * A <code>VirtualServer</code> consumes physical resources within the
 * virtualization layer.
 *
 * @author  AO Industries, Inc.
 */
final public class VirtualServer extends CachedObjectIntegerKey<VirtualServer> {

    static final int COLUMN_SERVER=0;

    static final String COLUMN_SERVER_name = "server";

    private int primaryRam;
    private int secondaryRam;
    private String minimumProcessorType;
    private String minimumProcessorArchitecture;
    private int minimumProcessorSpeed;
    private short processorCores;
    private short processorWeight;
    private boolean primaryPhysicalServerLocked;
    private boolean secondaryPhysicalServerLocked;
    private boolean requires_hvm;

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_SERVER: return Integer.valueOf(pkey);
            case 1 : return primaryRam;
            case 2 : return secondaryRam==-1 ? null : Integer.valueOf(secondaryRam);
            case 3 : return minimumProcessorType;
            case 4 : return minimumProcessorArchitecture;
            case 5 : return minimumProcessorSpeed==-1 ? null : Integer.valueOf(minimumProcessorSpeed);
            case 6 : return processorCores;
            case 7 : return processorWeight;
            case 8 : return primaryPhysicalServerLocked;
            case 9 : return secondaryPhysicalServerLocked;
            case 10 : return requires_hvm;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public Server getServer() throws SQLException, IOException {
        Server se=table.connector.servers.get(pkey);
        if(se==null) new SQLException("Unable to find Server: "+pkey);
        return se;
    }

    public int getPrimaryRam() {
        return primaryRam;
    }

    /**
     * Gets the secondary RAM allocation or <code>-1</code> if no secondary required.
     * When RAM allocation is <code>-1</code>, the VM will not be able to run on the
     * secondary server - it only provides block device replication.  Therefore,
     * other things like processor type, speed, architecture, processor cores and
     * processor weights will also not be allocated.
     */
    public int getSecondaryRam() {
        return secondaryRam;
    }
    
    /**
     * Gets the minimum processor type or <code>null</code> if none.
     */
    public ProcessorType getMinimumProcessorType() {
        if(minimumProcessorType==null) return null;
        ProcessorType pt = table.connector.processorTypes.get(minimumProcessorType);
        if(pt==null) new SQLException("Unable to find ProcessorType: "+minimumProcessorType);
        return pt;
    }

    /**
     * Gets the minimum processor architecture.
     */
    public Architecture getMinimumProcessorArchitecture() {
        Architecture a = table.connector.architectures.get(minimumProcessorArchitecture);
        if(a==null) new SQLException("Unable to find Architecture: "+minimumProcessorArchitecture);
        return a;
    }

    /**
     * Gets the minimum processor speed or <code>-1</code> for none.
     */
    public int getMinimumProcessorSpeed() {
        return minimumProcessorSpeed;
    }

    /**
     * Gets the processor cores.
     */
    public short getProcessorCores() {
        return processorCores;
    }

    /**
     * Gets the processor weight.
     */
    public short getProcessorWeight() {
        return processorWeight;
    }

    /**
     * Gets if the primary server is locked (manually set).
     */
    public boolean isPrimaryPhysicalServerLocked() {
        return primaryPhysicalServerLocked;
    }
    
    /**
     * Gets if the secondary server is locked (manually set).
     */
    public boolean isSecondaryPhysicalServerLocked() {
        return secondaryPhysicalServerLocked;
    }
    
    /**
     * Gets if this virtual requires full hardware virtualization support.
     */
    public boolean getRequiresHvm() {
        return requires_hvm;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.VIRTUAL_SERVERS;
    }

    public void init(ResultSet result) throws SQLException {
        int pos = 1;
        pkey = result.getInt(pos++);
        primaryRam = result.getInt(pos++);
        secondaryRam = result.getInt(pos++);
        if(result.wasNull()) secondaryRam = -1;
        minimumProcessorType = result.getString(pos++);
        minimumProcessorArchitecture = result.getString(pos++);
        minimumProcessorSpeed = result.getInt(pos++);
        if(result.wasNull()) minimumProcessorSpeed = -1;
        processorCores = result.getShort(pos++);
        processorWeight = result.getShort(pos++);
        primaryPhysicalServerLocked = result.getBoolean(pos++);
        secondaryPhysicalServerLocked = result.getBoolean(pos++);
        requires_hvm = result.getBoolean(pos++);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey = in.readCompressedInt();
        primaryRam = in.readCompressedInt();
        secondaryRam = in.readCompressedInt();
        minimumProcessorType = StringUtility.intern(in.readNullUTF());
        minimumProcessorArchitecture = in.readUTF().intern();
        minimumProcessorSpeed = in.readCompressedInt();
        processorCores = in.readShort();
        processorWeight = in.readShort();
        primaryPhysicalServerLocked = in.readBoolean();
        secondaryPhysicalServerLocked = in.readBoolean();
        requires_hvm = in.readBoolean();
    }

    @Override
    protected String toStringImpl() throws SQLException, IOException {
        return getServer().toStringImpl();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(primaryRam);
        out.writeCompressedInt(secondaryRam);
        out.writeNullUTF(minimumProcessorType);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_40)<=0) out.writeNullUTF(secondaryRam==-1 ? null : minimumProcessorType);
        out.writeUTF(minimumProcessorArchitecture);
        out.writeCompressedInt(minimumProcessorSpeed);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_40)<=0) out.writeCompressedInt(secondaryRam==-1 ? -1 : minimumProcessorSpeed);
        out.writeShort(processorCores);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_40)<=0) out.writeShort(secondaryRam==-1 ? -1 : processorCores);
        out.writeShort(processorWeight);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_40)<=0) out.writeShort(secondaryRam==-1 ? -1 : processorWeight);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_40)<=0) out.writeCompressedInt(-1);
        out.writeBoolean(primaryPhysicalServerLocked);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_40)<=0) out.writeCompressedInt(-1);
        out.writeBoolean(secondaryPhysicalServerLocked);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_37)>=0) out.writeBoolean(requires_hvm);
    }
    
    public List<VirtualDisk> getVirtualDisks() throws IOException, SQLException {
        return table.connector.virtualDisks.getVirtualDisks(this);
    }
    
    /**
     * Gets the virtual disk for this virtual server and the provided device
     * name.
     * @param device should be <code>xvd[a-z]</code>
     * @return the disk or <code>null</code> if not found
     */
    public VirtualDisk getVirtualDisk(String device) throws IOException, SQLException {
        for(VirtualDisk vd : getVirtualDisks()) {
            if(vd.getDevice().equals(device)) return vd;
        }
        return null;
    }
}
