package com.aoindustries.aoserv.client;

/*
 * Copyright 2008-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * The table containing all of the possible disk types.
 *
 * @author  AO Industries, Inc.
 */
final public class DiskTypeTable extends GlobalTableStringKey<DiskType> {

    DiskTypeTable(AOServConnector connector) {
	super(connector, DiskType.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(DiskType.COLUMN_SORT_ORDER_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public DiskType get(Object type) {
        return getUniqueRow(DiskType.COLUMN_TYPE, type);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.DISK_TYPES;
    }
}