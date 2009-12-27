package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.TechnologyName;
import com.aoindustries.aoserv.client.TechnologyNameService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedTechnologyNameService extends CachedServiceStringKey<TechnologyName> implements TechnologyNameService<CachedConnector,CachedConnectorFactory> {

    CachedTechnologyNameService(CachedConnector connector, TechnologyNameService<?,?> wrapped) {
        super(connector, TechnologyName.class, wrapped);
    }
}
