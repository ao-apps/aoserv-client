/*
 * Copyright 2001-2010 by AO Industries, Inc.,
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
public interface ExpenseCategoryService extends AOServService<String,ExpenseCategory> {
}