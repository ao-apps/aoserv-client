/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import java.rmi.RemoteException;

/**
 * An object that uses a UnixPath as its key value.
 *
 * @author  AO Industries, Inc.
 */
abstract public class AOServObjectUnixPathKey extends AOServObject<UnixPath> {

    // TODO: private static final long serialVersionUID = 1L;

    private UnixPath key;

    protected AOServObjectUnixPathKey(AOServConnector connector, UnixPath key) {
        super(connector);
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

    @Override
    final public boolean equals(Object o) {
        if(o==null || getClass()!=o.getClass()) return false;
        AOServObjectUnixPathKey other = (AOServObjectUnixPathKey)o;
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
