package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.Resource;
import com.aoindustries.aoserv.client.ResourceService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryResourceService extends RetryServiceIntegerKey<Resource> implements ResourceService<RetryConnector,RetryConnectorFactory> {

    RetryResourceService(RetryConnector connector) {
        super(connector, Resource.class);
    }
}
