package com.aoindustries.aoserv.client.rmi;

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
abstract class RmiServiceIntegerKey<V extends AOServObjectIntegerKey<V>> extends RmiService<Integer,V> implements AOServServiceIntegerKey<RmiConnector,RmiConnectorFactory,V> {

    RmiServiceIntegerKey(RmiConnector connector, Class<V> clazz) {
        super(connector, Integer.class, clazz);
    }
}
