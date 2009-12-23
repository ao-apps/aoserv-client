package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.ResourceType;
import com.aoindustries.aoserv.client.ResourceTypeService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedResourceTypeService extends CachedServiceStringKey<ResourceType> implements ResourceTypeService<CachedConnector,CachedConnectorFactory> {

    CachedResourceTypeService(CachedConnector connector, ResourceTypeService<?,?> wrapped) {
        super(connector, ResourceType.class, wrapped);
    }
}
