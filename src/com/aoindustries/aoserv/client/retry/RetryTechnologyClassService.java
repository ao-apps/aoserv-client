package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.TechnologyClass;
import com.aoindustries.aoserv.client.TechnologyClassService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryTechnologyClassService extends RetryServiceStringKey<TechnologyClass> implements TechnologyClassService<RetryConnector,RetryConnectorFactory> {

    RetryTechnologyClassService(RetryConnector connector) {
        super(connector, TechnologyClass.class);
    }
}
