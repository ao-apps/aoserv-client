package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServObjectGroupIdKey;
import com.aoindustries.aoserv.client.AOServServiceGroupIdKey;
import com.aoindustries.aoserv.client.validator.GroupId;

/**
 * @author  AO Industries, Inc.
 */
abstract class RetryServiceGroupIdKey<V extends AOServObjectGroupIdKey<V>> extends RetryService<GroupId,V> implements AOServServiceGroupIdKey<RetryConnector,RetryConnectorFactory,V> {

    RetryServiceGroupIdKey(RetryConnector connector, Class<V> valueClass) {
        super(connector, GroupId.class, valueClass);
    }
}
