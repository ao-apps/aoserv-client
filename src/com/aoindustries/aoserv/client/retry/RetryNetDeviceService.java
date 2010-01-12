package com.aoindustries.aoserv.client.retry;

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
final class RetryNetDeviceService extends RetryServiceIntegerKey<NetDevice> implements NetDeviceService<RetryConnector,RetryConnectorFactory> {

    RetryNetDeviceService(RetryConnector connector) {
        super(connector, NetDevice.class);
    }
}
