package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.FailoverFileReplication;
import com.aoindustries.aoserv.client.FailoverFileReplicationService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryFailoverFileReplicationService extends RetryServiceIntegerKey<FailoverFileReplication> implements FailoverFileReplicationService<RetryConnector,RetryConnectorFactory> {

    RetryFailoverFileReplicationService(RetryConnector connector) {
        super(connector, FailoverFileReplication.class);
    }
}
