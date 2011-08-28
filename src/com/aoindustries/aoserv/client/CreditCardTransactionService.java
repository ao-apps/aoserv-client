/*
 * Copyright 2007-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  CreditCardTransaction
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.credit_card_transactions)
public interface CreditCardTransactionService extends AOServService<Integer,CreditCardTransaction> {
}
