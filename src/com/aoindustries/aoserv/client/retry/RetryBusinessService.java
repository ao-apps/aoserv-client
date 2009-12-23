package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.Business;
import com.aoindustries.aoserv.client.BusinessService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryBusinessService extends RetryServiceStringKey<Business> implements BusinessService<RetryConnector,RetryConnectorFactory> {

    RetryBusinessService(RetryConnector connector) {
        super(connector, Business.class);
    }
}
