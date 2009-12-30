package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.rmi.RemoteException;
import java.util.Locale;

/**
 * An object that uses a short as its key value.
 *
 * @see  AOServService
 *
 * @author  AO Industries, Inc.
 */
abstract public class AOServObjectShortKey<T extends AOServObjectShortKey<T>> extends AOServObject<Short,T> {

    private static final long serialVersionUID = 1L;

    final protected short key;

    protected AOServObjectShortKey(AOServServiceShortKey<?,?,T> service, short key) {
        super(service);
        this.key = key;
    }

    /**
     * Gets the key value for this object.
     */
    final public Short getKey() {
        return key;
    }

    @Override
    final public boolean equals(Object o) {
        if(o==null) return false;
        Class<? extends AOServObjectShortKey> clazz = getClass();
        return clazz==o.getClass() && key==clazz.cast(o).key;
    }

    /**
     * Compares keys in numerical order.
     */
    @Override
    protected int compareToImpl(T other) throws RemoteException {
        return compare(key, other.key);
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
        return Short.toString(key);
    }
}
