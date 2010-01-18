/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

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

    public String getDescription(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "BankTransactionType."+key+".description");
    }

    public String getDisplay(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "BankTransactionType."+key+".display");
    }

    public String getName() {
        return key;
    }

    @Override
    String toStringImpl(Locale userLocale) {
        return getDisplay(userLocale);
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getBankTransactions()
        );
    }
}