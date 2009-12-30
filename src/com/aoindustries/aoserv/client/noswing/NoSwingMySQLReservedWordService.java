package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.MySQLReservedWord;
import com.aoindustries.aoserv.client.MySQLReservedWordService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingMySQLReservedWordService extends NoSwingServiceStringKey<MySQLReservedWord> implements MySQLReservedWordService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingMySQLReservedWordService(NoSwingConnector connector, MySQLReservedWordService<?,?> wrapped) {
        super(connector, MySQLReservedWord.class, wrapped);
    }
}
