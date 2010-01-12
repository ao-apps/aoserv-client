package com.aoindustries.aoserv.client.retry;

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
final class RetryIPAddressService extends RetryServiceIntegerKey<IPAddress> implements IPAddressService<RetryConnector,RetryConnectorFactory> {

    RetryIPAddressService(RetryConnector connector) {
        super(connector, IPAddress.class);
    }
}
