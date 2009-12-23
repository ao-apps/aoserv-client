package com.aoindustries.aoserv.client.noswing;

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
abstract class NoSwingServiceStringKey<V extends AOServObjectStringKey<V>> extends NoSwingService<String,V> implements AOServServiceStringKey<NoSwingConnector,NoSwingConnectorFactory,V> {

    NoSwingServiceStringKey(NoSwingConnector connector, Class<V> valueClass, AOServServiceStringKey<?,?,V> wrapped) {
        super(connector, String.class, valueClass, wrapped);
    }
}
