package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * @see  TechnologyName
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class TechnologyNameTable extends GlobalTableStringKey<TechnologyName> {

    TechnologyNameTable(AOServConnector connector) {
	super(connector, TechnologyName.class);
    }

    int getTableID() {
	return SchemaTable.TECHNOLOGY_NAMES;
    }

    public TechnologyName get(Object pkey) {
	return getUniqueRow(TechnologyName.COLUMN_NAME, pkey);
    }
}