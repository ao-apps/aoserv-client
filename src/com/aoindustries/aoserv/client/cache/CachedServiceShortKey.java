package com.aoindustries.aoserv.client.cache;

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
abstract class CachedServiceShortKey<V extends AOServObjectShortKey<V>> extends CachedService<Short,V> implements AOServServiceShortKey<CachedConnector,CachedConnectorFactory,V> {

    CachedServiceShortKey(CachedConnector connector, Class<V> clazz, AOServServiceShortKey<?,?,V> wrapped) {
        super(connector, Short.class, clazz, wrapped);
    }
}
