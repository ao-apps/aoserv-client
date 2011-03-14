/*
 * Copyright 2000-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;

/**
 * The system can process several different <code>PaymentType</code>s.
 * Once processed, the amount paid is subtracted from the
 * <code>Business</code>' account as a new <code>Transaction</code>.
 *
 * @author  AO Industries, Inc.
 */
final public class PaymentType extends AOServObjectStringKey implements Comparable<PaymentType>, DtoFactory<com.aoindustries.aoserv.client.dto.PaymentType> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
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
    private static final long serialVersionUID = 7201466581627411205L;

    final private boolean active;
    final private boolean allowWeb;

    public PaymentType(AOServConnector connector, String name, boolean active, boolean allowWeb) {
        super(connector, name);
        this.active = active;
        this.allowWeb = allowWeb;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(PaymentType other) {
        return compareIgnoreCaseConsistentWithEquals(getKey(), other.getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="the name of the type")
    public String getName() {
    	return getKey();
    }

    @SchemaColumn(order=1, description="indicates if payment is currently accepted via this method")
    public boolean isActive() {
        return active;
    }

    @SchemaColumn(order=2, description="indicates if payment is allowed via a web form")
    public boolean getAllowWeb() {
    	return allowWeb;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public PaymentType(AOServConnector connector, com.aoindustries.aoserv.client.dto.PaymentType dto) {
        this(connector, dto.getName(), dto.isActive(), dto.isAllowWeb());
    }

    @Override
    public com.aoindustries.aoserv.client.dto.PaymentType getDto() {
        return new com.aoindustries.aoserv.client.dto.PaymentType(getKey(), active, allowWeb);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    public String getDescription() {
        return ApplicationResources.accessor.getMessage("PaymentType."+getKey()+".description");
    }

    @Override
    String toStringImpl() {
        return getDescription();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<Transaction> getTransactions() throws RemoteException {
        return getConnector().getTransactions().filterIndexed(Transaction.COLUMN_PAYMENT_TYPE, this);
    }
    // </editor-fold>
}