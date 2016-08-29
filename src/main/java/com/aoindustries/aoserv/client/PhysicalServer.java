/*
 * Copyright 2008-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.InternUtils;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>PhysicalServer</code> consumes space and electricity in a rack
 * and provides resources.
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

	public enum UpsType {
		/**
		 * No UPS is supporting this device.
		 */
		none,

		/**
		 * The UPS is provided by the datacenter, but cannot be monitored for clean shutdown.
		 */
		datacenter,

		/**
		 * The UPS is an APC model and can be monitored for clean shutdown.
		 */
		apc
	}

	private UpsType upsType;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_SERVER: return pkey;
			case 1: return rack==-1 ? null : rack;
			case 2: return rackUnits==-1 ? null : rackUnits;
			case 3: return ram==-1 ? null : ram;
			case 4: return processorType;
			case 5: return processorSpeed==-1 ? null : processorSpeed;
			case 6: return processorCores==-1 ? null : processorCores;
			case 7: return Float.isNaN(maxPower) ? null : maxPower;
			case 8: return supports_hvm;
			case 9: return upsType.name();
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public Server getServer() throws SQLException, IOException {
		Server se=table.connector.getServers().get(pkey);
		if(se==null) throw new SQLException("Unable to find Server: "+pkey);
		return se;
	}

	/**
	 * Gets the rack this server is part of or <code>null</code> if not in a rack.
	 */
	public Rack getRack() throws SQLException, IOException {
		if(rack==-1) return null;
		Rack ra = table.connector.getRacks().get(rack);
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
	public ProcessorType getProcessorType() throws SQLException, IOException {
		if(processorType==null) return null;
		ProcessorType pt = table.connector.getProcessorTypes().get(processorType);
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

	/**
	 * Gets the UPS type powering this server.
	 */
	public UpsType getUpsType() {
		return upsType;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.PHYSICAL_SERVERS;
	}

	@Override
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
		upsType = UpsType.valueOf(result.getString(pos++));
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey = in.readCompressedInt();
		rack = in.readCompressedInt();
		rackUnits = in.readShort();
		ram = in.readCompressedInt();
		processorType = InternUtils.intern(in.readNullUTF());
		processorSpeed = in.readCompressedInt();
		processorCores = in.readCompressedInt();
		maxPower = in.readFloat();
		supports_hvm = in.readBoolean() ? in.readBoolean() : null;
		upsType = UpsType.valueOf(in.readUTF());
	}

	@Override
	String toStringImpl() throws SQLException, IOException {
		return getServer().toStringImpl();
	}

	@Override
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
		if(version.compareTo(AOServProtocol.Version.VERSION_1_63)>=0) out.writeUTF(upsType.name());
	}
}
