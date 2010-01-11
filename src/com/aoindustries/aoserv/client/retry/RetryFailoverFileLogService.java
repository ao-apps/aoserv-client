package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.FailoverFileLog;
import com.aoindustries.aoserv.client.FailoverFileLogService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryFailoverFileLogService extends RetryServiceIntegerKey<FailoverFileLog> implements FailoverFileLogService<RetryConnector,RetryConnectorFactory> {

    RetryFailoverFileLogService(RetryConnector connector) {
        super(connector, FailoverFileLog.class);
    }
}
