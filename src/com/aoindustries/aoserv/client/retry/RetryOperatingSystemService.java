package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.OperatingSystem;
import com.aoindustries.aoserv.client.OperatingSystemService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryOperatingSystemService extends RetryServiceStringKey<OperatingSystem> implements OperatingSystemService<RetryConnector,RetryConnectorFactory> {

    RetryOperatingSystemService(RetryConnector connector) {
        super(connector, OperatingSystem.class);
    }
}
