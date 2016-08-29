/*
 * Copyright 2008-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>Rack</code> stores <code>PhysicalServer</code>s.
 *
 * @author  AO Industries, Inc.
 */
final public class Rack extends CachedObjectIntegerKey<Rack> {

	static final int
		COLUMN_PKEY=0,
		COLUMN_FARM=1,
		COLUMN_NAME=2
	;
	static final String COLUMN_FARM_name = "farm";
	static final String COLUMN_NAME_name = "name";

	private String farm;
	private String name;
	private float maxPower;
	private int totalRackUnits;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_FARM: return farm;
			case COLUMN_NAME: return name;
			case 3: return Float.isNaN(maxPower) ? null : maxPower;
			case 4: return totalRackUnits==-1 ? null : totalRackUnits;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public ServerFarm getServerFarm() throws SQLException, IOException {
		ServerFarm sf=table.connector.getServerFarms().get(farm);
		if(sf==null) throw new SQLException("Unable to find ServerFarm: "+farm);
		return sf;
	}

	public String getName() {
		return name;
	}

	/**
	 * Gets the max power for the rack or <code>Float.NaN</code> if unknown.
	 */
	public float getMaxPower() {
		return maxPower;
	}

	/**
	 * Gets the total rack units or <code>-1</code> if unknown.
	 */
	public int getTotalRackUnits() {
		return totalRackUnits;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.RACKS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getInt(1);
		farm = result.getString(2);
		name = result.getString(3);
		maxPower = result.getFloat(4);
		if(result.wasNull()) maxPower = Float.NaN;
		totalRackUnits = result.getInt(5);
		if(result.wasNull()) totalRackUnits = -1;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey = in.readCompressedInt();
		farm = in.readUTF().intern();
		name = in.readUTF();
		maxPower = in.readFloat();
		totalRackUnits = in.readCompressedInt();
	}

	@Override
	String toStringImpl() {
		return name;
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(farm);
		out.writeUTF(name);
		out.writeFloat(maxPower);
		out.writeCompressedInt(totalRackUnits);
	}
}
