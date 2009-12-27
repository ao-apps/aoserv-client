package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.OperatingSystemVersion;
import com.aoindustries.aoserv.client.OperatingSystemVersionService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedOperatingSystemVersionService extends CachedServiceIntegerKey<OperatingSystemVersion> implements OperatingSystemVersionService<CachedConnector,CachedConnectorFactory> {

    CachedOperatingSystemVersionService(CachedConnector connector, OperatingSystemVersionService<?,?> wrapped) {
        super(connector, OperatingSystemVersion.class, wrapped);
    }
}
