package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.HttpdSite;
import com.aoindustries.aoserv.client.HttpdSiteService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryHttpdSiteService extends RetryServiceIntegerKey<HttpdSite> implements HttpdSiteService<RetryConnector,RetryConnectorFactory> {

    RetryHttpdSiteService(RetryConnector connector) {
        super(connector, HttpdSite.class);
    }
}
