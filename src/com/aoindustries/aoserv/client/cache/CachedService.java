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
import com.aoindustries.table.IndexType;
import com.aoindustries.table.Table;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
    //final Class<K> keyClass;
    final ServiceName serviceName;
    final AOServServiceUtils.AnnotationTable<K,V> table;
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

    /**
     * The unique column objects are hashed on the method return value when first needed.
     */
    private final Map<String,Map<Object,V>> uniqueHashes = new HashMap<String,Map<Object,V>>();
    private final Map<String,Boolean> uniqueHashValids = new HashMap<String,Boolean>();

    /**
     * The indexed column objects are hashed on the method return value when first needed.
     */
    private final Map<String,Map<Object,Set<V>>> indexedHashes = new HashMap<String,Map<Object,Set<V>>>();
    private final Map<String,Boolean> indexHashValids = new HashMap<String,Boolean>();

    CachedService(CachedConnector connector, Class<K> keyClass, Class<V> valueClass, AOServService<?,?,K,V> wrapped) {
        this.connector = connector;
        //this.keyClass = keyClass;
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

    final public boolean isEmpty() throws RemoteException {
        return getSet().isEmpty();
    }

    final public int getSize() throws RemoteException {
        return getSet().size();
    }

    final public V get(K key) throws RemoteException {
        if(key==null) return null;
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

    final public V getUnique(String columnName, Object value) throws RemoteException {
        MethodColumn methodColumn = table.getColumn(columnName);
        IndexType indexType = methodColumn.getIndexType();
        if(indexType!=IndexType.PRIMARY_KEY && indexType!=IndexType.UNIQUE) throw new IllegalArgumentException("Column not primary key or unique: "+columnName);
        if(value==null) return null;
        Method method = methodColumn.getMethod();
        if(value.getClass()!=method.getReturnType()) throw new IllegalArgumentException("value class and return type mismatch: "+value.getClass().getName()+"!="+method.getReturnType().getName());
        synchronized(uniqueHashes) {
            Map<Object,V> uniqueHash = uniqueHashes.get(columnName);
            if(uniqueHash==null || Boolean.TRUE!=uniqueHashValids.get(columnName)) {
                Set<V> set = getSet();
                if(uniqueHash==null) uniqueHashes.put(columnName, uniqueHash = new HashMap<Object,V>(set.size()*4/3+1));
                else uniqueHash.clear();
                try {
                    for(V obj : set) {
                        Object columnValue = method.invoke(obj);
                        if(uniqueHash.put(columnValue, obj)!=null) throw new AssertionError("Duplicate value in unique column "+getServiceName()+"."+columnName+": "+columnValue);
                    }
                } catch(IllegalAccessException err) {
                    throw new RemoteException(err.getMessage(), err);
                } catch(InvocationTargetException err) {
                    throw new RemoteException(err.getMessage(), err);
                }
                uniqueHashValids.put(columnName, Boolean.TRUE);
            }
            return uniqueHash.get(value);
        }
    }

    final public Set<V> getIndexed(String columnName, Object value) throws RemoteException {
        MethodColumn methodColumn = table.getColumn(columnName);
        if(methodColumn.getIndexType()!=IndexType.INDEXED) throw new IllegalArgumentException("Column not indexed: "+columnName);
        if(value==null) return null;
        Method method = methodColumn.getMethod();
        if(value.getClass()!=method.getReturnType()) throw new IllegalArgumentException("value class and return type mismatch: "+value.getClass().getName()+"!="+method.getReturnType().getName());
        synchronized(indexedHashes) {
            Map<Object,Set<V>> indexedHash = indexedHashes.get(columnName);
            if(indexedHash==null || Boolean.TRUE!=indexHashValids.get(columnName)) {
                if(indexedHash==null) indexedHashes.put(columnName, indexedHash = new HashMap<Object,Set<V>>());
                else indexedHash.clear();
                try {
                    for(V obj : getSet()) {
                        Object columnValue = method.invoke(obj);
                        Set<V> results = indexedHash.get(columnValue);
                        if(results==null) indexedHash.put(columnValue, results = new HashSet<V>());
                        results.add(obj);
                    }
                } catch(IllegalAccessException err) {
                    throw new RemoteException(err.getMessage(), err);
                } catch(InvocationTargetException err) {
                    throw new RemoteException(err.getMessage(), err);
                }
                // Make each set unmodifiable
                for(Map.Entry<Object,Set<V>> entry : indexedHash.entrySet()) {
                    Set<V> set = entry.getValue();
                    entry.setValue(
                        set.size()==1
                        ? Collections.singleton(set.iterator().next())
                        : Collections.unmodifiableSet(set)
                    );
                }
                indexHashValids.put(columnName, Boolean.TRUE);
            }
            Set<V> results = indexedHash.get(value);
            if(results==null) return Collections.emptySet();
            return results;
        }
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
        synchronized(uniqueHashes) {
            for(Map.Entry<String,Boolean> entry : uniqueHashValids.entrySet()) {
                entry.setValue(Boolean.FALSE);
            }
        }
        synchronized(indexedHashes) {
            for(Map.Entry<String,Boolean> entry : indexHashValids.entrySet()) {
                entry.setValue(Boolean.FALSE);
            }
        }
    }
}
