package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServerResource;
import com.aoindustries.aoserv.client.AOServerResourceService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryAOServerResourceService extends RetryServiceIntegerKey<AOServerResource> implements AOServerResourceService<RetryConnector,RetryConnectorFactory> {

    RetryAOServerResourceService(RetryConnector connector) {
        super(connector, AOServerResource.class);
    }
}
