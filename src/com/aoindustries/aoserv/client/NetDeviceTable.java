package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  NetDevice
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class NetDeviceTable extends CachedTableIntegerKey<NetDevice> {

    NetDeviceTable(AOServConnector connector) {
	super(connector, NetDevice.class);
    }

    public NetDevice get(Object pkey) {
	return getUniqueRow(NetDevice.COLUMN_PKEY, pkey);
    }

    public NetDevice get(int pkey) {
	return getUniqueRow(NetDevice.COLUMN_PKEY, pkey);
    }

    List<NetDevice> getNetDevices(AOServer ao) {
        return getIndexedRows(NetDevice.COLUMN_AO_SERVER, ao.pkey);
    }

    NetDevice getNetDevice(AOServer ao, String deviceID) {
        // Use the index first
	List<NetDevice> cached=getNetDevices(ao);
	int size=cached.size();
	for(int c=0;c<size;c++) {
            NetDevice dev=cached.get(c);
            if(dev.device_id.equals(deviceID)) return dev;
	}
        return null;
    }

    int getTableID() {
	return SchemaTable.NET_DEVICES;
    }
}