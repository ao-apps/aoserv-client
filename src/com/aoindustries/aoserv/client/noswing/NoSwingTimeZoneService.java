package com.aoindustries.aoserv.client.noswing;

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
final class NoSwingTimeZoneService extends NoSwingServiceStringKey<TimeZone> implements TimeZoneService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingTimeZoneService(NoSwingConnector connector, TimeZoneService<?,?> wrapped) {
        super(connector, TimeZone.class, wrapped);
    }
}
