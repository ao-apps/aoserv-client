package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.NetProtocol;
import com.aoindustries.aoserv.client.NetProtocolService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryNetProtocolService extends RetryServiceStringKey<NetProtocol> implements NetProtocolService<RetryConnector,RetryConnectorFactory> {

    RetryNetProtocolService(RetryConnector connector) {
        super(connector, NetProtocol.class);
    }
}
