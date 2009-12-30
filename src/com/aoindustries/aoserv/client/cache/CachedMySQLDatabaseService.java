package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.MySQLDatabase;
import com.aoindustries.aoserv.client.MySQLDatabaseService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedMySQLDatabaseService extends CachedServiceIntegerKey<MySQLDatabase> implements MySQLDatabaseService<CachedConnector,CachedConnectorFactory> {

    CachedMySQLDatabaseService(CachedConnector connector, MySQLDatabaseService<?,?> wrapped) {
        super(connector, MySQLDatabase.class, wrapped);
    }
}
