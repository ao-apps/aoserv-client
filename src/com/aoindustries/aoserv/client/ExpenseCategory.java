/*
 * Copyright 2000-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;

/**
 * For AO Industries use only.
 *
 * @author  AO Industries, Inc.
 */
final public class ExpenseCategory extends AOServObjectStringKey implements Comparable<ExpenseCategory>, DtoFactory<com.aoindustries.aoserv.client.dto.ExpenseCategory> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    // TODO: private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public ExpenseCategory(AOServConnector connector, String expenseCode) {
        super(connector, expenseCode);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(ExpenseCategory other) {
        return compareIgnoreCaseConsistentWithEquals(getKey(), other.getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="a simple code used as primary key")
    public String getExpenseCode() {
        return getKey();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public ExpenseCategory(AOServConnector connector, com.aoindustries.aoserv.client.dto.ExpenseCategory dto) {
        this(connector, dto.getExpenseCode());
    }

    @Override
    public com.aoindustries.aoserv.client.dto.ExpenseCategory getDto() {
        return new com.aoindustries.aoserv.client.dto.ExpenseCategory(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    @DependentObjectSet
    public IndexedSet<BankTransaction> getBankTransactions() throws RemoteException {
        return getConnector().getTicketCategories().filterIndexed(COLUMN_PARENT, this);
    }
     */
    // </editor-fold>
}
