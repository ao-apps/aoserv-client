package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.MySQLDatabase;
import com.aoindustries.aoserv.client.MySQLDatabaseService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryMySQLDatabaseService extends RetryServiceIntegerKey<MySQLDatabase> implements MySQLDatabaseService<RetryConnector,RetryConnectorFactory> {

    RetryMySQLDatabaseService(RetryConnector connector) {
        super(connector, MySQLDatabase.class);
    }
}
