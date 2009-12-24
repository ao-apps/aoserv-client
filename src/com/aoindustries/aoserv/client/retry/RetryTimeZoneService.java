package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.TimeZone;
import com.aoindustries.aoserv.client.TimeZoneService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryTimeZoneService extends RetryServiceStringKey<TimeZone> implements TimeZoneService<RetryConnector,RetryConnectorFactory> {

    RetryTimeZoneService(RetryConnector connector) {
        super(connector, TimeZone.class);
    }
}
