/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;

/**
 * An <code>AOServService</code> containing objects with AccountingCode key values.
 *
 * @author  AO Industries, Inc.
 *
 * @see  AOServObject
 */
public interface AOServServiceAccountingCodeKey<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>,V extends AOServObjectAccountingCodeKey<V>> extends AOServService<C,F,AccountingCode,V> {
}
