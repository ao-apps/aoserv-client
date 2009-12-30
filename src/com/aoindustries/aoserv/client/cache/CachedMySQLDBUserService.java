package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.MySQLDBUser;
import com.aoindustries.aoserv.client.MySQLDBUserService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedMySQLDBUserService extends CachedServiceIntegerKey<MySQLDBUser> implements MySQLDBUserService<CachedConnector,CachedConnectorFactory> {

    CachedMySQLDBUserService(CachedConnector connector, MySQLDBUserService<?,?> wrapped) {
        super(connector, MySQLDBUser.class, wrapped);
    }
}
