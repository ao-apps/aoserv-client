package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServObjectUnixPathKey;
import com.aoindustries.aoserv.client.AOServServiceUnixPathKey;
import com.aoindustries.aoserv.client.validator.UnixPath;

/**
 * @author  AO Industries, Inc.
 */
abstract class CachedServiceUnixPathKey<V extends AOServObjectUnixPathKey<V>> extends CachedService<UnixPath,V> implements AOServServiceUnixPathKey<CachedConnector,CachedConnectorFactory,V> {

    CachedServiceUnixPathKey(CachedConnector connector, Class<V> valueClass, AOServServiceUnixPathKey<?,?,V> wrapped) {
        super(connector, UnixPath.class, valueClass, wrapped);
    }
}
