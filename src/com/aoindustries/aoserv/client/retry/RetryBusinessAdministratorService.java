package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.BusinessAdministrator;
import com.aoindustries.aoserv.client.BusinessAdministratorService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryBusinessAdministratorService extends RetryServiceUserIdKey<BusinessAdministrator> implements BusinessAdministratorService<RetryConnector,RetryConnectorFactory> {

    RetryBusinessAdministratorService(RetryConnector connector) {
        super(connector, BusinessAdministrator.class);
    }
}
