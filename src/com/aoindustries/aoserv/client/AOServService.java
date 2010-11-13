package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.Table;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

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
public interface AOServService<
    C extends AOServConnector<C,F>,
    F extends AOServConnectorFactory<C,F>,
    K extends Comparable<K>,
    V extends AOServObject<K> & Comparable<V> & DtoFactory<?>
> extends Indexed<V>, Remote {

    /**
     * The toString should be the service name.
     */
    //String toString();

    /**
     * Gets the connector that this service is part of.
     */
    C getConnector() throws RemoteException;

    /**
     * Checks if this service returns objects that may safely have their service reset without copying.
     * This implies that this service performs no caching of the objects.
     */
    boolean isAoServObjectServiceSettable() throws RemoteException;

    /**
     * Gets the set of all accessible objects.  These objects may or
     * may not be sorted.  Unless otherwise necessary, the set view of the data
     * should be preferred due to being the fastetst.  This set represents an
     * unmodifiable snapshot of all objects in the service and will not change
     * even when the underlying data has changed.
     *
     * @return  a <code>Set</code> containing all of the objects
     */
    IndexedSet<V> getSet() throws RemoteException;

    /**
     * Gets the ServiceName for this service by finding the interface that
     * has the SchemaAnnotation annotation.
     */
    ServiceName getServiceName() throws RemoteException;

    /**
     * Gets a table view of this data.  This uses a fair amount of reflection
     * and will not perform as well as other views of the data.  Unlike the
     * <code>getSet</code> method, this does not completely act as a snapshot of
     * the underlying data.
     */
    Table<MethodColumn,V> getTable() throws RemoteException;

    /**
     * Gets a <code>Map</code> view of this service.  Unlike the <code>getSet</code>
     * and <code>getSortedSet</code> methods, this does not completely act as
     * a snapshot of the underlying data.
     */
    Map<K,V> getMap() throws RemoteException;

    /**
     * Determines if this service is empty.  It is empty when <code>getSet</code>
     * is empty.
     */
    boolean isEmpty() throws RemoteException;

    /**
     * Gets the size of this service as obtained by <code>getSet().size()</code>.
     */
    int getSize() throws RemoteException;

    /**
     * Gets the object having the provided key value.  Like SQL, a <code>null</code> value will not match
     * any rows.  Will throw <code>NoSuchElementException</code> if the value doesn't exist.
     *
     * @throws  NoSuchElementException if the element doesn't exist (including because value is null)
     */
    V get(K key) throws RemoteException, NoSuchElementException;

    /**
     * Gets the set of objects having any of the provided key values.  Like SQL, a <code>null</code> value will not match
     * any rows.
     */
    // Create when first needed: Set<V> getSet(Set<K> keys) throws RemoteException;

    /**
     * Gets the object having the provided column value.  Like SQL, a <code>null</code> value will not match
     * any rows.
     * The column must have an index type of PRIMARY_KEY or UNIQUE.
     */
    @Override
    V filterUnique(String columnName, Object value) throws RemoteException;

    /**
     * Gets the set of objects having one of the provided column values.  Like SQL, a <code>null</code> value will not match
     * any rows.
     * The column must have an index type of PRIMARY_KEY or UNIQUE.
     */
    @Override
    IndexedSet<V> filterUniqueSet(String columnName, Set<?> values) throws RemoteException;

    /**
     * Gets the set of objects having the provided column value.  Like SQL, a <code>null</code> value will not match
     * any rows.
     * The column must have an index type of INDEXED.
     */
    @Override
    IndexedSet<V> filterIndexed(String columnName, Object value) throws RemoteException;

    /**
     * Gets the set of objects having one of the provided column values.  Like SQL, a <code>null</code> value will not match
     * any rows.
     * The column must have an index type of INDEXED.
     */
    @Override
    IndexedSet<V> filterIndexedSet(String columnName, Set<?> values) throws RemoteException;
}
