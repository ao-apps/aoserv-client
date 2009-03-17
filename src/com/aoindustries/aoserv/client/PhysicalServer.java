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

/**
 * A <code>PhysicalServer</code> consumes space and electricity in a rack
 * and provides resources.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class PhysicalServer extends CachedObjectIntegerKey<PhysicalServer> {

    static final int COLUMN_SERVER = 0;
    
    static final String COLUMN_SERVER_name = "server";

    private int rack;
    private short rackUnits;
    private int ram;
    private String processorType;
    private int processorSpeed;
    private int processorCores;
    private float maxPower;
    private Boolean supports_hvm;

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_SERVER: return Integer.valueOf(pkey);
            case 1: return rack==-1 ? null : Integer.valueOf(rack);
            case 2: return rackUnits==-1 ? null : Short.valueOf(rackUnits);
            case 3: return ram==-1 ? null : Integer.valueOf(ram);
            case 4: return processorType;
            case 5: return processorSpeed==-1 ? null : Integer.valueOf(processorSpeed);
            case 6: return processorCores==-1 ? null : Integer.valueOf(processorCores);
            case 7: return Float.isNaN(maxPower) ? null : Float.valueOf(maxPower);
            case 8: return supports_hvm;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public Server getServer() throws SQLException, IOException {
        Server se=table.connector.servers.get(pkey);
        if(se==null) throw new SQLException("Unable to find Server: "+pkey);
        return se;
    }

    /**
     * Gets the rack this server is part of or <code>null</code> if not in a rack.
     */
    public Rack getRack() throws SQLException {
        if(rack==-1) return null;
        Rack ra = table.connector.racks.get(rack);
        if(ra==null) throw new SQLException("Unable to find Rack: "+rack);
        return ra;
    }

    /**
     * Gets the number of rack units used by this server or <code>-1</code> if unknown
     * or not applicable.
     */
    public short getRackUnits() {
        return rackUnits;
    }

    /**
     * Gets the number of megabytes of RAM in this server or <code>-1</code> if not applicable.
     */
    public int getRam() {
        return ram;
    }

    /**
     * Gets the processor type or <code>null</code> if not applicable.
     */
    public ProcessorType getProcessorType() throws SQLException {
        if(processorType==null) return null;
        ProcessorType pt = table.connector.processorTypes.get(processorType);
        if(pt==null) throw new SQLException("Unable to find ProcessorType: "+processorType);
        return pt;
    }

    /**
     * Gets the processor speed in MHz or <code>-1</code> if not applicable.
     */
    public int getProcessorSpeed() {
        return processorSpeed;
    }

    /**
     * Gets the total number of processor cores or <code>-1</code> if not applicable,
     * different hyperthreads are counted as separate cores.
     */
    public int getProcessorCores() {
        return processorCores;
    }
    
    /**
     * Gets the maximum electricity current or <code>Float.NaN</code> if not known.
     */
    public float getMaxPower() {
        return maxPower;
    }
    
    /**
     * Gets if this supports HVM or <code>null</code> if not applicable.
     */
    public Boolean getSupportsHvm() {
        return supports_hvm;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.PHYSICAL_SERVERS;
    }

    public void init(ResultSet result) throws SQLException {
        int pos = 1;
        pkey = result.getInt(pos++);
        rack = result.getInt(pos++);
        if(result.wasNull()) rack = -1;
        rackUnits = result.getShort(pos++);
        if(result.wasNull()) rackUnits = -1;
        ram = result.getInt(pos++);
        if(result.wasNull()) ram = -1;
        processorType = result.getString(pos++);
        processorSpeed = result.getInt(pos++);
        if(result.wasNull()) processorSpeed = -1;
        processorCores = result.getInt(pos++);
        if(result.wasNull()) processorCores = -1;
        maxPower = result.getFloat(pos++);
        if(result.wasNull()) maxPower = Float.NaN;
        supports_hvm = result.getBoolean(pos++);
        if(result.wasNull()) supports_hvm = null;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey = in.readCompressedInt();
        rack = in.readCompressedInt();
        rackUnits = in.readShort();
        ram = in.readCompressedInt();
        processorType = StringUtility.intern(in.readNullUTF());
        processorSpeed = in.readCompressedInt();
        processorCores = in.readCompressedInt();
        maxPower = in.readFloat();
        supports_hvm = in.readBoolean() ? in.readBoolean() : null;
    }

    @Override
    protected String toStringImpl() throws SQLException, IOException {
        return getServer().toStringImpl();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(rack);
        out.writeShort(rackUnits);
        out.writeCompressedInt(ram);
        out.writeNullUTF(processorType);
        out.writeCompressedInt(processorSpeed);
        out.writeCompressedInt(processorCores);
        out.writeFloat(maxPower);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_37)>=0) {
            out.writeBoolean(supports_hvm!=null);
            if(supports_hvm!=null) out.writeBoolean(supports_hvm);
        }
    }
}
