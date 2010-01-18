/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * For AO Industries use only.
 *
 * @author  AO Industries, Inc.
 */
final public class ExpenseCategory extends AOServObjectStringKey<ExpenseCategory> implements BeanFactory<com.aoindustries.aoserv.client.beans.ExpenseCategory> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public ExpenseCategory(ExpenseCategoryService<?,?> service, String expenseCode) {
        super(service, expenseCode);
    }
    // </editor-fold>

    public String getExpenseCode() {
        return key;
    }

    public List<? extends AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
            getBankTransactions()
        );
    }
}
