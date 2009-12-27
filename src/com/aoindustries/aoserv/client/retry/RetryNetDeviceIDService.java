package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.NetDeviceID;
import com.aoindustries.aoserv.client.NetDeviceIDService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryNetDeviceIDService extends RetryServiceStringKey<NetDeviceID> implements NetDeviceIDService<RetryConnector,RetryConnectorFactory> {

    RetryNetDeviceIDService(RetryConnector connector) {
        super(connector, NetDeviceID.class);
    }
}
