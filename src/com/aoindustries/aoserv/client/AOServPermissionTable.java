package com.aoindustries.aoserv.client;

/*
 * Copyright 2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * The table containing all of the possible permissions.
 *
 * @see AOServPermission
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class AOServPermissionTable extends GlobalTableStringKey<AOServPermission> {

    AOServPermissionTable(AOServConnector connector) {
	super(connector, AOServPermission.class);
    }

    public AOServPermission get(Object name) {
        return getUniqueRow(AOServPermission.COLUMN_NAME, name);
    }

    int getTableID() {
        return SchemaTable.AOSERV_PERMISSIONS;
    }
}
