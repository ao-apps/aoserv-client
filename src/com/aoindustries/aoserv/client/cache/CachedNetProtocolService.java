package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.NetProtocol;
import com.aoindustries.aoserv.client.NetProtocolService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedNetProtocolService extends CachedServiceStringKey<NetProtocol> implements NetProtocolService<CachedConnector,CachedConnectorFactory> {

    CachedNetProtocolService(CachedConnector connector, NetProtocolService<?,?> wrapped) {
        super(connector, NetProtocol.class, wrapped);
    }
}
