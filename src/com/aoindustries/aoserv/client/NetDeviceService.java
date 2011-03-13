/*
 * Copyright 2001-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  NetDevice
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.net_devices)
public interface NetDeviceService extends AOServService<Integer,NetDevice> {

    /* TODO
    List<NetDevice> getNetDevices(Server se) throws IOException, SQLException {
        return getIndexedRows(NetDevice.COLUMN_SERVER, se.pkey);
    }

    NetDevice getNetDevice(Server se, String deviceID) throws IOException, SQLException {
        // Use the index first
	List<NetDevice> cached=getNetDevices(se);
	int size=cached.size();
	for(int c=0;c<size;c++) {
            NetDevice dev=cached.get(c);
            if(dev.device_id.equals(deviceID)) return dev;
	}
        return null;
    }
    */
}