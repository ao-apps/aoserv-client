package com.aoindustries.aoserv.client.rmi;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServObject;
import com.aoindustries.aoserv.client.AOServService;
import com.aoindustries.aoserv.client.AbstractService;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * @author  AO Industries, Inc.
 */
abstract class RmiService<K extends Comparable<K>,V extends AOServObject<K,V>> extends AbstractService<RmiConnector,RmiConnectorFactory,K,V> {

    AOServService<?,?,K,V> wrapped;

    RmiService(RmiConnector connector, Class<K> keyClass, Class<V> valueClass) {
        super(connector, keyClass, valueClass);
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

    static interface RetryCallable<T> {
        T call() throws RemoteException;
    }

    <T> T retry(RetryCallable<T> callable) throws RemoteException {
        int attempt = 1;
        while(!Thread.interrupted()) {
            try {
                return callable.call();
            } catch(RuntimeException err) {
                connector.disconnectIfNeeded(err);
                if(Thread.interrupted() || attempt>=RmiUtils.RETRY_ATTEMPTS || RmiUtils.isImmediateFail(err)) throw err;
            } catch(RemoteException err) {
                connector.disconnectIfNeeded(err);
                if(Thread.interrupted() || attempt>=RmiUtils.RETRY_ATTEMPTS || RmiUtils.isImmediateFail(err)) throw err;
            }
            try {
                Thread.sleep(RmiUtils.retryAttemptDelays[attempt-1]);
            } catch(InterruptedException err) {
                throw new RemoteException(err.getMessage(), err);
            }
            attempt++;
        }
        throw new RemoteException("interrupted", new InterruptedException("interrupted"));
    }

    final public Set<V> getSet() throws RemoteException {
        return retry(
            new RetryCallable<Set<V>>() {
                public Set<V> call() throws RemoteException {
                    return setServices(getWrapped().getSet());
                }
            }
        );
    }

    @Override
    final public V get(final K key) throws RemoteException {
        return retry(
            new RetryCallable<V>() {
                public V call() throws RemoteException {
                    return setService(getWrapped().get(key));
                }
            }
        );
    }

    @Override
    final public boolean isEmpty() throws RemoteException {
        return retry(
            new RetryCallable<Boolean>() {
                public Boolean call() throws RemoteException {
                    return getWrapped().isEmpty();
                }
            }
        );
    }

    @Override
    final public int size() throws RemoteException {
        return retry(
            new RetryCallable<Integer>() {
                public Integer call() throws RemoteException {
                    return getWrapped().size();
                }
            }
        );
    }
}
