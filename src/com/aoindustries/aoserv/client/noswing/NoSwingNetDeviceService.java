package com.aoindustries.aoserv.client.noswing;

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
final class NoSwingNetDeviceService extends NoSwingServiceIntegerKey<NetDevice> implements NetDeviceService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingNetDeviceService(NoSwingConnector connector, NetDeviceService<?,?> wrapped) {
        super(connector, NetDevice.class, wrapped);
    }
}
