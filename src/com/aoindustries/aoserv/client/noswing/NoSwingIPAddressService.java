package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.IPAddress;
import com.aoindustries.aoserv.client.IPAddressService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingIPAddressService extends NoSwingServiceIntegerKey<IPAddress> implements IPAddressService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingIPAddressService(NoSwingConnector connector, IPAddressService<?,?> wrapped) {
        super(connector, IPAddress.class, wrapped);
    }
}
