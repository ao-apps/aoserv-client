package com.aoindustries.aoserv.client.cache;

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
abstract class CachedServiceUserIdKey<V extends AOServObjectUserIdKey<V>> extends CachedService<UserId,V> implements AOServServiceUserIdKey<CachedConnector,CachedConnectorFactory,V> {

    CachedServiceUserIdKey(CachedConnector connector, Class<V> valueClass, AOServServiceUserIdKey<?,?,V> wrapped) {
        super(connector, UserId.class, valueClass, wrapped);
    }
}
