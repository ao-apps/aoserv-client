package com.aoindustries.aoserv.client;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.validator.*;
import java.rmi.RemoteException;

/**
 * An object that uses a AccountingCode as its key value.
 *
 * @author  AO Industries, Inc.
 */
abstract public class AOServObjectAccountingCodeKey<T extends AOServObjectAccountingCodeKey<T> & Comparable<T> & DtoFactory<?>> extends AOServObject<AccountingCode,T> {

    private static final long serialVersionUID = 1L;

    private AccountingCode key;

    protected AOServObjectAccountingCodeKey(AOServService<?,?,AccountingCode,T> service, AccountingCode key) {
        super(service);
        this.key = key;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        key = intern(key);
    }

    /**
     * Gets the interned key value for this object.
     */
    @Override
    final public AccountingCode getKey() {
        return key;
    }

    @Override
    final public boolean equals(Object o) {
        if(o==null || getClass()!=o.getClass()) return false;
        AOServObjectAccountingCodeKey other = (AOServObjectAccountingCodeKey)o;
        return key==other.key; // OK because interned
    }

    /**
     * The default string representation is that of the key value.
     */
    @Override
    String toStringImpl() throws RemoteException {
        return key.toString();
    }
}
