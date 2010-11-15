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
 * The system can process several different <code>PaymentType</code>s.
 * Once processed, the amount paid is subtracted from the
 * <code>Business</code>' account as a new <code>Transaction</code>.
 *
 * @author  AO Industries, Inc.
 */
final public class PaymentType extends AOServObjectStringKey implements Comparable<PaymentType>, DtoFactory<com.aoindustries.aoserv.client.dto.PaymentType> {

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

    public PaymentType(AOServConnector connector, String name, boolean isActive, boolean allowWeb) {
        super(connector, name);
        this.isActive = isActive;
        this.allowWeb = allowWeb;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(PaymentType other) {
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(getKey(), other.getKey());
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

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public PaymentType(AOServConnector connector, com.aoindustries.aoserv.client.dto.PaymentType dto) {
        this(connector, dto.getName(), dto.isIsActive(), dto.isAllowWeb());
    }

    @Override
    public com.aoindustries.aoserv.client.dto.PaymentType getDto() {
        return new com.aoindustries.aoserv.client.dto.PaymentType(getKey(), isActive, allowWeb);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependentObjects(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTransactions());
        return unionSet;
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
    public IndexedSet<Transaction> getTransactions() throws RemoteException {
        return getConnector().getTransactions().filterIndexed(Transaction.COLUMN_PAYMENT_TYPE, this);
    }
    // </editor-fold>
}