package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(TimeZone.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.TIME_ZONES;
    }

    public TimeZone get(Object pkey) {
	return getUniqueRow(TimeZone.COLUMN_NAME, pkey);
    }
}