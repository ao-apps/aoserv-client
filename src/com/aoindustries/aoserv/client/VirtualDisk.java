package com.aoindustries.aoserv.client;

/*
 * Copyright 2008-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
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
    private int minimumDiskSpeed;
    private int minimumDiskSpeedTarget;
    private int extents;
    private short weight;
    private short weightTarget;

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return pkey;
            case COLUMN_VIRTUAL_SERVER : return virtualServer;
            case COLUMN_DEVICE : return device;
            case 3 : return minimumDiskSpeed==-1 ? null : Integer.valueOf(minimumDiskSpeed);
            case 4 : return minimumDiskSpeedTarget==-1 ? null : Integer.valueOf(minimumDiskSpeedTarget);
            case 5 : return extents;
            case 6 : return weight;
            case 7 : return weightTarget;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public VirtualServer getVirtualServer() throws SQLException, IOException {
        VirtualServer vs=table.connector.getVirtualServers().get(virtualServer);
        if(vs==null) throw new SQLException("Unable to find VirtualServer: "+virtualServer);
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
     * Gets the minimum disk speed or <code>-1</code> if doesn't matter.
     */
    public int getMinimumDiskSpeed() {
        return minimumDiskSpeed;
    }

    /**
     * Gets the minimum disk speed target or <code>-1</code> if doesn't matter.
     */
    public int getMinimumDiskSpeedTarget() {
        return minimumDiskSpeedTarget;
    }

    /**
     * Gets the total extents required by this device.
     */
    public int getExtents() {
        return extents;
    }

    /**
     * Gets the disk weight.
     */
    public short getWeight() {
        return weight;
    }

    /**
     * Gets the disk weight target.
     */
    public short getWeightTarget() {
        return weightTarget;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.VIRTUAL_DISKS;
    }

    public void init(ResultSet result) throws SQLException {
        int pos = 1;
        pkey = result.getInt(pos++);
        virtualServer = result.getInt(pos++);
        device = result.getString(pos++);
        minimumDiskSpeed = result.getInt(pos++);
        if(result.wasNull()) minimumDiskSpeed = -1;
        minimumDiskSpeedTarget = result.getInt(pos++);
        if(result.wasNull()) minimumDiskSpeedTarget = -1;
        extents = result.getInt(pos++);
        weight = result.getShort(pos++);
        weightTarget = result.getShort(pos++);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey = in.readCompressedInt();
        virtualServer = in.readCompressedInt();
        device = in.readUTF().intern();
        minimumDiskSpeed = in.readCompressedInt();
        minimumDiskSpeedTarget = in.readCompressedInt();
        extents = in.readCompressedInt();
        weight = in.readShort();
        weightTarget = in.readShort();
    }

    @Override
    protected String toStringImpl() throws SQLException, IOException {
        return getVirtualServer().toStringImpl()+":/dev/"+device;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(virtualServer);
        out.writeUTF(device);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_41)<=0) out.writeNullUTF(null); // primaryMinimumRaidType
        if(version.compareTo(AOServProtocol.Version.VERSION_1_40)<=0) out.writeNullUTF(null); // secondaryMinimumRaidType
        if(version.compareTo(AOServProtocol.Version.VERSION_1_41)<=0) out.writeNullUTF(null); // primaryMinimumDiskType
        if(version.compareTo(AOServProtocol.Version.VERSION_1_40)<=0) out.writeNullUTF(null); // secondaryMinimumDiskType
        out.writeCompressedInt(minimumDiskSpeed);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_43)>=0) out.writeCompressedInt(minimumDiskSpeedTarget);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_40)<=0) out.writeCompressedInt(minimumDiskSpeed);
        out.writeCompressedInt(extents);
        out.writeShort(weight);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_43)>=0) out.writeShort(weightTarget);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_40)<=0) out.writeShort(weight);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_42)<=0) {
            out.writeBoolean(false); // primaryPhysicalVolumesLocked
            out.writeBoolean(false); // secondaryPhysicalVolumesLocked
        }
    }
}
