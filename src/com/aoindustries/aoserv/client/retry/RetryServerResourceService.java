package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.ServerResource;
import com.aoindustries.aoserv.client.ServerResourceService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryServerResourceService extends RetryServiceIntegerKey<ServerResource> implements ServerResourceService<RetryConnector,RetryConnectorFactory> {

    RetryServerResourceService(RetryConnector connector) {
        super(connector, ServerResource.class);
    }
}
