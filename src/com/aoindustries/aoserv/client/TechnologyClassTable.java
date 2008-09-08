package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see  TechnologyClass
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class TechnologyClassTable extends GlobalTableStringKey<TechnologyClass> {

    TechnologyClassTable(AOServConnector connector) {
	super(connector, TechnologyClass.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(TechnologyClass.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.TECHNOLOGY_CLASSES;
    }

    public TechnologyClass get(Object pkey) {
	return getUniqueRow(TechnologyClass.COLUMN_NAME, pkey);
    }
}