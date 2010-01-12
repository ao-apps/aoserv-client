package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.BusinessServer;
import com.aoindustries.aoserv.client.BusinessServerService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryBusinessServerService extends RetryServiceIntegerKey<BusinessServer> implements BusinessServerService<RetryConnector,RetryConnectorFactory> {

    RetryBusinessServerService(RetryConnector connector) {
        super(connector, BusinessServer.class);
    }
}
