package com.aoindustries.aoserv.client;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.validator.*;
import java.rmi.RemoteException;

/**
 * An object that uses a GroupId as its key value.
 *
 * @author  AO Industries, Inc.
 */
abstract public class AOServObjectGroupIdKey extends AOServObject<GroupId> {

    private static final long serialVersionUID = 1L;

    private GroupId key;

    protected AOServObjectGroupIdKey(AOServConnector<?,?> connector, GroupId key) {
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
    final public GroupId getKey() {
        return key;
    }

    @Override
    final public boolean equals(Object o) {
        if(o==null || getClass()!=o.getClass()) return false;
        AOServObjectGroupIdKey other = (AOServObjectGroupIdKey)o;
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
