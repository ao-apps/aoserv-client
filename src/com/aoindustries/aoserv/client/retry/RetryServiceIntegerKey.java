package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServObjectIntegerKey;
import com.aoindustries.aoserv.client.AOServServiceIntegerKey;

/**
 * @author  AO Industries, Inc.
 */
abstract class RetryServiceIntegerKey<V extends AOServObjectIntegerKey<V>> extends RetryService<Integer,V> implements AOServServiceIntegerKey<RetryConnector,RetryConnectorFactory,V> {

    RetryServiceIntegerKey(RetryConnector connector, Class<V> clazz) {
        super(connector, Integer.class, clazz);
    }
}
