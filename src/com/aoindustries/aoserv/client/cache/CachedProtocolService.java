package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.Protocol;
import com.aoindustries.aoserv.client.ProtocolService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedProtocolService extends CachedServiceStringKey<Protocol> implements ProtocolService<CachedConnector,CachedConnectorFactory> {

    CachedProtocolService(CachedConnector connector, ProtocolService<?,?> wrapped) {
        super(connector, Protocol.class, wrapped);
    }
}
