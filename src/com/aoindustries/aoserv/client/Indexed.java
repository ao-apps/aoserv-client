package com.aoindustries.aoserv.client;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.rmi.RemoteException;
import java.util.Set;

/**
 * @author  AO Industries, Inc.
 *
 * @see  AOServService
 */
public interface Indexed<E extends AOServObject> {

    /**
     * Gets the object having the provided column value.  Like SQL, a <code>null</code> value will not match
     * any rows.
     * The column must have an index type of PRIMARY_KEY, UNIQUE, or INDEXED.
     */
    E filterUnique(String columnName, Object value) throws RemoteException;

    /**
     * Gets the set of objects having one of the provided column values.  Like SQL, a <code>null</code> value will not match
     * any rows.
     * The column must have an index type of PRIMARY_KEY, UNIQUE, or INDEXED.
     */
    IndexedSet<E> filterUniqueSet(String columnName, Set<?> values) throws RemoteException;

    /**
     * Gets the set of objects having the provided column value.  Like SQL, a <code>null</code> value will not match
     * any rows.
     * The column must have an index type of INDEXED.
     */
    IndexedSet<E> filterIndexed(String columnName, Object value) throws RemoteException;

    /**
     * Gets the set of objects having one of the provided column values.  Like SQL, a <code>null</code> value will not match
     * any rows.
     */
    //IndexedSet<E> getIndexedSet(String columnName, Set<?> values) throws RemoteException;

    /**
     * Gets the set of distinct column values for the provided column name.
     *
     * @throws ClassCastException if the return type of the column is not assignable to the expected type.
     */
    //<T> IndexedSet<T> join(Class<T> clazz, String columnName) throws RemoteException;
}
