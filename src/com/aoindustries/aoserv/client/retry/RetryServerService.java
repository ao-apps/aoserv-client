package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.Server;
import com.aoindustries.aoserv.client.ServerService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryServerService extends RetryServiceIntegerKey<Server> implements ServerService<RetryConnector,RetryConnectorFactory> {

    RetryServerService(RetryConnector connector) {
        super(connector, Server.class);
    }
}
