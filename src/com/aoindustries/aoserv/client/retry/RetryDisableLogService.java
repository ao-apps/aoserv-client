package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.DisableLog;
import com.aoindustries.aoserv.client.DisableLogService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryDisableLogService extends RetryServiceIntegerKey<DisableLog> implements DisableLogService<RetryConnector,RetryConnectorFactory> {

    RetryDisableLogService(RetryConnector connector) {
        super(connector, DisableLog.class);
    }
}
