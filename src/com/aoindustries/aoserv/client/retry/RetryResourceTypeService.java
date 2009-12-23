package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.ResourceType;
import com.aoindustries.aoserv.client.ResourceTypeService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryResourceTypeService extends RetryServiceStringKey<ResourceType> implements ResourceTypeService<RetryConnector,RetryConnectorFactory> {

    RetryResourceTypeService(RetryConnector connector) {
        super(connector, ResourceType.class);
    }
}
