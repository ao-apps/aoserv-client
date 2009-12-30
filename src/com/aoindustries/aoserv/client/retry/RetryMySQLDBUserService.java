package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.MySQLDBUser;
import com.aoindustries.aoserv.client.MySQLDBUserService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryMySQLDBUserService extends RetryServiceIntegerKey<MySQLDBUser> implements MySQLDBUserService<RetryConnector,RetryConnectorFactory> {

    RetryMySQLDBUserService(RetryConnector connector) {
        super(connector, MySQLDBUser.class);
    }
}
