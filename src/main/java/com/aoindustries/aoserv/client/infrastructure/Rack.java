/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2008, 2009, 2016, 2017, 2018, 2019, 2021, 2022  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoindustries.aoserv.client.infrastructure;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>Rack</code> stores <code>PhysicalServer</code>s.
 *
 * @author  AO Industries, Inc.
 */
public final class Rack extends CachedObjectIntegerKey<Rack> {

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

	/**
	 * @deprecated  Only required for implementation, do not use directly.
	 *
	 * @see  #init(java.sql.ResultSet)
	 * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
	 */
	@Deprecated/* Java 9: (forRemoval = true) */
	public Rack() {
		// Do nothing
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_FARM: return farm;
			case COLUMN_NAME: return name;
			case 3: return Float.isNaN(maxPower) ? null : maxPower;
			case 4: return totalRackUnits==-1 ? null : totalRackUnits;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public ServerFarm getServerFarm() throws SQLException, IOException {
		ServerFarm sf=table.getConnector().getInfrastructure().getServerFarm().get(farm);
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
	public Table.TableID getTableID() {
		return Table.TableID.RACKS;
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
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		pkey = in.readCompressedInt();
		farm = in.readUTF().intern();
		name = in.readUTF();
		maxPower = in.readFloat();
		totalRackUnits = in.readCompressedInt();
	}

	@Override
	public String toStringImpl() {
		return name;
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(farm);
		out.writeUTF(name);
		out.writeFloat(maxPower);
		out.writeCompressedInt(totalRackUnits);
	}
}
