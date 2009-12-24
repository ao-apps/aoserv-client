package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.Table;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * An <code>AOServService</code> provides access to one
 * set of <code>AOServObject</code>s.  The subclasses often provide additional
 * methods for manipulating the data outside the scope
 * of a single <code>AOServObject</code>.
 *
 * @author  AO Industries, Inc.
 *
 * @see  AOServObject
 */
public interface AOServService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>, K extends Comparable<K>,V extends AOServObject<K,V>> extends Remote {

    /**
     * The toString should be the service name.
     */
    //String toString();

    /**
     * Gets the connector that this service is part of.
     */
    C getConnector() throws RemoteException;

    /**
     * Gets the set of all accessible objects.  These objects may or
     * may not be sorted.  Unless otherwise necessary, the set view of the data
     * should be preferred due to being the fastetst.  This set represents an
     * unmodifiable snapshot of all objects in the service and will not change
     * even when the underlying data has changed.
     *
     * @return  a <code>Set</code> containing all of the objects
     */
    Set<V> getSet() throws RemoteException;

    /**
     * Gets the sorted set of all accessible objects, using their natural ordering.
     * This set represents an unmodifiable snapshot of all objects in the service
     * and will not change even when the underlying data has changed.
     *
     * @return  a <code>SortedSet</code> containing all of the objects
     */
    SortedSet<V> getSortedSet() throws RemoteException;

    /**
     * Gets the ServiceName for this service by finding the interface that
     * has the SchemaAnnotation annotation.
     */
    ServiceName getServiceName() throws RemoteException;

    /**
     * Gets a table view of this data.  This uses a fair amount of reflection
     * and will not perform as well as other views of the data.  Unlike the
     * <code>getSet</code> and <code>getSortedSet</code> methods, this does not
     * completely act as a snapshot of the underlying data.
     */
    Table<V> getTable() throws RemoteException;

    /**
     * Gets a <code>Map</code> view of this service.  Unlike the <code>getSet</code>
     * and <code>getSortedSet</code> methods, this does not completely act as
     * a snapshot of the underlying data.
     */
    Map<K,V> getMap() throws RemoteException;

    /**
     * Gets the value for the associated key or <code>null</code> if the data
     * doesn't exist or is filtered.
     */
    V get(K key) throws RemoteException;

    /**
     * Determines if this service is empty.  It is empty when <code>getSet</code>
     * is empty.
     */
    boolean isEmpty() throws RemoteException;

    /**
     * Gets the size of this service as obtained by <code>getSet().size()</code>.
     *
     * @see #size()
     */
    int getSize() throws RemoteException;
}
