package com.aoindustries.aoserv.client;

/*
 * Copyright 2008-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * The table containing all of the possible RAID types.
 *
 * @author  AO Industries, Inc.
 */
final public class RaidTypeTable extends GlobalTableStringKey<RaidType> {

    RaidTypeTable(AOServConnector connector) {
	super(connector, RaidType.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(RaidType.COLUMN_SORT_ORDER_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public RaidType get(Object type) {
        return getUniqueRow(RaidType.COLUMN_TYPE, type);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.RAID_TYPES;
    }
}