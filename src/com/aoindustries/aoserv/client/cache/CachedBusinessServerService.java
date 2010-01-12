package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.BusinessServer;
import com.aoindustries.aoserv.client.BusinessServerService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedBusinessServerService extends CachedServiceIntegerKey<BusinessServer> implements BusinessServerService<CachedConnector,CachedConnectorFactory> {

    CachedBusinessServerService(CachedConnector connector, BusinessServerService<?,?> wrapped) {
        super(connector, BusinessServer.class, wrapped);
    }
}
