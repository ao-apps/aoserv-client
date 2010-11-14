/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * The table containing all of the roles.
 *
 * @see AOServRole
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.aoserv_roles)
public interface AOServRoleService extends AOServService<Integer,AOServRole> {
}
