package com.aoindustries.aoserv.client;

/*
 * Copyright 2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * The table containing all of the possible time zones.
 *
 * @see TimeZone
 *
 * @since  1.2
 *
 * @author  AO Industries, Inc.
 */
final public class TimeZoneTable extends GlobalTableStringKey<TimeZone> {

    TimeZoneTable(AOServConnector connector) {
	super(connector, TimeZone.class);
    }

    int getTableID() {
        return SchemaTable.TIME_ZONES;
    }

    public TimeZone get(Object pkey) {
	return getUniqueRow(TimeZone.COLUMN_NAME, pkey);
    }
}