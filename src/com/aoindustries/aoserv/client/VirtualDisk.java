package com.aoindustries.aoserv.client;

/*
 * Copyright 2008-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>VirtualDisk</code> is a block device for a <code>VirtualServer</code>.
 *
 * @author  AO Industries, Inc.
 */
final public class VirtualDisk extends CachedObjectIntegerKey<VirtualDisk> {

    static final int COLUMN_PKEY = 0;
    static final int COLUMN_VIRTUAL_SERVER = 1;
    static final int COLUMN_DEVICE = 2;

    static final String COLUMN_VIRTUAL_SERVER_name = "virtual_server";
    static final String COLUMN_DEVICE_name = "device";

    private int virtualServer;
    private String device;
    private String primaryMinimumRaidType;
    private String secondaryMinimumRaidType;
    private String primaryMinimumDiskType;
    private String secondaryMinimumDiskType;
    private int primaryMinimumDiskSpeed;
    private int secondaryMinimumDiskSpeed;
    private int extents;
    private short primaryWeight;
    private short secondaryWeight;
    private boolean primaryPhysicalVolumesLocked;
    private boolean secondaryPhysicalVolumesLocked;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return pkey;
            case COLUMN_VIRTUAL_SERVER : return virtualServer;
            case COLUMN_DEVICE : return device;
            case 3 : return primaryMinimumRaidType;
            case 4 : return secondaryMinimumRaidType;
            case 5 : return primaryMinimumDiskType;
            case 6 : return secondaryMinimumDiskType;
            case 7 : return primaryMinimumDiskSpeed==-1 ? null : Integer.valueOf(primaryMinimumDiskSpeed);
            case 8 : return secondaryMinimumDiskSpeed==-1 ? null : Integer.valueOf(secondaryMinimumDiskSpeed);
            case 9 : return extents;
            case 10 : return primaryWeight;
            case 11 : return secondaryWeight;
            case 12 : return primaryPhysicalVolumesLocked;
            case 13 : return secondaryPhysicalVolumesLocked;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public VirtualServer getVirtualServer() {
        VirtualServer vs=table.connector.virtualServers.get(virtualServer);
        if(vs==null) throw new WrappedException(new SQLException("Unable to find VirtualServer: "+virtualServer));
        return vs;
    }

    /**
     * Gets the per-VirtualServer unique device (without the /dev/ prefix), such
     * as <code>xvda</code> or <code>xvdb</code>.
     */
    public String getDevice() {
        return device;
    }

    /**
     * Gets the primary minimum RAID type or <code>null</code> if doesn't matter.
     */
    public RaidType getPrimaryMinimumRaidType() {
        if(primaryMinimumRaidType==null) return null;
        RaidType rt=table.connector.raidTypes.get(primaryMinimumRaidType);
        if(rt==null) throw new WrappedException(new SQLException("Unable to find RaidType: "+primaryMinimumRaidType));
        return rt;
    }

    /**
     * Gets the secondary minimum RAID type or <code>null</code> if doesn't matter.
     */
    public RaidType getSecondaryMinimumRaidType() {
        if(secondaryMinimumRaidType==null) return null;
        RaidType rt=table.connector.raidTypes.get(secondaryMinimumRaidType);
        if(rt==null) throw new WrappedException(new SQLException("Unable to find RaidType: "+secondaryMinimumRaidType));
        return rt;
    }

    /**
     * Gets the primary minimum disk type or <code>null</code> if doesn't matter.
     */
    public DiskType getPrimaryMinimumDiskType() {
        if(primaryMinimumDiskType==null) return null;
        DiskType rt=table.connector.diskTypes.get(primaryMinimumDiskType);
        if(rt==null) throw new WrappedException(new SQLException("Unable to find DiskType: "+primaryMinimumDiskType));
        return rt;
    }

    /**
     * Gets the secondary minimum disk type or <code>null</code> if doesn't matter.
     */
    public DiskType getSecondaryMinimumDiskType() {
        if(secondaryMinimumDiskType==null) return null;
        DiskType rt=table.connector.diskTypes.get(secondaryMinimumDiskType);
        if(rt==null) throw new WrappedException(new SQLException("Unable to find DiskType: "+secondaryMinimumDiskType));
        return rt;
    }

