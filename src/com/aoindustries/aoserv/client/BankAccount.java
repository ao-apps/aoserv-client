/*
 * Copyright 2000-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;

/**
 * For AO Industries use only.
 *
 * @author  AO Industries, Inc.
 */
final public class BankAccount extends AOServObjectStringKey implements Comparable<BankAccount>, DtoFactory<com.aoindustries.aoserv.client.dto.BankAccount> {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = -7502808387336724934L;

    final private String display;
    private String bank;

    public BankAccount(
        AOServConnector connector,
        String name,
        String display,
        String bank
    ) {
        super(connector, name);
        this.display = display;
        this.bank = bank;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        bank = intern(bank);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(BankAccount other) {
        return compareIgnoreCaseConsistentWithEquals(getKey(), other.getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="the unique name of this account")
    public String getName() {
        return getKey();
    }

    @SchemaColumn(order=1, description="the display name of this account")
    public String getDisplay() {
        return display;
    }

    public static final MethodColumn COLUMN_BANK = getMethodColumn(BankAccount.class, "bank");
    @DependencySingleton
    @SchemaColumn(order=2, index=IndexType.INDEXED, description="the bank the account is with")
    public Bank getBank() throws RemoteException {
        return getConnector().getBanks().get(bank);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public BankAccount(AOServConnector connector, com.aoindustries.aoserv.client.dto.BankAccount dto) {
        this(
            connector,
            dto.getName(),
            dto.getDisplay(),
            dto.getBank()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.BankAccount getDto() {
        return new com.aoindustries.aoserv.client.dto.BankAccount(
            getKey(),
            display,
            bank
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
    	return display;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<BankTransaction> getBankTransactions() throws RemoteException {
    	return getConnector().getBankTransactions().filterIndexed(BankTransaction.COLUMN_BANK_ACCOUNT, this);
    }
    // </editor-fold>
}