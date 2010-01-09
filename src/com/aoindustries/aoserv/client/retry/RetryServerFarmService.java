package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.ServerFarm;
import com.aoindustries.aoserv.client.ServerFarmService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryServerFarmService extends RetryServiceDomainLabelKey<ServerFarm> implements ServerFarmService<RetryConnector,RetryConnectorFactory> {

    RetryServerFarmService(RetryConnector connector) {
        super(connector, ServerFarm.class);
    }
}
