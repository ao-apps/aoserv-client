package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.HttpdSite;
import com.aoindustries.aoserv.client.HttpdSiteService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedHttpdSiteService extends CachedServiceIntegerKey<HttpdSite> implements HttpdSiteService<CachedConnector,CachedConnectorFactory> {

    CachedHttpdSiteService(CachedConnector connector, HttpdSiteService<?,?> wrapped) {
        super(connector, HttpdSite.class, wrapped);
    }
}
