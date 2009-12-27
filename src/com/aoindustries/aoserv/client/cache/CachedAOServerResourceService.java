package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServerResource;
import com.aoindustries.aoserv.client.AOServerResourceService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedAOServerResourceService extends CachedServiceIntegerKey<AOServerResource> implements AOServerResourceService<CachedConnector,CachedConnectorFactory> {

    CachedAOServerResourceService(CachedConnector connector, AOServerResourceService<?,?> wrapped) {
        super(connector, AOServerResource.class, wrapped);
    }
}
