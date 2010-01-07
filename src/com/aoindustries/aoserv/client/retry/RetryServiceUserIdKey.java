package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServObjectUserIdKey;
import com.aoindustries.aoserv.client.AOServServiceUserIdKey;
import com.aoindustries.aoserv.client.validator.UserId;

/**
 * @author  AO Industries, Inc.
 */
abstract class RetryServiceUserIdKey<V extends AOServObjectUserIdKey<V>> extends RetryService<UserId,V> implements AOServServiceUserIdKey<RetryConnector,RetryConnectorFactory,V> {

    RetryServiceUserIdKey(RetryConnector connector, Class<V> valueClass) {
        super(connector, UserId.class, valueClass);
    }
}
