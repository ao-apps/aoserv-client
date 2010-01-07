package com.aoindustries.aoserv.client.noswing;

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
final class NoSwingHttpdSiteService extends NoSwingServiceIntegerKey<HttpdSite> implements HttpdSiteService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingHttpdSiteService(NoSwingConnector connector, HttpdSiteService<?,?> wrapped) {
        super(connector, HttpdSite.class, wrapped);
    }
}
