/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * The table containing all of the permissions granted by each role.
 *
 * @see AOServRole
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.aoserv_role_permissions)
public interface AOServRolePermissionService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,Integer,AOServRolePermission> {
}
