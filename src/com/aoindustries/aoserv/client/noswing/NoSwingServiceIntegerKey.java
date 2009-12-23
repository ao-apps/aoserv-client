package com.aoindustries.aoserv.client.noswing;

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
abstract class NoSwingServiceIntegerKey<V extends AOServObjectIntegerKey<V>> extends NoSwingService<Integer,V> implements AOServServiceIntegerKey<NoSwingConnector,NoSwingConnectorFactory,V> {

    NoSwingServiceIntegerKey(NoSwingConnector connector, Class<V> clazz, AOServServiceIntegerKey<?,?,V> wrapped) {
        super(connector, Integer.class, clazz, wrapped);
    }
}
