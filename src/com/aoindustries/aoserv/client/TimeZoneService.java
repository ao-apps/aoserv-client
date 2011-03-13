package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2011 by AO Industries, Inc.,
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
public interface TimeZoneService extends AOServService<String,TimeZone> {
}
