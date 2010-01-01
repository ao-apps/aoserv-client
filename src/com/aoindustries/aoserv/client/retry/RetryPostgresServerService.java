package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.PostgresServer;
import com.aoindustries.aoserv.client.PostgresServerService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryPostgresServerService extends RetryServiceIntegerKey<PostgresServer> implements PostgresServerService<RetryConnector,RetryConnectorFactory> {

    RetryPostgresServerService(RetryConnector connector) {
        super(connector, PostgresServer.class);
    }
}
