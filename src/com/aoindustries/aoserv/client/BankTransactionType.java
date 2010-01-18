/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

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
        return key;
    }

    @SchemaColumn(order=1, name="is_negative", description="when true the amount must be negative")
    public boolean isNegative() {
        return isNegative;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.BankTransactionType getBean() {
        return new com.aoindustries.aoserv.client.beans.BankTransactionType(key, isNegative);
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

    // <editor-fold defaultstate="collapsed" desc="i18n">
    public String getDescription(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "BankTransactionType."+key+".description");
    }

    public String getDisplay(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "BankTransactionType."+key+".display");
    }

    @Override
    String toStringImpl(Locale userLocale) {
        return getDisplay(userLocale);
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