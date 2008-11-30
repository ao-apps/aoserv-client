package com.aoindustries.aoserv.client;

/*
 * Copyright 2008 by AO Industries, Inc.,
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
    private String primaryMinimumProcessorType;
    private String secondaryMinimumProcessorType;
    private String minimumProcessorArchitecture;
    private int primaryMinimumProcessorSpeed;
    private int secondaryMinimumProcessorSpeed;
    private short primaryProcessorCores;
    private short secondaryProcessorCores;
    private short primaryProcessorWeight;
    private short secondaryProcessorWeight;
    private int primaryPhysicalServer;
    private boolean primaryPhysicalServerLocked;
    private int secondaryPhysicalServer;
    private boolean secondaryPhysicalServerLocked;
    private boolean requires_hvm;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_SERVER: return Integer.valueOf(pkey);
            case 1 : return primaryRam;
            case 2 : return secondaryRam==-1 ? null : Integer.valueOf(secondaryRam);
            case 3 : return primaryMinimumProcessorType;
            case 4 : return secondaryMinimumProcessorType;
            case 5 : return minimumProcessorArchitecture;
            case 6 : return primaryMinimumProcessorSpeed==-1 ? null : Integer.valueOf(primaryMinimumProcessorSpeed);
            case 7 : return secondaryMinimumProcessorSpeed==-1 ? null : Integer.valueOf(secondaryMinimumProcessorSpeed);
            case 8 : return primaryProcessorCores;
            case 9 : return secondaryProcessorCores==-1 ? null : Short.valueOf(secondaryProcessorCores);
            case 10 : return primaryProcessorWeight;
            case 11 : return secondaryProcessorWeight==-1 ? null : Short.valueOf(secondaryProcessorWeight);
            case 12 : return primaryPhysicalServer;
            case 13 : return primaryPhysicalServerLocked;
            case 14 : return secondaryPhysicalServer;
            case 15 : return secondaryPhysicalServerLocked;
            case 16 : return requires_hvm;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public Server getServer() {
        Server se=table.connector.servers.get(pkey);
        if(se==null) throw new WrappedException(new SQLException("Unable to find Server: "+pkey));
        return se;
    }

    public int getPrimaryRam() {
        return primaryRam;
    }

    /**
     * Gets the secondary RAM allocation or <code>-1</code> if no secondary required.
     */
    public int getSecondaryRam() {
        return secondaryRam;
    }
    
    /**
     * Gets the primary minimum processor type or <code>null</code> if none.
     */
    public ProcessorType getPrimaryMinimumProcessorType() {
        if(primaryMinimumProcessorType==null) return null;
        ProcessorType pt = table.connector.processorTypes.get(primaryMinimumProcessorType);
        if(pt==null) throw new WrappedException(new SQLException("Unable to find ProcessorType: "+primaryMinimumProcessorType));
        return pt;
    }
    
    /**
     * Gets the secondary minimum processor type or <code>null</code> if none.
     */
    public ProcessorType getSecondaryMinimumProcessorType() {
        if(secondaryMinimumProcessorType==null) return null;
        ProcessorType pt = table.connector.processorTypes.get(secondaryMinimumProcessorType);
        if(pt==null) throw new WrappedException(new SQLException("Unable to find ProcessorType: "+secondaryMinimumProcessorType));
        return pt;
    }
    
    /**
     * Gets the minimum processor architecture.
     */
    public Architecture getMinimumProcessorArchitecture() {
        Architecture a = table.connector.architectures.get(minimumProcessorArchitecture);
        if(a==null) throw new WrappedException(new SQLException("Unable to find Architecture: "+minimumProcessorArchitecture));
        return a;
    }

    /**
     * Gets the primary minimum processor speed or <code>-1</code> for none.
     */
    public int getPrimaryMinimumProcessorSpeed() {
        return primaryMinimumProcessorSpeed;
    }

    /**
     * Gets the secondary minimum processor speed or <code>-1</code> for none.
     */
    public int getSecondaryMinimumProcessorSpeed() {
        return secondaryMinimumProcessorSpeed;
    }

    /**
     * Gets the primary processor cores.
     */
    public short getPrimaryProcessorCores() {
        return primaryProcessorCores;
    }

    /**
     * Gets the secondary processor cores or <code>-1</code> if no secondary required.
     */
    public short getSecondaryProcessorCores() {
        return secondaryProcessorCores;
    }

    /**
     * Gets the primary processor weight.
     */
    public short getPrimaryProcessorWeight() {
        return primaryProcessorWeight;
    }

    /**
     * Gets the secondary processor weight or <code>-1</code> if no secondary is required.
     */
    public short getSecondaryProcessorWeight() {
        return secondaryProcessorWeight;
    }

    /**
     * Gets the primary physical server.
     */
    public PhysicalServer getPrimaryPhysicalServer() {
        PhysicalServer ps = table.connector.physicalServers.get(primaryPhysicalServer);
        if(ps==null) throw new WrappedException(new SQLException("Unable to find PhysicalServer: "+primaryPhysicalServer));
        return ps;
    }
    
    /**
     * Gets if the primary server is locked (manually set).
     */
    public boolean isPrimaryPhysicalServerLocked() {
        return primaryPhysicalServerLocked;
    }
    
    /**
     * Gets the secondary physical server.
     */
    public PhysicalServer getSecondaryPhysicalServer() {
        PhysicalServer ps = table.connector.physicalServers.get(secondaryPhysicalServer);
        if(ps==null) throw new WrappedException(new SQLException("Unable to find PhysicalServer: "+secondaryPhysicalServer));
        return ps;
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
        primaryMinimumProcessorType = result.getString(pos++);
        secondaryMinimumProcessorType = result.getString(pos++);
        minimumProcessorArchitecture = result.getString(pos++);
        primaryMinimumProcessorSpeed = result.getInt(pos++);
        if(result.wasNull()) primaryMinimumProcessorSpeed = -1;
        secondaryMinimumProcessorSpeed = result.getInt(pos++);
        if(result.wasNull()) secondaryMinimumProcessorSpeed = -1;
        primaryProcessorCores = result.getShort(pos++);
        secondaryProcessorCores = result.getShort(pos++);
        if(result.wasNull()) secondaryProcessorCores = -1;
        primaryProcessorWeight = result.getShort(pos++);
        secondaryProcessorWeight = result.getShort(pos++);
        if(result.wasNull()) secondaryProcessorWeight = -1;
        primaryPhysicalServer = result.getInt(pos++);
        primaryPhysicalServerLocked = result.getBoolean(pos++);
        secondaryPhysicalServer = result.getInt(pos++);
        secondaryPhysicalServerLocked = result.getBoolean(pos++);
        requires_hvm = result.getBoolean(pos++);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey = in.readCompressedInt();
        primaryRam = in.readCompressedInt();
        secondaryRam = in.readCompressedInt();
        primaryMinimumProcessorType = StringUtility.intern(in.readNullUTF());
        secondaryMinimumProcessorType = StringUtility.intern(in.readNullUTF());
        minimumProcessorArchitecture = in.readUTF().intern();
        primaryMinimumProcessorSpeed = in.readCompressedInt();
        secondaryMinimumProcessorSpeed = in.readCompressedInt();
        primaryProcessorCores = in.readShort();
        secondaryProcessorCores = in.readShort();
        primaryProcessorWeight = in.readShort();
        secondaryProcessorWeight = in.readShort();
        primaryPhysicalServer = in.readCompressedInt();
        primaryPhysicalServerLocked = in.readBoolean();
        secondaryPhysicalServer = in.readCompressedInt();
        secondaryPhysicalServerLocked = in.readBoolean();
        requires_hvm = in.readBoolean();
    }

    @Override
    protected String toStringImpl() {
        return getServer().toStringImpl();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(primaryRam);
        out.writeCompressedInt(secondaryRam);
        out.writeNullUTF(primaryMinimumProcessorType);
        out.writeNullUTF(secondaryMinimumProcessorType);
        out.writeUTF(minimumProcessorArchitecture);
        out.writeCompressedInt(primaryMinimumProcessorSpeed);
        out.writeCompressedInt(secondaryMinimumProcessorSpeed);
        out.writeShort(primaryProcessorCores);
        out.writeShort(secondaryProcessorCores);
        out.writeShort(primaryProcessorWeight);
        out.writeShort(secondaryProcessorWeight);
        out.writeCompressedInt(primaryPhysicalServer);
        out.writeBoolean(primaryPhysicalServerLocked);
        out.writeCompressedInt(secondaryPhysicalServer);
        out.writeBoolean(secondaryPhysicalServerLocked);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_37)>=0) out.writeBoolean(requires_hvm);
    }
}
