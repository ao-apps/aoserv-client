package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.Resource;
import com.aoindustries.aoserv.client.ResourceService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedResourceService extends CachedServiceIntegerKey<Resource> implements ResourceService<CachedConnector,CachedConnectorFactory> {

    CachedResourceService(CachedConnector connector, ResourceService<?,?> wrapped) {
        super(connector, Resource.class, wrapped);
    }
}
