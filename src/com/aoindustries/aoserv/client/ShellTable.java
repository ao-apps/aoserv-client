package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(Shell.COLUMN_PATH_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SHELLS;
    }

    public Shell get(Object pkey) {
	return getUniqueRow(Shell.COLUMN_PATH, pkey);
    }
}