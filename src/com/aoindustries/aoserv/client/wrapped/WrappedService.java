package com.aoindustries.aoserv.client.wrapped;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServObject;
import com.aoindustries.aoserv.client.AOServService;
import com.aoindustries.aoserv.client.AOServServiceUtils;
import com.aoindustries.aoserv.client.IndexedSet;
import com.aoindustries.aoserv.client.MethodColumn;
import com.aoindustries.aoserv.client.ServiceName;
import com.aoindustries.security.LoginException;
import com.aoindustries.table.Table;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * @author  AO Industries, Inc.
 */
abstract public class WrappedService<C extends WrappedConnector<C,F>, F extends WrappedConnectorFactory<C,F>, K extends Comparable<K>,V extends AOServObject<K,V>> implements AOServService<C,F,K,V> {

    final C connector;
    final ServiceName serviceName;
    final Table<MethodColumn,V> table;
    final Map<K,V> map;
    AOServService<?,?,K,V> wrapped;

    protected WrappedService(C connector, Class<K> keyClass, Class<V> valueClass) {
        this.connector = connector;
        serviceName = AOServServiceUtils.findServiceNameByAnnotation(getClass());
        table = new AOServServiceUtils.AnnotationTable<K,V>(this, valueClass);
        map = new AOServServiceUtils.ServiceMap<K,V>(this, keyClass, valueClass);
    }

    @Override
    final public String toString() {
        return getServiceName().toString(connector.getLocale());
    }

    /**
     * Gets the wrapped service, reconnecting if necessary.
     */
    protected AOServService<?,?,K,V> getWrapped() throws RemoteException {
        synchronized(connector.connectionLock) {
            if(wrapped==null) {
                try {
                    connector.connect();
                } catch(LoginException err) {
                    throw new RemoteException(err.getMessage(), err);
                }
            }
            return wrapped;
        }
    }

    final public C getConnector() {
        return connector;
    }

    final public boolean isAoServObjectServiceSettable() throws RemoteException {
        return connector.isAoServObjectServiceSettable();
    }

    final public IndexedSet<V> getSet() throws RemoteException {
        return connector.call(
            new Callable<IndexedSet<V>>() {
                public IndexedSet<V> call() throws RemoteException {
                    return AOServServiceUtils.setServices(getWrapped().getSet(), WrappedService.this);
                }
            }
        );
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
        return connector.call(
            new Callable<Boolean>() {
                public Boolean call() throws RemoteException {
                    return getWrapped().isEmpty();
                }
            }
        );
    }

    final public int getSize() throws RemoteException {
        return connector.call(
            new Callable<Integer>() {
                public Integer call() throws RemoteException {
                    return getWrapped().getSize();
                }
            }
        );
    }

    final public V get(final K key) throws RemoteException, NoSuchElementException {
        return connector.call(
            new Callable<V>() {
                public V call() throws RemoteException, NoSuchElementException {
                    return AOServServiceUtils.setService(getWrapped().get(key), WrappedService.this);
                }
            }
        );
    }

    final public V filterUnique(final String columnName, final Object value) throws RemoteException {
        return connector.call(
            new Callable<V>() {
                public V call() throws RemoteException {
                    return AOServServiceUtils.setService(getWrapped().filterUnique(columnName, value), WrappedService.this);
                }
            }
        );
    }

    final public IndexedSet<V> filterUniqueSet(final String columnName, final Set<?> values) throws RemoteException {
        return connector.call(
            new Callable<IndexedSet<V>>() {
                public IndexedSet<V> call() throws RemoteException {
                    return AOServServiceUtils.setServices(getWrapped().filterUniqueSet(columnName, values), WrappedService.this);
                }
            }
        );
    }

    final public IndexedSet<V> filterIndexed(final String columnName, final Object value) throws RemoteException {
        return connector.call(
            new Callable<IndexedSet<V>>() {
                public IndexedSet<V> call() throws RemoteException {
                    return AOServServiceUtils.setServices(getWrapped().filterIndexed(columnName, value), WrappedService.this);
                }
            }
        );
    }

    final public IndexedSet<V> filterIndexedSet(final String columnName, final Set<?> values) throws RemoteException {
        return connector.call(
            new Callable<IndexedSet<V>>() {
                public IndexedSet<V> call() throws RemoteException {
                    return AOServServiceUtils.setServices(getWrapped().filterIndexedSet(columnName, values), WrappedService.this);
                }
            }
        );
    }
}
