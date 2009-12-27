package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.TechnologyVersion;
import com.aoindustries.aoserv.client.TechnologyVersionService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedTechnologyVersionService extends CachedServiceIntegerKey<TechnologyVersion> implements TechnologyVersionService<CachedConnector,CachedConnectorFactory> {

    CachedTechnologyVersionService(CachedConnector connector, TechnologyVersionService<?,?> wrapped) {
        super(connector, TechnologyVersion.class, wrapped);
    }
}
