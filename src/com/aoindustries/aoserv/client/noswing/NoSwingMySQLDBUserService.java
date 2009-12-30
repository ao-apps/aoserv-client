package com.aoindustries.aoserv.client.noswing;

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
final class NoSwingMySQLDBUserService extends NoSwingServiceIntegerKey<MySQLDBUser> implements MySQLDBUserService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingMySQLDBUserService(NoSwingConnector connector, MySQLDBUserService<?,?> wrapped) {
        super(connector, MySQLDBUser.class, wrapped);
    }
}
