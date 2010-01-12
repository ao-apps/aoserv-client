package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.ServerResource;
import com.aoindustries.aoserv.client.ServerResourceService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedServerResourceService extends CachedServiceIntegerKey<ServerResource> implements ServerResourceService<CachedConnector,CachedConnectorFactory> {

    CachedServerResourceService(CachedConnector connector, ServerResourceService<?,?> wrapped) {
        super(connector, ServerResource.class, wrapped);
    }
}
