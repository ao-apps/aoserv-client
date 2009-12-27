package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.TechnologyClass;
import com.aoindustries.aoserv.client.TechnologyClassService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedTechnologyClassService extends CachedServiceStringKey<TechnologyClass> implements TechnologyClassService<CachedConnector,CachedConnectorFactory> {

    CachedTechnologyClassService(CachedConnector connector, TechnologyClassService<?,?> wrapped) {
        super(connector, TechnologyClass.class, wrapped);
    }
}
