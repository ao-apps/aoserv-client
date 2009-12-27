package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.TechnologyName;
import com.aoindustries.aoserv.client.TechnologyNameService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryTechnologyNameService extends RetryServiceStringKey<TechnologyName> implements TechnologyNameService<RetryConnector,RetryConnectorFactory> {

    RetryTechnologyNameService(RetryConnector connector) {
        super(connector, TechnologyName.class);
    }
}
