package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.PostgresReservedWord;
import com.aoindustries.aoserv.client.PostgresReservedWordService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryPostgresReservedWordService extends RetryServiceStringKey<PostgresReservedWord> implements PostgresReservedWordService<RetryConnector,RetryConnectorFactory> {

    RetryPostgresReservedWordService(RetryConnector connector) {
        super(connector, PostgresReservedWord.class);
    }
}
