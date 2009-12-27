package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServObjectShortKey;
import com.aoindustries.aoserv.client.AOServServiceShortKey;

/**
 * @author  AO Industries, Inc.
 */
abstract class RetryServiceShortKey<V extends AOServObjectShortKey<V>> extends RetryService<Short,V> implements AOServServiceShortKey<RetryConnector,RetryConnectorFactory,V> {

    RetryServiceShortKey(RetryConnector connector, Class<V> clazz) {
        super(connector, Short.class, clazz);
    }
}
