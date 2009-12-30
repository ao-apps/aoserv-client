package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.MySQLUser;
import com.aoindustries.aoserv.client.MySQLUserService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedMySQLUserService extends CachedServiceIntegerKey<MySQLUser> implements MySQLUserService<CachedConnector,CachedConnectorFactory> {

    CachedMySQLUserService(CachedConnector connector, MySQLUserService<?,?> wrapped) {
        super(connector, MySQLUser.class, wrapped);
    }
}
