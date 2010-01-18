/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.util.Locale;

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

    public boolean allowWeb() {
    	return allowWeb;
    }

    public String getDescription(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "PaymentType."+key+".description");
    }

    public String getName() {
    	return key;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    String toStringImpl(Locale userLocale) {
        return getDescription(userLocale);
    }
}