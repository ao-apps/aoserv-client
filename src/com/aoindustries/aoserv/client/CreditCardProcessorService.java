/*
 * Copyright 2007-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  CreditCardProcessor
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.credit_card_processors)
public interface CreditCardProcessorService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,String,CreditCardProcessor> {
}
