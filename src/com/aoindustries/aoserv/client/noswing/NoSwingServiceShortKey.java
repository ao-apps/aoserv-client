package com.aoindustries.aoserv.client.noswing;

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
abstract class NoSwingServiceShortKey<V extends AOServObjectShortKey<V>> extends NoSwingService<Short,V> implements AOServServiceShortKey<NoSwingConnector,NoSwingConnectorFactory,V> {

    NoSwingServiceShortKey(NoSwingConnector connector, Class<V> clazz, AOServServiceShortKey<?,?,V> wrapped) {
        super(connector, Short.class, clazz, wrapped);
    }
}
