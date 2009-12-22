package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.rmi.RemoteException;
import java.util.Locale;

/**
 * An object that uses a String as its key value.
 *
 * @see  AOServService
 *
 * @author  AO Industries, Inc.
 */
abstract public class AOServObjectStringKey<T extends AOServObjectStringKey<T>> extends AOServObject<String,T> {

    private static final long serialVersionUID = 1L;

    final protected String key;

    protected AOServObjectStringKey(AOServServiceStringKey<?,?,T> service, String key) {
        super(service);
        this.key = key;
    }

    /**
     * Gets the key value for this object.
     */
    final public String getKey() {
        return key;
    }

    /**
     * Compares keys in a case-insensitive manner using the English locale.
     */
    @Override
    protected int compareToImpl(T other) throws RemoteException {
        return compareIgnoreCaseConsistentWithEquals(key, other.key);
    }

    /**
     * The default string representation is that of the key value.
     */
    @Override
    String toStringImpl(Locale userLocale) throws RemoteException {
        return key;
    }
}
