package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.Table;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author  AO Industries, Inc.
 */
abstract public class AbstractService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>, K extends Comparable<K>,V extends AOServObject<K,V>> implements AOServService<C,F,K,V> {

    protected final C connector;
    protected final ServiceName serviceName;
    protected final Table<V> table;
    protected final Map<K,V> map;

    protected AbstractService(C connector, Class<K> keyClass, Class<V> valueClass) {
        this.connector = connector;
        serviceName = AOServServiceUtils.findServiceNameByAnnotation(getClass());
        table = new AOServServiceUtils.AnnotationTable<K,V>(this, valueClass);
        map = new AOServServiceUtils.ServiceMap<K,V>(this, keyClass, valueClass);
    }

    @Override
    final public String toString() {
        return getServiceName().getDisplay();
    }

    final public C getConnector() {
        return connector;
    }

    abstract public Set<V> getSet() throws RemoteException;

    public SortedSet<V> getSortedSet() throws RemoteException {
        Set<V> set = getSet();
        if(set instanceof SortedSet) return (SortedSet<V>)set;
        return Collections.unmodifiableSortedSet(new TreeSet<V>(set));
    }

    final public ServiceName getServiceName() {
        return serviceName;
    }

    final public Table<V> getTable() {
        return table;
    }

    final public Map<K,V> getMap() {
        return map;
    }

    /**
     * This default implemention iterates through all of the objects returned by getSet
     * and looks for the first one with a matching key.  Subclasses are encouraged to
     * provide more efficient implementations.
     */
    public V get(K key) throws RemoteException {
        for(V obj : getSet()) if(obj.getKey().equals(key)) return obj;
        return null;
    }

    /**
     * This default implementation calls size to determine if the service is empty.
     * Subclasses are encouraged to provide more efficient implementations.
     */
    public boolean isEmpty() throws RemoteException {
        return size()==0;
    }

    /**
     * This default implementation calls getSet to determine if the size of the service.
     * Subclasses are encouraged to provide more efficient implementations.
     */
    public int size() throws RemoteException {
        return getSet().size();
    }

    /**
     * Calls size().
     */
    final public int getSize() throws RemoteException {
        return size();
    }
}
