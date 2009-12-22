package com.aoindustries.aoserv.client.rmi;

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
abstract class RmiServiceStringKey<V extends AOServObjectStringKey<V>> extends RmiService<String,V> implements AOServServiceStringKey<RmiConnector,RmiConnectorFactory,V> {

    RmiServiceStringKey(RmiConnector connector, Class<V> valueClass) {
        super(connector, String.class, valueClass);
    }
}
