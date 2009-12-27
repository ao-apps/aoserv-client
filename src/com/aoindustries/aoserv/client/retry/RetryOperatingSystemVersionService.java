package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.OperatingSystemVersion;
import com.aoindustries.aoserv.client.OperatingSystemVersionService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryOperatingSystemVersionService extends RetryServiceIntegerKey<OperatingSystemVersion> implements OperatingSystemVersionService<RetryConnector,RetryConnectorFactory> {

    RetryOperatingSystemVersionService(RetryConnector connector) {
        super(connector, OperatingSystemVersion.class);
    }
}
