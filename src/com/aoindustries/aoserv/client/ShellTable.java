package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * @see  Shell
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class ShellTable extends GlobalTableStringKey<Shell> {

    ShellTable(AOServConnector connector) {
	super(connector, Shell.class);
    }

    int getTableID() {
	return SchemaTable.SHELLS;
    }

    public Shell get(Object pkey) {
	return getUniqueRow(Shell.COLUMN_PATH, pkey);
    }
}