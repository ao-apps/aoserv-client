package com.aoindustries.aoserv.client.cache;

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
abstract class CachedServiceStringKey<V extends AOServObjectStringKey<V>> extends CachedService<String,V> implements AOServServiceStringKey<CachedConnector,CachedConnectorFactory,V> {

    CachedServiceStringKey(CachedConnector connector, Class<V> valueClass, AOServServiceStringKey<?,?,V> wrapped) {
        super(connector, String.class, valueClass, wrapped);
    }
}
