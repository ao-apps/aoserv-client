package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.OperatingSystem;
import com.aoindustries.aoserv.client.OperatingSystemService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedOperatingSystemService extends CachedServiceStringKey<OperatingSystem> implements OperatingSystemService<CachedConnector,CachedConnectorFactory> {

    CachedOperatingSystemService(CachedConnector connector, OperatingSystemService<?,?> wrapped) {
        super(connector, OperatingSystem.class, wrapped);
    }
}
