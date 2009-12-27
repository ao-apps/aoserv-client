package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.Architecture;
import com.aoindustries.aoserv.client.ArchitectureService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedArchitectureService extends CachedServiceStringKey<Architecture> implements ArchitectureService<CachedConnector,CachedConnectorFactory> {

    CachedArchitectureService(CachedConnector connector, ArchitectureService<?,?> wrapped) {
        super(connector, Architecture.class, wrapped);
    }
}
