package com.aoindustries.aoserv.client.noswing;

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
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * @see  NoSwingConnectorFactory
 *
 * @author  AO Industries, Inc.
 */
abstract class NoSwingService<K extends Comparable<K>,V extends AOServObject<K,V>> implements AOServService<NoSwingConnector,NoSwingConnectorFactory,K,V> {

    final NoSwingConnector connector;
    final Table<MethodColumn,V> table;
    final Map<K,V> map;
    final AOServService<?,?,K,V> wrapped;

    NoSwingService(NoSwingConnector connector, Class<K> keyClass, Class<V> valueClass, AOServService<?,?,K,V> wrapped) {
        this.connector = connector;
        this.wrapped = wrapped;
        table = new AOServServiceUtils.AnnotationTable<K,V>(this, valueClass);
        map = new AOServServiceUtils.ServiceMap<K,V>(this, keyClass, valueClass);
    }

    @Override
    final public String toString() {
        try {
            return getServiceName().toString(connector.getLocale());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }

    final public NoSwingConnector getConnector() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return connector;
    }

    final public boolean isAoServObjectServiceSettable() {
        return true;
    }

    final public Set<V> getSet() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return AOServServiceUtils.setServices(wrapped.getSet(), this);
    }

    final public SortedSet<V> getSortedSet() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return AOServServiceUtils.setServices(wrapped.getSortedSet(), this);
    }

    final public ServiceName getServiceName() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return wrapped.getServiceName();
    }

    final public Table<MethodColumn,V> getTable() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return table;
    }

    final public Map<K,V> getMap() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return map;
    }

    final public boolean isEmpty() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return wrapped.isEmpty();
    }

    final public int getSize() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return wrapped.getSize();
    }

    final public V get(K key) throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return AOServServiceUtils.setService(wrapped.get(key), this);
    }

    final public V getUnique(String columnName, Object value) throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return AOServServiceUtils.setService(wrapped.getUnique(columnName, value), this);
    }

    final public Set<V> getIndexed(String columnName, Object value) throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return AOServServiceUtils.setServices(wrapped.getIndexed(columnName, value), this);
    }
}