    /**
     * Gets the minimum disk speed or <code>-1</code> if doesn't matter.
     */
    public int getPrimaryMinimumDiskSpeed() {
        return primaryMinimumDiskSpeed;
    }

    /**
     * Gets the minimum disk speed or <code>-1</code> if doesn't matter.
     */
    public int getSecondaryMinimumDiskSpeed() {
        return secondaryMinimumDiskSpeed;
    }

    /**
     * Gets the total extents required by this device.
     */
    public int getExtents() {
        return extents;
    }

    /**
     * Gets the primary disk weight.
     */
    public short getPrimaryWeight() {
        return primaryWeight;
    }
    
    /**
     * Gets the secondary disk weight.
     */
    public short getSecondaryWeight() {
        return secondaryWeight;
    }

    /**
     * Gets if the primary physical volumes are locked (manually configured).
     */
    public boolean getPrimaryPhysicalVolumesLocked() {
        return primaryPhysicalVolumesLocked;
    }

    /**
     * Gets if the secondary physical volumes are locked (manually configured).
     */
    public boolean getSecondaryPhysicalVolumesLocked() {
        return secondaryPhysicalVolumesLocked;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.VIRTUAL_DISKS;
    }

    public void init(ResultSet result) throws SQLException {
        int pos = 1;
        pkey = result.getInt(pos++);
        virtualServer = result.getInt(pos++);
        device = result.getString(pos++);
        primaryMinimumRaidType = result.getString(pos++);
        secondaryMinimumRaidType = result.getString(pos++);
        primaryMinimumDiskType = result.getString(pos++);
        secondaryMinimumDiskType = result.getString(pos++);
        primaryMinimumDiskSpeed = result.getInt(pos++);
        if(result.wasNull()) primaryMinimumDiskSpeed = -1;
        secondaryMinimumDiskSpeed = result.getInt(pos++);
        if(result.wasNull()) secondaryMinimumDiskSpeed = -1;
        extents = result.getInt(pos++);
        primaryWeight = result.getShort(pos++);
        secondaryWeight = result.getShort(pos++);
        primaryPhysicalVolumesLocked = result.getBoolean(pos++);
        secondaryPhysicalVolumesLocked = result.getBoolean(pos++);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey = in.readCompressedInt();
        virtualServer = in.readCompressedInt();
        device = in.readUTF().intern();
        primaryMinimumRaidType = StringUtility.intern(in.readNullUTF());
        secondaryMinimumRaidType = StringUtility.intern(in.readNullUTF());
        primaryMinimumDiskType = StringUtility.intern(in.readNullUTF());
        secondaryMinimumDiskType = StringUtility.intern(in.readNullUTF());
        primaryMinimumDiskSpeed = in.readCompressedInt();
        secondaryMinimumDiskSpeed = in.readCompressedInt();
        extents = in.readCompressedInt();
        primaryWeight = in.readShort();
        secondaryWeight = in.readShort();
        primaryPhysicalVolumesLocked = in.readBoolean();
        secondaryPhysicalVolumesLocked = in.readBoolean();
    }

    @Override
    protected String toStringImpl() {
        return getVirtualServer().toStringImpl()+":/dev/"+device;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(virtualServer);
        out.writeUTF(device);
        out.writeNullUTF(primaryMinimumRaidType);
        out.writeNullUTF(secondaryMinimumRaidType);
        out.writeNullUTF(primaryMinimumDiskType);
        out.writeNullUTF(secondaryMinimumDiskType);
        out.writeCompressedInt(primaryMinimumDiskSpeed);
        out.writeCompressedInt(secondaryMinimumDiskSpeed);
        out.writeCompressedInt(extents);
        out.writeShort(primaryWeight);
        out.writeShort(secondaryWeight);
        out.writeBoolean(primaryPhysicalVolumesLocked);
        out.writeBoolean(secondaryPhysicalVolumesLocked);
    }
}
