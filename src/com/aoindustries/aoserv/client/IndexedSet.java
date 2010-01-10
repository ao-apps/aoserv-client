/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * An unmodifiable set that is also indexed.
 *
 * @author  AO Industries, Inc.
 *
 * @see  AOServService
 */
final public class IndexedSet<E extends AOServObject> implements Set<E>, Indexed<E>, Serializable {

    private static final long serialVersionUID = 1L;

    public static final IndexedSet EMPTY_INDEXED_SET = new IndexedSet();

    @SuppressWarnings("unchecked")
    public static final <T extends AOServObject> IndexedSet<T> emptyIndexedSet() {
        return (IndexedSet<T>) EMPTY_INDEXED_SET;
    }

    /**
     * Chooses the best constructor.
     */
    public static <T extends AOServObject> IndexedSet<T> wrap(Set<T> wrapped) {
        int size = wrapped.size();
        if(size==0) return emptyIndexedSet();
        if(wrapped instanceof IndexedSet) return (IndexedSet<T>)wrapped;
        if(size==1) return new IndexedSet<T>(wrapped.iterator().next());
        return new IndexedSet<T>(wrapped);
    }

    private final Set<E> wrapped;

    /**
     * The unique column objects are hashed on the method return value when first needed.
     */
    private transient Map<String,Map<Object,E>> uniqueHashes;

    /**
     * The indexed column objects are hashed on the method return value when first needed.
     */
    private transient Map<String,Map<Object,IndexedSet<E>>> indexedHashes;

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

    private Map<Object,E> getUniqueHash(String columnName, Class<?> valueClass) throws RemoteException {
        assert Thread.holdsLock(wrapped);
        if(uniqueHashes==null) uniqueHashes = new HashMap<String,Map<Object,E>>();
        Map<Object,E> uniqueHash = uniqueHashes.get(columnName);
        if(uniqueHash==null) {
            uniqueHash = new HashMap<Object,E>(wrapped.size()*4/3+1);
            try {
                Method method = null;
                Class<?> lastClass = null;
                for(E obj : wrapped) {
                    if(obj!=null) {
                        Class<? extends AOServObject> objClass = obj.getClass();
                        if(objClass!=lastClass) {
                            MethodColumn methodColumn = AOServObjectUtils.getMethodColumnMap(objClass).get(columnName);
                            IndexType indexType = methodColumn.getIndexType();
                            if(indexType!=IndexType.PRIMARY_KEY && indexType!=IndexType.UNIQUE && indexType!=IndexType.INDEXED) throw new IllegalArgumentException("Column neither primary key, unique, or indexed: "+columnName);
                            method = methodColumn.getMethod();
                            assert valueClass==null || AOServServiceUtils.classesMatch(valueClass, method.getReturnType()) : "value class and return type mismatch: "+valueClass.getName()+"!="+method.getReturnType().getName();
                            lastClass = objClass;
                        }
                        Object columnValue = method.invoke(obj);
                        if(columnValue!=null && uniqueHash.put(columnValue, obj)!=null) throw new AssertionError("Duplicate value in unique column "+obj.getService().getServiceName()+"."+columnName+": "+columnValue);
                    }
                }
            } catch(IllegalAccessException err) {
                throw new RemoteException(err.getMessage(), err);
            } catch(InvocationTargetException err) {
                throw new RemoteException(err.getMessage(), err);
            }
            uniqueHashes.put(columnName, uniqueHash);
        }
        return uniqueHash;
    }

    public E filterUnique(String columnName, Object value) throws RemoteException {
        if(value==null) return null;
        synchronized(wrapped) {
            return getUniqueHash(columnName, value.getClass()).get(value);
        }
    }

    public IndexedSet<E> filterUniqueSet(String columnName, Set<?> values) throws RemoteException {
        if(values==null || values.isEmpty()) return null;
        synchronized(wrapped) {
            Set<E> results = new HashSet<E>();
            Map<Object,E> uniqueHash = getUniqueHash(columnName, null);
            if(values.size()<uniqueHash.size()) {
                for(Object value : values) {
                    if(value!=null) {
                        E obj = uniqueHash.get(value);
                        if(obj!=null && !results.add(obj)) throw new AssertionError("Duplicate value in unique column "+obj.getService().getServiceName()+"."+columnName+": "+value);
                    }
                }
            } else {
                for(Map.Entry<Object,E> entry : uniqueHash.entrySet()) {
                    Object value = entry.getKey();
                    if(values.contains(value)) {
                        E obj = entry.getValue();
                        if(!results.add(obj)) throw new AssertionError("Duplicate value in unique column "+obj.getService().getServiceName()+"."+columnName+": "+value);
                    }
                }
            }
            return wrap(results);
        }
    }

    public IndexedSet<E> filterIndexed(String columnName, Object value) throws RemoteException {
        if(value==null) return null;
        synchronized(wrapped) {
            if(indexedHashes==null) indexedHashes = new HashMap<String,Map<Object,IndexedSet<E>>>();
            Map<Object,IndexedSet<E>> indexedHash = indexedHashes.get(columnName);
            if(indexedHash==null) {
                Map<Object,Set<E>> setByValue = new HashMap<Object,Set<E>>(wrapped.size()*4/3+1); // Error on the side of avoiding rehash
                try {
                    Method method = null;
                    Class<?> lastClass = null;
                    for(E obj : wrapped) {
                        if(obj!=null) {
                            Class<? extends AOServObject> objClass = obj.getClass();
                            if(objClass!=lastClass) {
                                MethodColumn methodColumn = AOServObjectUtils.getMethodColumnMap(objClass).get(columnName);
                                if(methodColumn.getIndexType()!=IndexType.INDEXED) throw new IllegalArgumentException("Column not indexed: "+columnName);
                                method = methodColumn.getMethod();
                                lastClass = objClass;
                            }
                            Object columnValue = method.invoke(obj);
                            if(columnValue!=null) {
                                Set<E> results = setByValue.get(columnValue);
                                if(results==null) setByValue.put(columnValue, results = new HashSet<E>());
                                results.add(obj);
                            }
                        }
                    }
                } catch(IllegalAccessException err) {
                    throw new RemoteException(err.getMessage(), err);
                } catch(InvocationTargetException err) {
                    throw new RemoteException(err.getMessage(), err);
                }
                // Make each set indexed
                indexedHash = new HashMap<Object,IndexedSet<E>>(setByValue.size()*4/3+1);
                for(Map.Entry<Object,Set<E>> entry : setByValue.entrySet()) indexedHash.put(entry.getKey(), wrap(entry.getValue()));
                indexedHashes.put(columnName, indexedHash);
            }
            IndexedSet<E> results = indexedHash.get(value);
            if(results==null) return emptyIndexedSet();
            return results;
        }
    }
}
