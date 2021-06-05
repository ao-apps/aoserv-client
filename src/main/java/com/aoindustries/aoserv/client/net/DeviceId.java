/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.net;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An <code>NetDeviceID</code> is a simple wrapper for the
 * different names of network devices used in Linux servers.
 *
 * @see  Device
 *
 * @author  AO Industries, Inc.
 */
final public class DeviceId extends GlobalObjectStringKey<DeviceId> implements Comparable<DeviceId> {

	static final int COLUMN_NAME=0;
	static final String COLUMN_NAME_name = "name";

	public static final String
		BMC="bmc",
		BOND0="bond0",
		BOND1="bond1",
		BOND2="bond2",
		LO="lo",
		ETH0="eth0",
		ETH1="eth1",
		ETH2="eth2",
		ETH3="eth3",
		ETH4="eth4",
		ETH5="eth5",
		ETH6="eth6"
	;

	private boolean is_loopback;

	@Override
	protected Object getColumnImpl(int i) {
		if(i==COLUMN_NAME) return pkey;
		if(i==1) return is_loopback;
		throw new IllegalArgumentException("Invalid index: " + i);
	}

	public String getName() {
		return pkey;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.NET_DEVICE_IDS;
	}

	@Override
	public void init(ResultSet results) throws SQLException {
		pkey=results.getString(1);
		is_loopback=results.getBoolean(2);
	}

	public boolean isLoopback() {
		return is_loopback;
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		pkey=in.readUTF().intern();
		is_loopback=in.readBoolean();
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey);
		out.writeBoolean(is_loopback);
	}

	@Override
	public int compareTo(DeviceId other) {
		return pkey.compareTo(other.getName());
	}
}
