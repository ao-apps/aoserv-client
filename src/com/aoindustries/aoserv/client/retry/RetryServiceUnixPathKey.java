package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServObjectUnixPathKey;
import com.aoindustries.aoserv.client.AOServServiceUnixPathKey;
import com.aoindustries.aoserv.client.UnixPath;

/**
 * @author  AO Industries, Inc.
 */
abstract class RetryServiceUnixPathKey<V extends AOServObjectUnixPathKey<V>> extends RetryService<UnixPath,V> implements AOServServiceUnixPathKey<RetryConnector,RetryConnectorFactory,V> {

    RetryServiceUnixPathKey(RetryConnector connector, Class<V> valueClass) {
        super(connector, UnixPath.class, valueClass);
    }
}
