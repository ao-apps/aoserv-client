package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.MySQLReservedWord;
import com.aoindustries.aoserv.client.MySQLReservedWordService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryMySQLReservedWordService extends RetryServiceStringKey<MySQLReservedWord> implements MySQLReservedWordService<RetryConnector,RetryConnectorFactory> {

    RetryMySQLReservedWordService(RetryConnector connector) {
        super(connector, MySQLReservedWord.class);
    }
}
