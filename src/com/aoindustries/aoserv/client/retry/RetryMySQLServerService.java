package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.MySQLServer;
import com.aoindustries.aoserv.client.MySQLServerService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryMySQLServerService extends RetryServiceIntegerKey<MySQLServer> implements MySQLServerService<RetryConnector,RetryConnectorFactory> {

    RetryMySQLServerService(RetryConnector connector) {
        super(connector, MySQLServer.class);
    }
}
