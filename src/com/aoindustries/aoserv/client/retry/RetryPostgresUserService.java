package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.PostgresUser;
import com.aoindustries.aoserv.client.PostgresUserService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryPostgresUserService extends RetryServiceIntegerKey<PostgresUser> implements PostgresUserService<RetryConnector,RetryConnectorFactory> {

    RetryPostgresUserService(RetryConnector connector) {
        super(connector, PostgresUser.class);
    }
}
