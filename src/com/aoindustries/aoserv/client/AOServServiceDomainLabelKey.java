/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.DomainLabel;

/**
 * An <code>AOServService</code> containing objects with DomainLabel key values.
 *
 * @author  AO Industries, Inc.
 *
 * @see  AOServObject
 */
public interface AOServServiceDomainLabelKey<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>,V extends AOServObjectDomainLabelKey<V>> extends AOServService<C,F,DomainLabel,V> {
}
