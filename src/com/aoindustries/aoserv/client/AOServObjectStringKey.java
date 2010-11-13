package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.rmi.RemoteException;

/**
 * An object that uses a String as its key value.
 *
 * @see  AOServService
 *
 * @author  AO Industries, Inc.
 */
abstract public class AOServObjectStringKey<T extends AOServObjectStringKey<T> & Comparable<T> & DtoFactory<?>> extends AOServObject<String,T> {

    private static final long serialVersionUID = 1L;

    private String key;

    protected AOServObjectStringKey(AOServService<?,?,String,T> service, String key) {
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
    final public String getKey() {
        return key;
    }

    @Override
    final public boolean equals(Object o) {
        if(o==null || getClass()!=o.getClass()) return false;
        AOServObjectStringKey other = (AOServObjectStringKey)o;
        return key==other.key; // OK because interned
    }

    /**
     * Compares keys in a case-insensitive manner using the English locale.
     */
    @Override
    public int compareTo(T other) {
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(key, other.getKey());
    }

    /**
     * The default string representation is that of the key value.
     */
    @Override
    String toStringImpl() throws RemoteException {
        return key;
    }
}
