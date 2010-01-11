package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.FailoverFileSchedule;
import com.aoindustries.aoserv.client.FailoverFileScheduleService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryFailoverFileScheduleService extends RetryServiceIntegerKey<FailoverFileSchedule> implements FailoverFileScheduleService<RetryConnector,RetryConnectorFactory> {

    RetryFailoverFileScheduleService(RetryConnector connector) {
        super(connector, FailoverFileSchedule.class);
    }
}
