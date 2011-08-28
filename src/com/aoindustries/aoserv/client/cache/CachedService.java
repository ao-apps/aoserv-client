/*
 * Copyright 2001-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.cache;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A <code>CachedService</code> stores all of the
 * available <code>AOServObject</code>s and performs
 * all subsequent data access locally.  The server
 * notifies the client when a table is updated, and
 * the caches are then invalidated.  Once invalidated,
 * the data is reloaded upon next use.
 *
 * @author  AO Industries, Inc.
 */
abstract class CachedService<K extends Comparable<K>, V extends AOServObject<K>> extends AbstractService<K,V> {

    final CachedConnector connector;
    final AOServService<K,V> wrapped;

    /**
     * The internal objects are stored in an unmodifiable set
     * for access to the entire table.
     */
    private final Object cachedSetLock = new Object();
    private IndexedSet<V> cachedSet;

    /**
     * The internal objects are hashed on the key when first needed.
     */
    private final Map<K,V> cachedHash = new HashMap<K,V>();
    private boolean cachedHashValid = false;

    CachedService(CachedConnector connector, Class<K> keyClass, Class<V> valueClass, AOServService<K,V> wrapped) {
        super(keyClass, valueClass);
        this.connector = connector;
        this.wrapped = wrapped;
    }

    @Override
    final public CachedConnector getConnector() {
        return connector;
    }

    @Override
    final public IndexedSet<V> getSet() throws RemoteException {
        synchronized(cachedSetLock) {
            if(cachedSet==null) cachedSet = AOServConnectorUtils.setConnector(valueClass, wrapped.getSet(), connector);
            return cachedSet;
        }
    }

    @Override
    final public boolean isEmpty() throws RemoteException {
        return getSet().isEmpty();
    }

    @Override
    final public int getSize() throws RemoteException {
        return getSet().size();
    }

    @Override
    final public V get(K key) throws RemoteException, NoSuchElementException {
        if(key==null) return null;
        V result;
        synchronized(cachedHash) {
            if(!cachedHashValid) {
                cachedHash.clear();
                for(V v : getSet()) {
                    K k = v.getKey();
                    if(cachedHash.put(k, v)!=null) throw new AssertionError("Duplicate key: "+k);
                }
                cachedHashValid = true;
            }
            result = cachedHash.get(key);
        }
        if(result==null) throw new NoSuchElementException("service="+getServiceName()+", key="+key);
        return result;
    }

    @Override
    final public V filterUnique(MethodColumn column, Object value) throws RemoteException {
        if(value==null) return null;
        IndexType indexType = column.getIndexType();
        if(indexType!=IndexType.PRIMARY_KEY && indexType!=IndexType.UNIQUE) throw new IllegalArgumentException("Column neither primary key nor unique: "+column);
        return getSet().filterUnique(column, value);
    }

    /**
     * The filtered set is based on the intersection of the values set and uniqueHash.keySet
     */
    @Override
    final public IndexedSet<V> filterUniqueSet(MethodColumn column, Set<?> values) throws RemoteException {
        if(values==null || values.isEmpty()) return IndexedSet.emptyIndexedSet(getServiceName());
        IndexType indexType = column.getIndexType();
        if(indexType!=IndexType.PRIMARY_KEY && indexType!=IndexType.UNIQUE) throw new IllegalArgumentException("Column neither primary key nor unique: "+column);
        return getSet().filterUniqueSet(column, values);
    }

    @Override
    final public IndexedSet<V> filterIndexed(MethodColumn column, Object value) throws RemoteException {
        if(value==null) return IndexedSet.emptyIndexedSet(getServiceName());
        return getSet().filterIndexed(column, value);
    }

    @Override
    final public IndexedSet<V> filterIndexedSet(MethodColumn column, Set<?> values) throws RemoteException {
        if(values==null || values.isEmpty()) return IndexedSet.emptyIndexedSet(getServiceName());
        return getSet().filterIndexedSet(column, values);
    }

    /**
     * Clears the cache, freeing up memory.  The data will be reloaded upon next use.
     * TODO: Clear in asynchronous mode
     */
    void clearCache() {
        synchronized(cachedSetLock) {
            cachedSet = null;
        }
        synchronized(cachedHash) {
            cachedHashValid = false;
            cachedHash.clear();
        }
    }
}
