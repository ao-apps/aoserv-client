package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
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

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.TECHNOLOGY_NAMES;
    }

    public TechnologyName get(Object pkey) {
	return getUniqueRow(TechnologyName.COLUMN_NAME, pkey);
    }
}