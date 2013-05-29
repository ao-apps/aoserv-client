package com.aoindustries.aoserv.client;

import java.io.IOException;

/*
 * Copyright 2007-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.sql.SQLException;

/**
 * The table containing all of the possible permissions.
 *
 * @see AOServPermission
 *
 * @author  AO Industries, Inc.
 */
final public class AOServPermissionTable extends GlobalTableStringKey<AOServPermission> {

    AOServPermissionTable(AOServConnector connector) {
	super(connector, AOServPermission.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(AOServPermission.COLUMN_SORT_ORDER_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public AOServPermission get(String name) throws IOException, SQLException {
        return getUniqueRow(AOServPermission.COLUMN_NAME, name);
    }

    public AOServPermission get(AOServPermission.Permission permission) throws IOException, SQLException {
        return getUniqueRow(AOServPermission.COLUMN_NAME, permission.name());
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.AOSERV_PERMISSIONS;
    }
}
