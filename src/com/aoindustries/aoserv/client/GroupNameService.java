/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.GroupId;

/**
 * @see  GroupName
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.group_names)
public interface GroupNameService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,GroupId,GroupName> {
}
