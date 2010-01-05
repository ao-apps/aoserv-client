package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServObjectUnixPathKey;
import com.aoindustries.aoserv.client.AOServServiceUnixPathKey;
import com.aoindustries.aoserv.client.validator.UnixPath;

/**
 * @author  AO Industries, Inc.
 */
abstract class NoSwingServiceUnixPathKey<V extends AOServObjectUnixPathKey<V>> extends NoSwingService<UnixPath,V> implements AOServServiceUnixPathKey<NoSwingConnector,NoSwingConnectorFactory,V> {

    NoSwingServiceUnixPathKey(NoSwingConnector connector, Class<V> valueClass, AOServServiceUnixPathKey<?,?,V> wrapped) {
        super(connector, UnixPath.class, valueClass, wrapped);
    }
}
