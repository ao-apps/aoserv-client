package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.PostgresServer;
import com.aoindustries.aoserv.client.PostgresServerService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedPostgresServerService extends CachedServiceIntegerKey<PostgresServer> implements PostgresServerService<CachedConnector,CachedConnectorFactory> {

    CachedPostgresServerService(CachedConnector connector, PostgresServerService<?,?> wrapped) {
        super(connector, PostgresServer.class, wrapped);
    }
}
