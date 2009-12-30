package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.MySQLUser;
import com.aoindustries.aoserv.client.MySQLUserService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryMySQLUserService extends RetryServiceIntegerKey<MySQLUser> implements MySQLUserService<RetryConnector,RetryConnectorFactory> {

    RetryMySQLUserService(RetryConnector connector) {
        super(connector, MySQLUser.class);
    }
}
