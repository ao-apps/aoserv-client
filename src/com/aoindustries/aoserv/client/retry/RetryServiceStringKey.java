package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServObjectStringKey;
import com.aoindustries.aoserv.client.AOServServiceStringKey;

/**
 * @author  AO Industries, Inc.
 */
abstract class RetryServiceStringKey<V extends AOServObjectStringKey<V>> extends RetryService<String,V> implements AOServServiceStringKey<RetryConnector,RetryConnectorFactory,V> {

    RetryServiceStringKey(RetryConnector connector, Class<V> valueClass) {
        super(connector, String.class, valueClass);
    }
}
