package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(NetDevice.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
        new OrderBy(NetDevice.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
        new OrderBy(NetDevice.COLUMN_DEVICE_ID_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public NetDevice get(Object pkey) {
	return getUniqueRow(NetDevice.COLUMN_PKEY, pkey);
    }

    public NetDevice get(int pkey) {
	return getUniqueRow(NetDevice.COLUMN_PKEY, pkey);
    }

    List<NetDevice> getNetDevices(Server se) {
        return getIndexedRows(NetDevice.COLUMN_SERVER, se.pkey);
    }

    NetDevice getNetDevice(Server se, String deviceID) {
        // Use the index first
	List<NetDevice> cached=getNetDevices(se);
	int size=cached.size();
	for(int c=0;c<size;c++) {
            NetDevice dev=cached.get(c);
            if(dev.device_id.equals(deviceID)) return dev;
	}
        return null;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.NET_DEVICES;
    }
}