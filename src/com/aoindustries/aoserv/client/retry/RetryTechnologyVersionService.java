package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.TechnologyVersion;
import com.aoindustries.aoserv.client.TechnologyVersionService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryTechnologyVersionService extends RetryServiceIntegerKey<TechnologyVersion> implements TechnologyVersionService<RetryConnector,RetryConnectorFactory> {

    RetryTechnologyVersionService(RetryConnector connector) {
        super(connector, TechnologyVersion.class);
    }
}
