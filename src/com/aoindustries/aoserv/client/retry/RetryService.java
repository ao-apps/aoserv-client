package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServObject;
import com.aoindustries.aoserv.client.AOServService;
import com.aoindustries.aoserv.client.AOServServiceUtils;
import com.aoindustries.aoserv.client.IndexedSet;
import com.aoindustries.aoserv.client.MethodColumn;
import com.aoindustries.aoserv.client.ServiceName;
import com.aoindustries.table.Table;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * @author  AO Industries, Inc.
 */
abstract class RetryService<K extends Comparable<K>,V extends AOServObject<K,V>> implements AOServService<RetryConnector,RetryConnectorFactory,K,V> {

    final RetryConnector connector;
    final ServiceName serviceName;
    final Table<MethodColumn,V> table;
    final Map<K,V> map;
    AOServService<?,?,K,V> wrapped;

    RetryService(RetryConnector connector, Class<K> keyClass, Class<V> valueClass) {
        this.connector = connector;
        serviceName = AOServServiceUtils.findServiceNameByAnnotation(getClass());
        table = new AOServServiceUtils.AnnotationTable<K,V>(this, valueClass);
        map = new AOServServiceUtils.ServiceMap<K,V>(this, keyClass, valueClass);
    }

    @Override
    final public String toString() {
        return getServiceName().toString(connector.getLocale());
    }

    AOServService<?,?,K,V> getWrapped() throws RemoteException {
        synchronized(connector.connectionLock) {
            if(wrapped==null) {
                try {
                    connector.connect();
                } catch(Exception err) {
                    throw new RemoteException(err.getMessage(), err);
                }
            }
            return wrapped;
        }
    }

    final public RetryConnector getConnector() {
        return connector;
    }

    final public boolean isAoServObjectServiceSettable() {
        return true;
    }

    final public IndexedSet<V> getSet() throws RemoteException {
        return connector.retry(
            new Callable<IndexedSet<V>>() {
                public IndexedSet<V> call() throws RemoteException {
                    return AOServServiceUtils.setServices(getWrapped().getSet(), RetryService.this);
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
        return connector.retry(
            new Callable<Boolean>() {
                public Boolean call() throws RemoteException {
                    return getWrapped().isEmpty();
                }
            }
        );
    }

    final public int getSize() throws RemoteException {
        return connector.retry(
            new Callable<Integer>() {
                public Integer call() throws RemoteException {
                    return getWrapped().getSize();
                }
            }
        );
    }

    final public V get(final K key) throws RemoteException {
        return connector.retry(
            new Callable<V>() {
                public V call() throws RemoteException {
                    return AOServServiceUtils.setService(getWrapped().get(key), RetryService.this);
                }
            }
        );
    }

    final public V filterUnique(final String columnName, final Object value) throws RemoteException {
        return connector.retry(
            new Callable<V>() {
                public V call() throws RemoteException {
                    return AOServServiceUtils.setService(getWrapped().filterUnique(columnName, value), RetryService.this);
                }
            }
        );
    }

    final public IndexedSet<V> filterUniqueSet(final String columnName, final Set<?> values) throws RemoteException {
        return connector.retry(
            new Callable<IndexedSet<V>>() {
                public IndexedSet<V> call() throws RemoteException {
                    return AOServServiceUtils.setServices(getWrapped().filterUniqueSet(columnName, values), RetryService.this);
                }
            }
        );
    }

    final public IndexedSet<V> filterIndexed(final String columnName, final Object value) throws RemoteException {
        return connector.retry(
            new Callable<IndexedSet<V>>() {
                public IndexedSet<V> call() throws RemoteException {
                    return AOServServiceUtils.setServices(getWrapped().filterIndexed(columnName, value), RetryService.this);
                }
            }
        );
    }
}
