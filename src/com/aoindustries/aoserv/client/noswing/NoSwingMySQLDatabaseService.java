package com.aoindustries.aoserv.client.noswing;

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
final class NoSwingMySQLDatabaseService extends NoSwingServiceIntegerKey<MySQLDatabase> implements MySQLDatabaseService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingMySQLDatabaseService(NoSwingConnector connector, MySQLDatabaseService<?,?> wrapped) {
        super(connector, MySQLDatabase.class, wrapped);
    }
}
