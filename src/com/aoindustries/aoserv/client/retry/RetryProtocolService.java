package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.Protocol;
import com.aoindustries.aoserv.client.ProtocolService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryProtocolService extends RetryServiceStringKey<Protocol> implements ProtocolService<RetryConnector,RetryConnectorFactory> {

    RetryProtocolService(RetryConnector connector) {
        super(connector, Protocol.class);
    }
}
