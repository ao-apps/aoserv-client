/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.rmi.RemoteException;

/**
 * An object that uses a short as its key value.
 *
 * @see  AOServService
 *
 * @author  AO Industries, Inc.
 */
abstract public class AOServObjectShortKey extends AOServObject<Short> {

    // TODO: private static final long serialVersionUID = 1L;

    final protected short key;

    protected AOServObjectShortKey(AOServConnector connector, short key) {
        super(connector);
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
    final public boolean equals(Object o) {
        if(o==null || getClass()!=o.getClass()) return false;
        AOServObjectShortKey other = (AOServObjectShortKey)o;
        return key==other.key;
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
