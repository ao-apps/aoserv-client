package com.aoindustries.aoserv.client.cache;

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
abstract class CachedServiceIntegerKey<V extends AOServObjectIntegerKey<V>> extends CachedService<Integer,V> implements AOServServiceIntegerKey<CachedConnector,CachedConnectorFactory,V> {

    CachedServiceIntegerKey(CachedConnector connector, Class<V> clazz, AOServServiceIntegerKey<?,?,V> wrapped) {
        super(connector, Integer.class, clazz, wrapped);
    }
}
