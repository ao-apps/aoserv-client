package com.aoindustries.aoserv.client;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.validator.UserId;
import java.rmi.RemoteException;
import java.util.Locale;

/**
 * An object that uses a UserId as its key value.
 *
 * @author  AO Industries, Inc.
 */
abstract public class AOServObjectUserIdKey<T extends AOServObjectUserIdKey<T>> extends AOServObject<UserId,T> {

    private static final long serialVersionUID = 1L;

    final protected UserId key;

    protected AOServObjectUserIdKey(AOServServiceUserIdKey<?,?,T> service, UserId key) {
        super(service);
        this.key = key.intern();
    }

    /**
     * Gets the key value for this object.
     */
    final public UserId getKey() {
        return key;
    }

    /**
     * Compares keys in a case-insensitive manner using the English locale.
     */
    @Override
    protected int compareToImpl(T other) throws RemoteException {
        return key.compareTo(other.key);
    }

    /**
     * The default string representation is that of the key value.
     */
    @Override
    String toStringImpl(Locale userLocale) throws RemoteException {
        return key.toString();
    }
}