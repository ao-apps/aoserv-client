/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.AoCollections;
import com.aoindustries.util.HashCodeComparator;
import com.aoindustries.util.UnionClassSet;
import com.aoindustries.util.UnmodifiableArraySet;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
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

    private static final long serialVersionUID = 7104855900076848927L;

    public static final Map<ServiceName,IndexedSet> EMPTY_INDEXED_SETS;
    static {
        Map<ServiceName,IndexedSet> emptyIndexedSets = new EnumMap<ServiceName,IndexedSet>(ServiceName.class);
        for(ServiceName serviceName : ServiceName.values) {
            Set<AOServObject> emptySet = Collections.emptySet();
            emptyIndexedSets.put(serviceName, new IndexedSet<AOServObject>(serviceName, emptySet));
        }
        EMPTY_INDEXED_SETS = AoCollections.optimalUnmodifiableMap(emptyIndexedSets);
    }

    @SuppressWarnings("unchecked")
    public static final <T extends AOServObject> IndexedSet<T> emptyIndexedSet(ServiceName serviceName) {
        return (IndexedSet<T>) EMPTY_INDEXED_SETS.get(serviceName);
    }

    /**
     * Chooses the best constructor, preferring faster wrapping speed.
     */
    /*
    public static <T extends AOServObject> IndexedSet<T> wrapFast(Set<T> wrapped) {
        int size = wrapped.size();
        if(size==0) return emptyIndexedSet();
        if(wrapped instanceof IndexedSet) return (IndexedSet<T>)wrapped;
        if(size==1) return new IndexedSet<T>(wrapped.iterator().next());
        return new IndexedSet<T>(wrapped);
    }*/

    /**
     * Wraps a single object.
     */
    public static <T extends AOServObject> IndexedSet<T> wrap(ServiceName serviceName, T singleton) {
        return new IndexedSet<T>(serviceName, singleton);
    }

    /**
     * Wraps the provided ArraySet.
     */
    /*
    public static <T extends AOServObject> IndexedSet<T> wrap(ServiceName serviceName, ArraySet<T> wrapped) {
        int size = wrapped.size();
        // Empty
        if(size==0) return emptyIndexedSet(serviceName);
        // Singleton
        if(size==1) return wrap(serviceName, wrapped.iterator().next());
        wrapped.trimToSize();
        return new IndexedSet<T>(serviceName, wrapped);
    }*/

    /**
     * Wraps the provided ArraySortedSet.
     */
    /*
    public static <T extends AOServObject> IndexedSet<T> wrap(ServiceName serviceName, ArraySortedSet<T> wrapped) {
        int size = wrapped.size();
        // Empty
        if(size==0) return emptyIndexedSet(serviceName);
        // Singleton
        if(size==1) return wrap(serviceName, wrapped.iterator().next());
        wrapped.trimToSize();
        return new IndexedSet<T>(serviceName, wrapped);
    }
     */

    /**
     * Wraps an unsorted ArrayList.  The list will be sorted in-place.
     */
    public static <T extends AOServObject<?>> IndexedSet<T> wrap(ServiceName serviceName, ArrayList<T> wrapped) {
        int size = wrapped.size();
        // Empty
        if(size==0) return emptyIndexedSet(serviceName);
        // Singleton
        if(size==1) return wrap(serviceName, wrapped.get(0));
        wrapped.trimToSize();
        Collections.sort(wrapped, HashCodeComparator.getInstance());
        return new IndexedSet<T>(serviceName, new UnmodifiableArraySet<T>(wrapped));
    }

    /**
     * Wraps a UnionClassSet.
     */
    public static <T extends AOServObject<?>> IndexedSet<T> wrap(ServiceName serviceName, UnionClassSet<T> wrapped) {
        int size = wrapped.size(); // Size is fast for this type of UnionSet - this is OK
        // Empty
        if(size==0) return emptyIndexedSet(serviceName);
        // Singleton
        if(size==1) return wrap(serviceName, wrapped.iterator().next());
        return new IndexedSet<T>(serviceName, wrapped);
    }

    /**
     * Chooses the best constructor, preferring the most heap-efficient storage.
     * If possible and at no cost to the existing implementation, provide instances
     * of of ArraySet or ArraySortedSet to avoid creation of them here.
     */
    /*
    public static <T extends AOServObject> IndexedSet<T> wrap(ServiceName serviceName, Set<T> wrapped) {
        int size = wrapped.size();
        // Empty
        if(size==0) return emptyIndexedSet(serviceName);
        // Already IndexedSet
        if(wrapped instanceof IndexedSet) {
            IndexedSet<T> indexedSet = (IndexedSet<T>)wrapped;
            if(indexedSet.serviceName==serviceName) return indexedSet;
            return new IndexedSet<T>(serviceName, indexedSet.wrapped);
        }
        // Singleton
        if(size==1) return wrap(serviceName, wrapped.iterator().next());
        // These are already compact
        if(wrapped instanceof ArraySet) {
            ((ArraySet)wrapped).trimToSize();
            return new IndexedSet<T>(serviceName, wrapped);
        }
        if(wrapped instanceof ArraySortedSet) {
            ((ArraySortedSet)wrapped).trimToSize();
            return new IndexedSet<T>(serviceName, wrapped);
        }
        // Create more compact representation
        if(wrapped instanceof SortedSet) {
            // Make it be a compact ArraySortedSet
            return new IndexedSet<T>(serviceName, new ArraySortedSet<T>((SortedSet<T>)wrapped));
        } else {
            // Make it be a compact ArraySet
            ArrayList<T> elements = new ArrayList<T>(wrapped);
            Collections.sort(elements, HashCodeComparator.getInstance());
            return new IndexedSet<T>(serviceName, new ArraySet<T>(elements));
        }
    }*/

    private final ServiceName serviceName;
    private final Set<E> wrapped;

    /**
     * The unique column objects are hashed on the method return value when first needed.
     */
    private transient Map<MethodColumn,Map<Object,E>> uniqueHashes;

    /**
     * The indexed column objects are hashed on the method return value when first needed.
     */
    private transient Map<MethodColumn,Map<Object,IndexedSet<E>>> indexedHashes;

    private IndexedSet(ServiceName serviceName) {
        this.serviceName = serviceName;
        this.wrapped = Collections.emptySet();
    }

    private IndexedSet(ServiceName serviceName, E value) {
        this.serviceName = serviceName;
        this.wrapped = Collections.singleton(value);
    }

    /**
     * The wrapped set should not change underneath this set.
     */
    private IndexedSet(ServiceName serviceName, Set<E> wrapped) {
        if(wrapped==null) throw new IllegalArgumentException("wrapped==null");
        this.serviceName = serviceName;
        this.wrapped = wrapped;
    }

    /**
     * Use the singletons for any empty set.
     */
    private Object readResolve() {
        return wrapped.isEmpty() ? EMPTY_INDEXED_SETS.get(serviceName) : this;
    }

    public ServiceName getServiceName() {
        return serviceName;
    }

    @Override
    public int size() {
        return wrapped.size();
    }

    @Override
    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return wrapped.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            private final Iterator<E> iter = wrapped.iterator();

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public E next() {
                return iter.next();
            }

            @Override
            public void remove() {
        	    throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public Object[] toArray() {
        return wrapped.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return wrapped.toArray(a);
    }

    @Override
    public boolean add(E e) {
	    throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
	    throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return wrapped.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
	    throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
	    throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
	    throw new UnsupportedOperationException();
    }

    @Override
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

    private Map<Object,E> getUniqueHash(MethodColumn column, Class<?> valueClass) throws RemoteException {
        assert Thread.holdsLock(wrapped);
        if(uniqueHashes==null) uniqueHashes = new HashMap<MethodColumn,Map<Object,E>>();
        Map<Object,E> uniqueHash = uniqueHashes.get(column);
        if(uniqueHash==null) {
            uniqueHash = new HashMap<Object,E>(wrapped.size()*4/3+1);
            try {
                {
                    IndexType indexType = column.getIndexType();
                    if(indexType!=IndexType.PRIMARY_KEY && indexType!=IndexType.UNIQUE && indexType!=IndexType.INDEXED) throw new IllegalArgumentException("Column neither primary key, unique, nor indexed: "+column);
                }
                final Method method = column.getMethod();
                assert valueClass==null || AOServServiceUtils.classesMatch(valueClass, method.getReturnType()) : "value class and return type mismatch: "+valueClass.getName()+"!="+method.getReturnType().getName();
                for(E obj : wrapped) {
                    if(obj!=null) {
                        Object columnValue = method.invoke(obj);
                        if(columnValue!=null && uniqueHash.put(columnValue, obj)!=null) throw new AssertionError("Duplicate value in unique column "+serviceName+"."+column+": "+columnValue);
                    }
                }
            } catch(IllegalAccessException err) {
                throw new RemoteException(err.getMessage(), err);
            } catch(InvocationTargetException err) {
                throw new RemoteException(err.getMessage(), err);
            }
            uniqueHashes.put(column, uniqueHash);
        }
        return uniqueHash;
    }

    @Override
    public E filterUnique(MethodColumn column, Object value) throws RemoteException {
        if(value==null || wrapped.isEmpty()) return null;
        synchronized(wrapped) {
            return getUniqueHash(column, value.getClass()).get(value);
        }
    }

    @Override
    public IndexedSet<E> filterUniqueSet(MethodColumn column, Set<?> values) throws RemoteException {
        if(values==null || values.isEmpty() || wrapped.isEmpty()) return emptyIndexedSet(serviceName);
        synchronized(wrapped) {
            ArrayList<E> results;
            Map<Object,E> uniqueHash = getUniqueHash(column, null);
            if(values.size()<uniqueHash.size()) {
                results = new ArrayList<E>(values.size());
                for(Object value : values) {
                    if(value!=null) {
                        E obj = uniqueHash.get(value);
                        if(obj!=null) results.add(obj);
                    }
                }
            } else {
                results = new ArrayList<E>(uniqueHash.size());
                for(Map.Entry<Object,E> entry : uniqueHash.entrySet()) {
                    Object value = entry.getKey();
                    if(values.contains(value)) {
                        results.add(entry.getValue());
                    }
                }
            }
            return wrap(serviceName, results);
        }
    }

    private Map<Object,IndexedSet<E>> getIndexHash(MethodColumn column, Class<?> valueClass) throws RemoteException {
        assert Thread.holdsLock(wrapped);
        if(indexedHashes==null) indexedHashes = new HashMap<MethodColumn,Map<Object,IndexedSet<E>>>();
        Map<Object,IndexedSet<E>> indexedHash = indexedHashes.get(column);
        if(indexedHash==null) {
            Map<Object,ArrayList<E>> listByValue = new HashMap<Object,ArrayList<E>>(wrapped.size()*4/3+1); // Error on the side of avoiding rehash
            try {
                if(column.getIndexType()!=IndexType.INDEXED) throw new IllegalArgumentException("Column not indexed: "+column);
                final Method method = column.getMethod();
                assert valueClass==null || AOServServiceUtils.classesMatch(valueClass, method.getReturnType()) : "value class and return type mismatch: "+valueClass.getName()+"!="+method.getReturnType().getName();
                for(E obj : wrapped) {
                    if(obj!=null) {
                        Object columnValue = method.invoke(obj);
                        if(columnValue!=null) {
                            ArrayList<E> results = listByValue.get(columnValue);
                            if(results==null) listByValue.put(columnValue, results = new ArrayList<E>());
                            results.add(obj);
                        }
                    }
                }
            } catch(IllegalAccessException err) {
                throw new RemoteException(err.getMessage(), err);
            } catch(InvocationTargetException err) {
                // ErrorPrinter.printStackTraces(err);
                throw new RemoteException(err.getMessage(), err);
            }
            // Make each list indexed
            indexedHash = new HashMap<Object,IndexedSet<E>>(listByValue.size()*4/3+1);
            for(Map.Entry<Object,ArrayList<E>> entry : listByValue.entrySet()) {
                ArrayList<E> list = entry.getValue();
                indexedHash.put(entry.getKey(), wrap(serviceName, list));
            }
            indexedHashes.put(column, indexedHash);
        }
        return indexedHash;
    }

    @Override
    public IndexedSet<E> filterIndexed(MethodColumn column, Object value) throws RemoteException {
        if(value==null || wrapped.isEmpty()) return emptyIndexedSet(serviceName);
        synchronized(wrapped) {
            IndexedSet<E> results = getIndexHash(column, value.getClass()).get(value);
            if(results==null) return emptyIndexedSet(serviceName);
            return results;
        }
    }

    @Override
    public IndexedSet<E> filterIndexedSet(MethodColumn column, Set<?> values) throws RemoteException {
        if(values==null || values.isEmpty() || wrapped.isEmpty()) return emptyIndexedSet(serviceName);
        synchronized(wrapped) {
            ArrayList<E> results;
            Map<Object,IndexedSet<E>> indexHash = getIndexHash(column, null);
            if(values.size()<indexHash.size()) {
                results = new ArrayList<E>(values.size());
                for(Object value : values) {
                    if(value!=null) {
                        IndexedSet<E> objs = indexHash.get(value);
                        if(objs!=null) results.addAll(objs);
                    }
                }
            } else {
                results = new ArrayList<E>(indexHash.size());
                for(Map.Entry<Object,IndexedSet<E>> entry : indexHash.entrySet()) {
                    Object value = entry.getKey();
                    if(values.contains(value)) results.addAll(entry.getValue());
                }
            }
            return wrap(serviceName, results);
        }
    }
}
