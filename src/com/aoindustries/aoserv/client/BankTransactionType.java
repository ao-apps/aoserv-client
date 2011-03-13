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
final public class BankTransactionType
extends AOServObjectStringKey
implements
    Comparable<BankTransactionType>,
    DtoFactory<com.aoindustries.aoserv.client.dto.BankTransactionType> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private boolean isNegative;

    public BankTransactionType(AOServConnector connector, String name, boolean isNegative) {
        super(connector, name);
        this.isNegative = isNegative;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(BankTransactionType other) {
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(getKey(), other.getKey());
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

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public BankTransactionType(AOServConnector connector, com.aoindustries.aoserv.client.dto.BankTransactionType dto) {
        this(connector, dto.getName(), dto.isIsNegative());
    }

    @Override
    public com.aoindustries.aoserv.client.dto.BankTransactionType getDto() {
        return new com.aoindustries.aoserv.client.dto.BankTransactionType(getKey(), isNegative);
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
    @DependentObjectSet
    public IndexedSet<BankTransaction> getBankTransactions() throws RemoteException {
        return getConnector().getTicketCategories().filterIndexed(COLUMN_PARENT, this);
    }
     */
    // </editor-fold>
}