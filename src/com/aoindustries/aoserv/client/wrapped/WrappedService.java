/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.wrapped;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.security.LoginException;
import java.rmi.RemoteException;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * @author  AO Industries, Inc.
 */
abstract public class WrappedService<
    C extends WrappedConnector<C,F>,
    F extends WrappedConnectorFactory<C,F>,
    K extends Comparable<K>,
    V extends AOServObject<K>
> extends AbstractService<K,V> {

    final WrappedConnector<C,F> connector;
    AOServService<K,V> wrapped;

    protected WrappedService(WrappedConnector<C,F> connector, Class<K> keyClass, Class<V> valueClass) {
        super(keyClass, valueClass);
        this.connector = connector;
    }

    /**
     * Gets the wrapped service, reconnecting if necessary.
     */
    @SuppressWarnings("unchecked")
    protected AOServService<K,V> getWrapped() throws RemoteException {
        synchronized(connector.connectionLock) {
            if(wrapped==null) {
                try {
                    wrapped = (AOServService<K,V>)connector.getWrapped().getServices().get(getServiceName());
                } catch(LoginException err) {
                    throw new RemoteException(err.getMessage(), err);
                }
            }
            return wrapped;
        }
    }

    @Override
    final public WrappedConnector<C,F> getConnector() {
        return connector;
    }

    @Override
    final public IndexedSet<V> getSet() throws RemoteException {
        return connector.call(
            new Callable<IndexedSet<V>>() {
                @Override
                public IndexedSet<V> call() throws RemoteException {
                    return AOServConnectorUtils.setConnector(getWrapped().getSet(), connector);
                }
            }
        );
    }

    @Override
    final public boolean isEmpty() throws RemoteException {
        return connector.call(
            new Callable<Boolean>() {
                @Override
                public Boolean call() throws RemoteException {
                    return getWrapped().isEmpty();
                }
            }
        );
    }

    @Override
    final public int getSize() throws RemoteException {
        return connector.call(
            new Callable<Integer>() {
                @Override
                public Integer call() throws RemoteException {
                    return getWrapped().getSize();
                }
            }
        );
    }

    @Override
    final public V get(final K key) throws RemoteException, NoSuchElementException {
        return connector.call(
            new Callable<V>() {
                @Override
                public V call() throws RemoteException, NoSuchElementException {
                    return AOServConnectorUtils.setConnector(getWrapped().get(key), connector);
                }
            }
        );
    }

    @Override
    final public V filterUnique(final String columnName, final Object value) throws RemoteException {
        return connector.call(
            new Callable<V>() {
                @Override
                public V call() throws RemoteException {
                    return AOServConnectorUtils.setConnector(getWrapped().filterUnique(columnName, value), connector);
                }
            }
        );
    }

    @Override
    final public IndexedSet<V> filterUniqueSet(final String columnName, final Set<?> values) throws RemoteException {
        return connector.call(
            new Callable<IndexedSet<V>>() {
                @Override
                public IndexedSet<V> call() throws RemoteException {
                    return AOServConnectorUtils.setConnector(getWrapped().filterUniqueSet(columnName, values), connector);
                }
            }
        );
    }

    @Override
    final public IndexedSet<V> filterIndexed(final String columnName, final Object value) throws RemoteException {
        return connector.call(
            new Callable<IndexedSet<V>>() {
                @Override
                public IndexedSet<V> call() throws RemoteException {
                    return AOServConnectorUtils.setConnector(getWrapped().filterIndexed(columnName, value), connector);
                }
            }
        );
    }

    @Override
    final public IndexedSet<V> filterIndexedSet(final String columnName, final Set<?> values) throws RemoteException {
        return connector.call(
            new Callable<IndexedSet<V>>() {
                @Override
                public IndexedSet<V> call() throws RemoteException {
                    return AOServConnectorUtils.setConnector(getWrapped().filterIndexedSet(columnName, values), connector);
                }
            }
        );
    }
}
