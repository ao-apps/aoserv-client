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
 * The system can process several different <code>PaymentType</code>s.
 * Once processed, the amount paid is subtracted from the
 * <code>Business</code>' account as a new <code>Transaction</code>.
 *
 * @author  AO Industries, Inc.
 */
final public class PaymentType extends AOServObjectStringKey<PaymentType> implements BeanFactory<com.aoindustries.aoserv.client.beans.PaymentType> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The system supported payment types, not all of which can
     * be processed by AO Industries.
     */
    public static final String
        AMEX="amex",
        CASH="cash",
        CHECK="check",
        DISCOVER="discover",
        MASTERCARD="mastercard",
        MONEY_ORDER="money_order",
        VISA="visa",
        WIRE="wire"
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private boolean isActive;
    final private boolean allowWeb;

    public PaymentType(PaymentTypeService<?,?> service, String name, boolean isActive, boolean allowWeb) {
        super(service, name);
        this.isActive = isActive;
        this.allowWeb = allowWeb;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="name", index=IndexType.PRIMARY_KEY, description="the name of the type")
    public String getName() {
    	return getKey();
    }

    @SchemaColumn(order=1, name="active", description="indicates if payment is currently accepted via this method")
    public boolean isActive() {
        return isActive;
    }

    @SchemaColumn(order=2, name="allow_web", description="indicates if payment is allowed via a web form")
    public boolean allowWeb() {
    	return allowWeb;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.PaymentType getBean() {
        return new com.aoindustries.aoserv.client.beans.PaymentType(getKey(), isActive, allowWeb);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            // TODO: getTransactions()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    public String getDescription(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "PaymentType."+getKey()+".description");
    }

    @Override
    String toStringImpl(Locale userLocale) {
        return getDescription(userLocale);
    }
    // </editor-fold>
}