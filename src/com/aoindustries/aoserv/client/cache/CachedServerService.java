package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.Server;
import com.aoindustries.aoserv.client.ServerService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedServerService extends CachedServiceIntegerKey<Server> implements ServerService<CachedConnector,CachedConnectorFactory> {

    CachedServerService(CachedConnector connector, ServerService<?,?> wrapped) {
        super(connector, Server.class, wrapped);
    }
}
