package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.PostgresVersion;
import com.aoindustries.aoserv.client.PostgresVersionService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryPostgresVersionService extends RetryServiceIntegerKey<PostgresVersion> implements PostgresVersionService<RetryConnector,RetryConnectorFactory> {

    RetryPostgresVersionService(RetryConnector connector) {
        super(connector, PostgresVersion.class);
    }
}
