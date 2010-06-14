/*
 * Copyright 2005-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  TransactionType
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.transaction_types)
public interface TransactionTypeService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,String,TransactionType> {
}
