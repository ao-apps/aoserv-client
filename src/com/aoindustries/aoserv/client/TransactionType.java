/*
 * Copyright 2005-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.util.Locale;

/**
 * A <code>TransactionType</code> is one type that may be used
 * in a <code>Transaction</code>.  Each <code>PackageDefinition</code>
 * and <code>PackageDefinitionLimit</code> defines which type will be
 * used for billing.
 *
 * @see  PackageDefinition
 * @see  PackageDefinitionLimit
 * @see  Transaction
 *
 * @author  AO Industries, Inc.
 */
final public class TransactionType extends AOServObjectStringKey<TransactionType> implements BeanFactory<com.aoindustries.aoserv.client.beans.TransactionType> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final String
        HTTPD="httpd",
        PAYMENT="payment",
        VIRTUAL="virtual"
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    /**
     * If <code>true</code> this <code>TransactionType</code> represents a credit to
     * an account and will be listed in payments received reports.
     */
    final private boolean isCredit;

    public TransactionType(TransactionTypeService<?,?> service, String name, boolean isCredit) {
        super(service, name);
        this.isCredit = isCredit;
    }
    // </editor-fold>

    public String getDescription(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "TransactionType."+pkey+".description");
    }

    /**
     * Gets the unique name of this transaction type.
     */
    public String getName() {
        return pkey;
    }

    public String getUnit(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "TransactionType."+pkey+".unit");
    }

    public boolean isCredit() {
        return isCredit;
    }

    @Override
    String toStringImpl(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "TransactionType."+pkey+".toString");
    }
}
