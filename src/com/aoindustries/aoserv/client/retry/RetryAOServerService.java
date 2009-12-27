package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServer;
import com.aoindustries.aoserv.client.AOServerService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryAOServerService extends RetryServiceIntegerKey<AOServer> implements AOServerService<RetryConnector,RetryConnectorFactory> {

    RetryAOServerService(RetryConnector connector) {
        super(connector, AOServer.class);
    }
}
