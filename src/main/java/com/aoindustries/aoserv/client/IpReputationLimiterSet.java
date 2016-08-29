/*
 * Copyright 2012, 2016 by AO Industries, Inc.,
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
 * One set used by a <code>IpReputationLimiter</code>.
 *
 * @author  AO Industries, Inc.
 */
final public class IpReputationLimiterSet extends CachedObjectIntegerKey<IpReputationLimiterSet> {

	static final int
		COLUMN_PKEY    = 0,
		COLUMN_LIMITER = 1
	;
	static final String
		COLUMN_LIMITER_name = "limiter",
		COLUMN_SORT_ORDER_name = "sort_order"
	;

	int limiter;
	private int set;
	private short sortOrder;

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.IP_REPUTATION_LIMITER_SETS;
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
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(limiter);
		out.writeCompressedInt(set);
		out.writeShort        (sortOrder);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey      = in.readCompressedInt();
		limiter   = in.readCompressedInt();
		set       = in.readCompressedInt();
		sortOrder = in.readShort();
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY     : return pkey;
			case COLUMN_LIMITER  : return limiter;
			case 2               : return set;
			case 3               : return sortOrder;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public IpReputationLimiter getLimiter() throws SQLException, IOException {
		IpReputationLimiter obj = table.connector.getIpReputationLimiters().get(limiter);
		if(obj==null) throw new SQLException("Unable to find IpReputationLimiter: " + limiter);
		return obj;
	}

	public IpReputationSet getSet() throws SQLException, IOException {
		IpReputationSet obj = table.connector.getIpReputationSets().get(set);
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
