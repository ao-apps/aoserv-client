package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.Technology;
import com.aoindustries.aoserv.client.TechnologyService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryTechnologyService extends RetryServiceIntegerKey<Technology> implements TechnologyService<RetryConnector,RetryConnectorFactory> {

    RetryTechnologyService(RetryConnector connector) {
        super(connector, Technology.class);
    }
}
