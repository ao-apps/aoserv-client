/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;

/**
 * For AO Industries use only.
 *
 * @author  AO Industries, Inc.
 */
final public class BankTransactionType extends AOServObjectStringKey<BankTransactionType> implements BeanFactory<com.aoindustries.aoserv.client.beans.BankTransactionType> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private boolean isNegative;

    public BankTransactionType(BankTransactionTypeService<?,?> service, String name, boolean isNegative) {
        super(service, name);
        this.isNegative = isNegative;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="name", index=IndexType.PRIMARY_KEY, description="the name of the type")
    public String getName() {
        return getKey();
    }

    @SchemaColumn(order=1, name="is_negative", description="when true the amount must be negative")
    public boolean isNegative() {
        return isNegative;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    @Override
    public com.aoindustries.aoserv.client.beans.BankTransactionType getBean() {
        return new com.aoindustries.aoserv.client.beans.BankTransactionType(getKey(), isNegative);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getBankTransactions());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    public String getDescription() {
        return ApplicationResources.accessor.getMessage("BankTransactionType."+getKey()+".description");
    }

    public String getDisplay() {
        return ApplicationResources.accessor.getMessage("BankTransactionType."+getKey()+".display");
    }

    @Override
    String toStringImpl() {
        return getDisplay();
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