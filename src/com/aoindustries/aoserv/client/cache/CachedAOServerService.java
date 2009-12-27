package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServer;
import com.aoindustries.aoserv.client.AOServerService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedAOServerService extends CachedServiceIntegerKey<AOServer> implements AOServerService<CachedConnector,CachedConnectorFactory> {

    CachedAOServerService(CachedConnector connector, AOServerService<?,?> wrapped) {
        super(connector, AOServer.class, wrapped);
    }
}
