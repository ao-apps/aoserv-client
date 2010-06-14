package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.rmi.RemoteException;

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

    protected AOServObjectShortKey(AOServService<?,?,Short,T> service, short key) {
        super(service);
        this.key = key;
    }

    /**
     * Gets the key value for this object.
     */
    @Override
    final public Short getKey() {
        return key;
    }

    @Override
    final public boolean equals(T other) {
        if(other==null) return false;
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
        return Short.toString(key);
    }
}
