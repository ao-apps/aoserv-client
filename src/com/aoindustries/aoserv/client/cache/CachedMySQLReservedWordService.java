package com.aoindustries.aoserv.client.cache;

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
final class CachedMySQLReservedWordService extends CachedServiceStringKey<MySQLReservedWord> implements MySQLReservedWordService<CachedConnector,CachedConnectorFactory> {

    CachedMySQLReservedWordService(CachedConnector connector, MySQLReservedWordService<?,?> wrapped) {
        super(connector, MySQLReservedWord.class, wrapped);
    }
}
