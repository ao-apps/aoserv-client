package com.aoindustries.aoserv.client.noswing;

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
final class NoSwingNetDeviceIDService extends NoSwingServiceStringKey<NetDeviceID> implements NetDeviceIDService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingNetDeviceIDService(NoSwingConnector connector, NetDeviceIDService<?,?> wrapped) {
        super(connector, NetDeviceID.class, wrapped);
    }
}
