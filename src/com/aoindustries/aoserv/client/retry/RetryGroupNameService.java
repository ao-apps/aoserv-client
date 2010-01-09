package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.GroupName;
import com.aoindustries.aoserv.client.GroupNameService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryGroupNameService extends RetryServiceGroupIdKey<GroupName> implements GroupNameService<RetryConnector,RetryConnectorFactory> {

    RetryGroupNameService(RetryConnector connector) {
        super(connector, GroupName.class);
    }
}
