/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A service that represents the abstract parent class of several other services.
 * The other services are individually queried and the results are presented
 * as an aggregate view of their common base class.
 *
 * @author  AO Industries, Inc.
 */
abstract public class UnionService<K extends Comparable<K>, V extends AOServObject<K>> extends AbstractService<K,V> {

    protected final AOServConnector connector;

    /**
     * The internal objects are stored in an unmodifiable set
     * for access to the entire table.
     */
    private final Object cachedSetLock = new Object();
    private List<IndexedSet<? extends V>> cachedSets;
    private IndexedSet<V> cachedSet;

    protected UnionService(AOServConnector connector, Class<K> keyClass, Class<V> valueClass) {
        super(keyClass, valueClass);
        this.connector = connector;
    }

    @Override
    final public AOServConnector getConnector() {
        return connector;
    }

    /**
     * Gets the individual services that should be combined into a single view.
     * They must be returned in the same order every time so the cached
     * union may be reused when no underlying set has changed.
     */
    protected abstract List<AOServService<K,? extends V>> getSubServices() throws RemoteException;

    @Override
    final public IndexedSet<V> getSet() throws RemoteException {
        List<AOServService<K,? extends V>> subservices = getSubServices();
        List<IndexedSet<? extends V>> sets = new ArrayList<IndexedSet<? extends V>>(subservices.size());
        for(AOServService<K,? extends V> subservice : subservices) sets.add(subservice.getSet());
        synchronized(cachedSetLock) {
            // Reuse cache if no underlying set has changed
            if(cachedSet!=null) {
                int size = cachedSets.size();
                if(size!=sets.size()) throw new AssertionError("size!=sets.size(): "+size+"!="+sets.size());
                boolean setChanged = false;
                for(int i=0; i<size; i++) {
                    if(sets.get(i)!=cachedSets.get(i)) {
                        setChanged = true;
                        break;
                    }
                }
                if(!setChanged) return cachedSet;
            }
            int totalSize = 0;
            for(IndexedSet<? extends V> set : sets) totalSize += set.size();
            ArrayList<V> list = new ArrayList<V>(totalSize);
            for(IndexedSet<? extends V> set : sets) list.addAll(set);
            cachedSet = IndexedSet.wrap(getServiceName(), list);
            cachedSets = sets;
            return cachedSet;
        }
    }

    @Override
    final public boolean isEmpty() throws RemoteException {
        for(AOServService<K,? extends V> subservice : getSubServices()) if(!subservice.isEmpty()) return false;
        return true;
    }

    @Override
    final public int getSize() throws RemoteException {
        int totalSize = 0;
        for(AOServService<K,? extends V> subservice : getSubServices()) totalSize += subservice.getSize();
        return totalSize;
    }

    @Override
    final public V get(K key) throws RemoteException, NoSuchElementException {
        if(key==null) return null;
        for(AOServService<K,? extends V> subservice : getSubServices()) {
            try {
                return subservice.get(key);
            } catch(NoSuchElementException err) {
                // Try next subset
            }
        }
        throw new NoSuchElementException("service="+getServiceName()+", key="+key);
    }

    @Override
    final public V filterUnique(String columnName, Object value) throws RemoteException {
        if(value==null) return null;
        IndexType indexType = table.getColumn(columnName).getIndexType();
        if(indexType!=IndexType.PRIMARY_KEY && indexType!=IndexType.UNIQUE) throw new IllegalArgumentException("Column neither primary key nor unique: "+columnName);
        return getSet().filterUnique(columnName, value);
    }

    @Override
    final public IndexedSet<V> filterUniqueSet(String columnName, Set<?> values) throws RemoteException {
        if(values==null || values.isEmpty()) return IndexedSet.emptyIndexedSet(getServiceName());
        IndexType indexType = table.getColumn(columnName).getIndexType();
        if(indexType!=IndexType.PRIMARY_KEY && indexType!=IndexType.UNIQUE) throw new IllegalArgumentException("Column neither primary key nor unique: "+columnName);
        return getSet().filterUniqueSet(columnName, values);
    }

    @Override
    final public IndexedSet<V> filterIndexed(String columnName, Object value) throws RemoteException {
        if(value==null) return IndexedSet.emptyIndexedSet(getServiceName());
        return getSet().filterIndexed(columnName, value);
    }

    @Override
    final public IndexedSet<V> filterIndexedSet(String columnName, Set<?> values) throws RemoteException {
        if(values==null || values.isEmpty()) return IndexedSet.emptyIndexedSet(getServiceName());
        return getSet().filterIndexedSet(columnName, values);
    }
}
