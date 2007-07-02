package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.StringUtility;
import java.io.*;
import java.sql.*;

/**
 * An <code>NetDeviceID</code> is a simple wrapper for the
 * different names of network devices used in Linux servers.
 *
 * @see  NetDevice
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class NetDeviceID extends GlobalObjectStringKey<NetDeviceID> implements Comparable<NetDeviceID> {

    static final int COLUMN_NAME=0;

    public static final String
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

    public Object getColumn(int i) {
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

    void initImpl(ResultSet results) throws SQLException {
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

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeUTF(pkey);
	out.writeBoolean(is_loopback);
    }
    
    public int compareTo(NetDeviceID other) {
        return pkey.compareTo(other.pkey);
    }
}
