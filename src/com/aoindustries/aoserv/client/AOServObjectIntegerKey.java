/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.rmi.RemoteException;
import java.util.Locale;

/**
 * An object that uses a int as its key value.
 *
 * @see  AOServService
 *
 * @author  AO Industries, Inc.
 */
abstract public class AOServObjectIntegerKey<T extends AOServObjectIntegerKey<T>> extends AOServObject<Integer,T> {

    private static final long serialVersionUID = 1L;

    final protected int key;

    protected AOServObjectIntegerKey(AOServServiceIntegerKey<?,?,T> service, int key) {
        super(service);
        this.key = key;
    }

    /**
     * Gets the key value for this object.
     */
    final public Integer getKey() {
        return key;
    }

    @Override
    final public boolean equals(Object o) {
        if(o==null) return false;
        Class<? extends AOServObjectIntegerKey> clazz = getClass();
        return clazz==o.getClass() && key==clazz.cast(o).key;
    }

    /**
     * Compares keys in numerical order.
     */
    @Override
    protected int compareToImpl(T other) throws RemoteException {
        return AOServObjectUtils.compare(key, other.key);
    }

    @Override
    final public int hashCode() {
        return key;
    }

    /**
     * The default string representation is that of the key value.
     */
    @Override
    String toStringImpl(Locale userLocale) throws RemoteException {
        return Integer.toString(key);
    }
}
