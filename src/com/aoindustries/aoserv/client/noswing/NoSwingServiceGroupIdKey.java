package com.aoindustries.aoserv.client.noswing;

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
abstract class NoSwingServiceGroupIdKey<V extends AOServObjectGroupIdKey<V>> extends NoSwingService<GroupId,V> implements AOServServiceGroupIdKey<NoSwingConnector,NoSwingConnectorFactory,V> {

    NoSwingServiceGroupIdKey(NoSwingConnector connector, Class<V> valueClass, AOServServiceGroupIdKey<?,?,V> wrapped) {
        super(connector, GroupId.class, valueClass, wrapped);
    }
}
