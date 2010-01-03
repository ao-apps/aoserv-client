package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.PostgresDatabase;
import com.aoindustries.aoserv.client.PostgresDatabaseService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryPostgresDatabaseService extends RetryServiceIntegerKey<PostgresDatabase> implements PostgresDatabaseService<RetryConnector,RetryConnectorFactory> {

    RetryPostgresDatabaseService(RetryConnector connector) {
        super(connector, PostgresDatabase.class);
    }
}
