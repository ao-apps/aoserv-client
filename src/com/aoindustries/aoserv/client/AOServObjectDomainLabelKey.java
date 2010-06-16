package com.aoindustries.aoserv.client;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.validator.DomainLabel;
import java.rmi.RemoteException;

/**
 * An object that uses a DomainLabel as its key value.
 *
 * @author  AO Industries, Inc.
 */
abstract public class AOServObjectDomainLabelKey<T extends AOServObjectDomainLabelKey<T>> extends AOServObject<DomainLabel,T> {

    private static final long serialVersionUID = 1L;

    private DomainLabel key;

    protected AOServObjectDomainLabelKey(AOServService<?,?,DomainLabel,T> service, DomainLabel key) {
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
    final public DomainLabel getKey() {
        return key;
    }

    @Override
    final public boolean equals(Object o) {
        if(o==null || getClass()!=o.getClass()) return false;
        @SuppressWarnings("unchecked") T other = (T)o;
        return key==other.key; // OK because interned
    }

    /**
     * Compares keys.
     */
    @Override
    protected int compareToImpl(T other) throws RemoteException {
        return key.compareTo(other.key);
    }

    /**
     * The default string representation is that of the key value.
     */
    @Override
    String toStringImpl() throws RemoteException {
        return key.toString();
    }
}
