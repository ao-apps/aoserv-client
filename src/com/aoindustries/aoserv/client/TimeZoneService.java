package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * The table containing all of the possible time zones.
 *
 * @see TimeZone
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.time_zones)
public interface TimeZoneService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,String,TimeZone> {
}
