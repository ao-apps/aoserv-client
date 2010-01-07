package com.aoindustries.aoserv.client.noswing;

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
abstract class NoSwingServiceUserIdKey<V extends AOServObjectUserIdKey<V>> extends NoSwingService<UserId,V> implements AOServServiceUserIdKey<NoSwingConnector,NoSwingConnectorFactory,V> {

    NoSwingServiceUserIdKey(NoSwingConnector connector, Class<V> valueClass, AOServServiceUserIdKey<?,?,V> wrapped) {
        super(connector, UserId.class, valueClass, wrapped);
    }
}
