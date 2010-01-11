package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.FailoverFileLog;
import com.aoindustries.aoserv.client.FailoverFileLogService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedFailoverFileLogService extends CachedServiceIntegerKey<FailoverFileLog> implements FailoverFileLogService<CachedConnector,CachedConnectorFactory> {

    CachedFailoverFileLogService(CachedConnector connector, FailoverFileLogService<?,?> wrapped) {
        super(connector, FailoverFileLog.class, wrapped);
    }
}
