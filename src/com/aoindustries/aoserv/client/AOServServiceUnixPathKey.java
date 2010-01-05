package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.UnixPath;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * An <code>AOServService</code> containing objects with UnixPath key values.
 *
 * @author  AO Industries, Inc.
 *
 * @see  AOServObject
 */
public interface AOServServiceUnixPathKey<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>,V extends AOServObjectUnixPathKey<V>> extends AOServService<C,F,UnixPath,V> {
}
