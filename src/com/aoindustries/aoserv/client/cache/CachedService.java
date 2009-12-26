package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServObject;
import com.aoindustries.aoserv.client.AOServService;
import com.aoindustries.aoserv.client.AOServServiceUtils;
import com.aoindustries.aoserv.client.MethodColumn;
import com.aoindustries.aoserv.client.ServiceName;
import com.aoindustries.table.Table;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
abstract class CachedService<K extends Comparable<K>,V extends AOServObject<K,V>> implements AOServService<CachedConnector,CachedConnectorFactory,K,V> {

    final CachedConnector connector;
    final ServiceName serviceName;
    final Table<MethodColumn,V> table;
    final Map<K,V> map;
    final AOServService<?,?,K,V> wrapped;

    /**
     * The internal objects are stored in an unmodifiable set
     * for access to the entire table.
     */
    private final Object cachedSetLock = new Object();
    private Set<V> cachedSet;

    /**
     * The internal objects are stored in an unmodifiable set
     * for access to the entire table.
     */
    private final Object cachedSortedSetLock = new Object();
    private SortedSet<V> cachedSortedSet;

    /**
     * The internal objects are hashed on the key when first needed.
     */
    private final Map<K,V> cachedHash = new HashMap<K,V>();
    private boolean cachedHashValid = false;

    CachedService(CachedConnector connector, Class<K> keyClass, Class<V> valueClass, AOServService<?,?,K,V> wrapped) {
        this.connector = connector;
        this.wrapped = wrapped;
        serviceName = AOServServiceUtils.findServiceNameByAnnotation(getClass());
        table = new AOServServiceUtils.AnnotationTable<K,V>(this, valueClass);
        map = new AOServServiceUtils.ServiceMap<K,V>(this, keyClass, valueClass);
    }

    @Override
    final public String toString() {
        return getServiceName().toString(connector.getLocale());
    }

    final public CachedConnector getConnector() {
        return connector;
    }

    final public Set<V> getSet() throws RemoteException {
        synchronized(cachedSetLock) {
            if(cachedSet==null) cachedSet = AOServServiceUtils.setServices(wrapped.getSet(), this);
            return cachedSet;
        }
    }

    /**
     * Sorting is performed locally using <code>TreeSet</code>.
     */
    final public SortedSet<V> getSortedSet() throws RemoteException {
        synchronized(cachedSortedSetLock) {
            if(cachedSortedSet==null) cachedSortedSet = Collections.unmodifiableSortedSet(new TreeSet<V>(getSet()));
            return cachedSortedSet;
        }
    }

    final public ServiceName getServiceName() {
        return serviceName;
    }

    final public Table<MethodColumn,V> getTable() {
        return table;
    }

    final public Map<K,V> getMap() {
        return map;
    }

    final public V get(K key) throws RemoteException {
        synchronized(cachedHash) {
            if(!cachedHashValid) {
                cachedHash.clear();
                for(V v : getSet()) {
                    K k = v.getKey();
                    if(cachedHash.put(k, v)!=null) throw new AssertionError("Duplicate key: "+k);
                }
                cachedHashValid = true;
            }
            return cachedHash.get(key);
        }
    }

    final public boolean isEmpty() throws RemoteException {
        return getSet().isEmpty();
    }

    final public int getSize() throws RemoteException {
        return getSet().size();
    }

    /**
     * Clears the cache, freeing up memory.  The data will be reloaded upon next use.
     * TODO: Clear when table invalidated
     */
    //@Override
    void clearCache() {
        //super.clearCache();
        synchronized(cachedSetLock) {
            cachedSet = null;
        }
        synchronized(cachedSortedSetLock) {
            cachedSortedSet = null;
        }
        synchronized(cachedHash) {
            cachedHashValid = false;
            cachedHash.clear();
        }
    }
}
