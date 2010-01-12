package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.NetDevice;
import com.aoindustries.aoserv.client.NetDeviceService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedNetDeviceService extends CachedServiceIntegerKey<NetDevice> implements NetDeviceService<CachedConnector,CachedConnectorFactory> {

    CachedNetDeviceService(CachedConnector connector, NetDeviceService<?,?> wrapped) {
        super(connector, NetDevice.class, wrapped);
    }
}
