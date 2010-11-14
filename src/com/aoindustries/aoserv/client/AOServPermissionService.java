/*
 * Copyright 2007-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * The table containing all of the possible permissions.
 *
 * @see AOServPermission
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.aoserv_permissions)
public interface AOServPermissionService extends AOServService<String,AOServPermission> {

    /* TODO
    public AOServPermission get(AOServPermission.Permission permission) throws IOException, SQLException {
        return getUniqueRow(AOServPermission.COLUMN_NAME, permission.name());
    }*/
}
