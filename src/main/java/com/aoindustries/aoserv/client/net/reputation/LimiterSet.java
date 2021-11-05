/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2012, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.net.reputation;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * One set used by a <code>IpReputationLimiter</code>.
 *
 * @author  AO Industries, Inc.
 */
public final class LimiterSet extends CachedObjectIntegerKey<LimiterSet> {

	static final int
		COLUMN_PKEY    = 0,
		COLUMN_LIMITER = 1
	;
	static final String
		COLUMN_LIMITER_name = "limiter",
		COLUMN_SORT_ORDER_name = "sort_order"
	;

	private int limiter;
	private int set;
	private short sortOrder;

	/**
	 * @deprecated  Only required for implementation, do not use directly.
	 *
	 * @see  #init(java.sql.ResultSet)
	 * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
	 */
	@Deprecated/* Java 9: (forRemoval = true) */
	public LimiterSet() {
		// Do nothing
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.IP_REPUTATION_LIMITER_SETS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		int pos = 1;
		pkey      = result.getInt(pos++);
		limiter   = result.getInt(pos++);
		set       = result.getInt(pos++);
		sortOrder = result.getShort(pos++);
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(limiter);
		out.writeCompressedInt(set);
		out.writeShort        (sortOrder);
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		pkey      = in.readCompressedInt();
		limiter   = in.readCompressedInt();
		set       = in.readCompressedInt();
		sortOrder = in.readShort();
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY     : return pkey;
			case COLUMN_LIMITER  : return limiter;
			case 2               : return set;
			case 3               : return sortOrder;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public Limiter getLimiter() throws SQLException, IOException {
		Limiter obj = table.getConnector().getNet().getReputation().getLimiter().get(limiter);
		if(obj==null) throw new SQLException("Unable to find IpReputationLimiter: " + limiter);
		return obj;
	}

	public Set getSet() throws SQLException, IOException {
		Set obj = table.getConnector().getNet().getReputation().getSet().get(set);
		if(obj==null) throw new SQLException("Unable to find IpReputationSet: " + set);
		return obj;
	}

	/**
	 * Gets the per-limiter sort ordering.  This controls the preference.
	 */
	public short getSortOrder() {
		return sortOrder;
	}
}
