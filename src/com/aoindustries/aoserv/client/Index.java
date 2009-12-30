package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * Provides indexed access to an <code>AOServService</code>.
 *
 * @author  AO Industries, Inc.
 */
public interface Index<T extends Comparable<T>, K extends Comparable<K>, V extends AOServObject<K,V>> extends Remote {

    /**
     * Gets the set of objects having the provided column value.  Like SQL, a <code>null</code> value will not match
     * any rows.
     */
    Set<AOServObject<K,V>> get(T value) throws RemoteException;

    /**
     * Gets the set of objects having one of the provided column values.  Like SQL, a <code>null</code> value will not match
     * any rows.
     */
    Set<AOServObject<K,V>> get(Set<T> values) throws RemoteException;
}
