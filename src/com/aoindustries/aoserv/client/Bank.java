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
final public class Bank extends AOServObjectStringKey implements Comparable<Bank>, DtoFactory<com.aoindustries.aoserv.client.dto.Bank> {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = -3197258892714372577L;

    final private String display;

    public Bank(AOServConnector connector, String name, String display) {
        super(connector, name);
        this.display = display;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(Bank other) {
        return compareIgnoreCaseConsistentWithEquals(getKey(), other.getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="the unique name of the bank")
    public String getName() {
    	return getKey();
    }

    @SchemaColumn(order=1, description="the name that is displayed")
    public String getDisplay() {
    	return display;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public Bank(AOServConnector connector, com.aoindustries.aoserv.client.dto.Bank dto) {
        this(
            connector,
            dto.getName(),
            dto.getDisplay()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.Bank getDto() {
        return new com.aoindustries.aoserv.client.dto.Bank(
            getKey(),
            display
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
    public IndexedSet<BankAccount> getBankAccounts() throws RemoteException {
        return getConnector().getBankAccounts().filterIndexed(BankAccount.COLUMN_BANK, this);
    }
    // </editor-fold>
}
