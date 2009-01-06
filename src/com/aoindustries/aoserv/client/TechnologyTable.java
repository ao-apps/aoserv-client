package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.List;

/**
 * @see  Technology
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class TechnologyTable extends GlobalTableIntegerKey<Technology> {

    TechnologyTable(AOServConnector connector) {
	super(connector, Technology.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(Technology.COLUMN_NAME_name, ASCENDING),
        new OrderBy(Technology.COLUMN_CLASS_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.TECHNOLOGIES;
    }

    public Technology get(Object pkey) {
	return getUniqueRow(Technology.COLUMN_PKEY, pkey);
    }

    public Technology get(int pkey) {
	return getUniqueRow(Technology.COLUMN_PKEY, pkey);
    }

    List<Technology> getTechnologies(TechnologyName techName) {
        return getIndexedRows(Technology.COLUMN_NAME, techName.pkey);
    }
}