package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.Technology;
import com.aoindustries.aoserv.client.TechnologyService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedTechnologyService extends CachedServiceIntegerKey<Technology> implements TechnologyService<CachedConnector,CachedConnectorFactory> {

    CachedTechnologyService(CachedConnector connector, TechnologyService<?,?> wrapped) {
        super(connector, Technology.class, wrapped);
    }
}
