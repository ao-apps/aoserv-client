package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.MySQLServer;
import com.aoindustries.aoserv.client.MySQLServerService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedMySQLServerService extends CachedServiceIntegerKey<MySQLServer> implements MySQLServerService<CachedConnector,CachedConnectorFactory> {

    CachedMySQLServerService(CachedConnector connector, MySQLServerService<?,?> wrapped) {
        super(connector, MySQLServer.class, wrapped);
    }
}
