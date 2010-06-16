/*
 * Copyright 2006-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.rmi.RemoteException;

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

    protected AOServObjectIntegerKey(AOServService<?,?,Integer,T> service, int key) {
        super(service);
        this.key = key;
    }

    /**
     * Gets the key value for this object.
     */
    @Override
    final public Integer getKey() {
        return key;
    }

    @Override
    final public boolean equals(Object o) {
        if(o==null || getClass()!=o.getClass()) return false;
        @SuppressWarnings("unchecked") T other = (T)o;
        return key==other.key;
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
    String toStringImpl() throws RemoteException {
        return Integer.toString(key);
    }
}
