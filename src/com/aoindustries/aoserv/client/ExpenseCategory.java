/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Set;

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

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="expense_code", index=IndexType.PRIMARY_KEY, description="a simple code used as primary key")
    public String getExpenseCode() {
        return getKey();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.ExpenseCategory getBean() {
        return new com.aoindustries.aoserv.client.beans.ExpenseCategory(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            // TODO: getBankTransactions()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    public IndexedSet<BankTransaction> getBankTransactions() throws RemoteException {
        return getService().getConnector().getTicketCategories().filterIndexed(COLUMN_PARENT, this);
    }
     */
    // </editor-fold>
}
