package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.TimeZone;
import com.aoindustries.aoserv.client.TimeZoneService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedTimeZoneService extends CachedServiceStringKey<TimeZone> implements TimeZoneService<CachedConnector,CachedConnectorFactory> {

    CachedTimeZoneService(CachedConnector connector, TimeZoneService<?,?> wrapped) {
        super(connector, TimeZone.class, wrapped);
    }
}
