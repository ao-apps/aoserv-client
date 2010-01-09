package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServObjectGroupIdKey;
import com.aoindustries.aoserv.client.AOServServiceGroupIdKey;
import com.aoindustries.aoserv.client.validator.GroupId;

/**
 * @author  AO Industries, Inc.
 */
abstract class CachedServiceGroupIdKey<V extends AOServObjectGroupIdKey<V>> extends CachedService<GroupId,V> implements AOServServiceGroupIdKey<CachedConnector,CachedConnectorFactory,V> {

    CachedServiceGroupIdKey(CachedConnector connector, Class<V> valueClass, AOServServiceGroupIdKey<?,?,V> wrapped) {
        super(connector, GroupId.class, valueClass, wrapped);
    }
}
