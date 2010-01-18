/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * For AO Industries use only.
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.expense_categories)
public interface ExpenseCategoryService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,String,ExpenseCategory> {
}