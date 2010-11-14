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
public interface TransactionTypeService extends AOServService<String,TransactionType> {
}
