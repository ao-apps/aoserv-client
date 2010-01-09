/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.UserId;

/**
 * An <code>AOServService</code> containing objects with UserId key values.
 *
 * @author  AO Industries, Inc.
 *
 * @see  AOServObject
 */
public interface AOServServiceUserIdKey<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>,V extends AOServObjectUserIdKey<V>> extends AOServService<C,F,UserId,V> {
}
