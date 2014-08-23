/*
 * Copyright 2001-2013 by AO Industries, Inc.,
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
 * An <code>NetDeviceID</code> is a simple wrapper for the
 * different names of network devices used in Linux servers.
 *
 * @see  NetDevice
 *
 * @author  AO Industries, Inc.
 */
final public class NetDeviceID extends GlobalObjectStringKey<NetDeviceID> implements Comparable<NetDeviceID> {

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

	Object getColumnImpl(int i) {
		if(i==COLUMN_NAME) return pkey;
		if(i==1) return is_loopback?Boolean.TRUE:Boolean.FALSE;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public String getName() {
		return pkey;
	}

	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.NET_DEVICE_IDS;
	}

	public void init(ResultSet results) throws SQLException {
		pkey=results.getString(1);
		is_loopback=results.getBoolean(2);
	}

	public boolean isLoopback() {
		return is_loopback;
	}

	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF().intern();
		is_loopback=in.readBoolean();
	}

	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey);
		out.writeBoolean(is_loopback);
	}

	public int compareTo(NetDeviceID other) {
		return pkey.compareTo(other.pkey);
	}
}
