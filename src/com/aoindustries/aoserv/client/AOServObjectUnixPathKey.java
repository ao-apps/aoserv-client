package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.validator.UnixPath;
import java.rmi.RemoteException;

/**
 * An object that uses a UnixPath as its key value.
 *
 * @author  AO Industries, Inc.
 */
abstract public class AOServObjectUnixPathKey<T extends AOServObjectUnixPathKey<T>> extends AOServObject<UnixPath,T> {

    private static final long serialVersionUID = 1L;

    private UnixPath key;

    protected AOServObjectUnixPathKey(AOServService<?,?,UnixPath,T> service, UnixPath key) {
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
    final public UnixPath getKey() {
        return key;
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
