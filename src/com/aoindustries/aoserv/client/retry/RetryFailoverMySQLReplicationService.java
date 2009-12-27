package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.FailoverMySQLReplication;
import com.aoindustries.aoserv.client.FailoverMySQLReplicationService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryFailoverMySQLReplicationService extends RetryServiceIntegerKey<FailoverMySQLReplication> implements FailoverMySQLReplicationService<RetryConnector,RetryConnectorFactory> {

    RetryFailoverMySQLReplicationService(RetryConnector connector) {
        super(connector, FailoverMySQLReplication.class);
    }
}
