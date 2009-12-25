package com.aoindustries.aoserv.client;

/*
 * Copyright 2007-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * The table containing all of the possible permissions.
 *
 * @see AOServPermission
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.aoserv_permissions)
public interface AOServPermissionService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServServiceStringKey<C,F,AOServPermission> {

    /* TODO
    public AOServPermission get(AOServPermission.Permission permission) throws IOException, SQLException {
        return getUniqueRow(AOServPermission.COLUMN_NAME, permission.name());
    }*/
}
