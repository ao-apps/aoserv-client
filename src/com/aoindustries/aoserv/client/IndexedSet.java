/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * An unmodifiable set that is also indexed.
 *
 * @author  AO Industries, Inc.
 *
 * @see  AOServService
 */
final public class IndexedSet<E> implements Set<E>, Indexed<E>, Serializable {

    private static final long serialVersionUID = 1L;

    public static final IndexedSet EMPTY_INDEXED_SET = new IndexedSet();

    @SuppressWarnings("unchecked")
    public static final <T> IndexedSet<T> emptyIndexedSet() {
        return (IndexedSet<T>) EMPTY_INDEXED_SET;
    }

    /**
     * Chooses the best constructor.
     */
    public static <T> IndexedSet<T> wrap(Set<T> wrapped) {
        int size = wrapped.size();
        if(size==0) return emptyIndexedSet();
        if(wrapped instanceof IndexedSet) return (IndexedSet<T>)wrapped;
        if(size==1) return new IndexedSet<T>(wrapped.iterator().next());
        return new IndexedSet<T>(wrapped);
    }

    private final Set<E> wrapped;

    public IndexedSet() {
        this.wrapped = Collections.emptySet();
    }

    public IndexedSet(E value) {
        this.wrapped = Collections.singleton(value);
    }

    /**
     * The wrapped set should not change underneath this set.
     */
    public IndexedSet(Set<E> wrapped) {
        if(wrapped==null) throw new IllegalArgumentException("wrapped==null");
        this.wrapped = wrapped;
    }

    public int size() {
        return wrapped.size();
    }

    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    public boolean contains(Object o) {
        return wrapped.contains(o);
    }

    public Iterator<E> iterator() {
        return new Iterator<E>() {

            private final Iterator<E> iter = wrapped.iterator();

            public boolean hasNext() {
                return iter.hasNext();
            }

            public E next() {
                return iter.next();
            }

            public void remove() {
        	    throw new UnsupportedOperationException();
            }
        };
    }

    public Object[] toArray() {
        return wrapped.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return wrapped.toArray(a);
    }

    public boolean add(E e) {
	    throw new UnsupportedOperationException();
    }

    public boolean remove(Object o) {
	    throw new UnsupportedOperationException();
    }

    public boolean containsAll(Collection<?> c) {
        return wrapped.containsAll(c);
    }

    public boolean addAll(Collection<? extends E> c) {
	    throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection<?> c) {
	    throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection<?> c) {
	    throw new UnsupportedOperationException();
    }

    public void clear() {
	    throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        return this==o || wrapped.equals(o);
    }

    @Override
    public int hashCode() {
        return wrapped.hashCode();
    }

    @Override
    public String toString() {
        return wrapped.toString();
    }

    /**
     * Use the singleton for any empty set.
     */
    private Object readResolve() {
        return wrapped.isEmpty() ? EMPTY_INDEXED_SET : this;
    }

    public E filterUnique(String columnName, Object value) throws RemoteException {
        throw new UnsupportedOperationException("TODO: Not supported yet.");
    }

    public IndexedSet<E> filterUniqueSet(String columnName, Set<?> values) throws RemoteException {
        throw new UnsupportedOperationException("TODO: Not supported yet.");
    }

    public IndexedSet<E> filterIndexed(String columnName, Object value) throws RemoteException {
        throw new UnsupportedOperationException("TODO: Not supported yet.");
    }
}
