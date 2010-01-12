package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServerDaemonHost;
import com.aoindustries.aoserv.client.AOServerDaemonHostService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryAOServerDaemonHostService extends RetryServiceIntegerKey<AOServerDaemonHost> implements AOServerDaemonHostService<RetryConnector,RetryConnectorFactory> {

    RetryAOServerDaemonHostService(RetryConnector connector) {
        super(connector, AOServerDaemonHost.class);
    }
}
