package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServPermission;
import com.aoindustries.aoserv.client.AOServPermissionService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryAOServPermissionService extends RetryServiceStringKey<AOServPermission> implements AOServPermissionService<RetryConnector,RetryConnectorFactory> {

    RetryAOServPermissionService(RetryConnector connector) {
        super(connector, AOServPermission.class);
    }
}
