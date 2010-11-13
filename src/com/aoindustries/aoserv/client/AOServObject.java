/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.Row;
import com.aoindustries.util.Internable;
import com.aoindustries.util.UnionSet;
import com.aoindustries.util.WrappedException;
import com.aoindustries.util.i18n.Money;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Set;

/**
 * An <code>AOServObject</code> is the lowest level object
 * for all data in the system.  Each <code>AOServObject</code>
 * belongs to an <code>AOServService</code>, each service
 * belongs to an <code>AOServConnector</code>, and each
 * connector belongs to an <code>AOServConnectorFactory</code>.
 *
 * @author  AO Industries, Inc.
 *
 * @see  AOServService
 */
abstract public class AOServObject<
    K extends Comparable<K>,
    T extends AOServObject<K,T> & Comparable<T> & DtoFactory<?>
> implements Row, Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    /**
     * Value used when data has been filtered.
     */
    public static final String FILTERED = "*";

    /**
     * null-safe intern.
     */
    protected static <V extends Internable<V>> V intern(V value) {
        return value==null ? null : value.intern();
    }

    /**
     * null-safe intern.
     */
    protected static String intern(String value) {
        return value==null ? null : value.intern();
    }

    /**
     * null-safe getDto.
     */
    protected static <B> B getDto(DtoFactory<B> dtoFactory) {
        return dtoFactory==null ? null : dtoFactory.getDto();
    }

    /**
     * null-safe getDto for Money.
     */
    protected static com.aoindustries.aoserv.client.dto.Money getDto(Money money) {
        return money==null ? null : new com.aoindustries.aoserv.client.dto.Money(money.getCurrency().getCurrencyCode(), money.getValue());
    }

    private volatile transient AOServService<?,?,K,T> service;

    protected AOServObject(AOServService<?,?,K,T> service) {
        this.service = service;
    }

    /**
     * All AOServObjects are cloneable.
     */
    @Override
    @SuppressWarnings("unchecked")
    final public T clone() {
        try {
            return (T)super.clone();
        } catch(CloneNotSupportedException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Gets the service that this object belongs to.
     */
    final public AOServService<?,?,K,T> getService() {
        return service;
    }

    /**
     * Returns a (possibly new) instance of this object set to a different service.
     * <p>
     * The <code>service</code> field is marked <code>transient</code>, and thus
     * deserialized objects will initially have a <code>null</code> service
     * reference.  The code that deserializes the objects should call this
     * setService method on all objects received.
     * </p>
     * <p>
     * Also, caching layers should call setService on all objects in order to make
     * subsequent method invocations use the caches.  This will cause additional
     * copying within the cache layers, but the reduction of round-trips to the
     * server should payoff.
     * </p>
     *
     * @return  if the service field is currently <code>null</code>, sets the field and
     *          returns this object.  Next, if the service is equal to the provided service
     *          returns this object.  Next, if the current service returns <code>true</code> for
     *          <code>isAoServObjectServiceSettable</code>, updates and returns this object.
     *          Otherwise, returns a clone with the service field updated.
     */
    @SuppressWarnings("unchecked")
    final public T setService(AOServService<?,?,K,T> service) throws RemoteException {
        if(this.service==null) {
            this.service = service;
            return (T)this;
        } else if(this.service==service) {
            return (T)this;
        } else if(this.service.isAoServObjectServiceSettable()) {
            this.service = service;
            return (T)this;
        } else {
            T newObj = clone();
            ((AOServObject)newObj).service = service;
            return newObj;
        }
    }

    /**
     * Every object's equality is based on being of the same class and having the same key value.
     * This default implementation checks for class compatibility and calls equals(T).
     */
    @Override
    public boolean equals(Object o) {
        if(o==null || getClass()!=o.getClass()) return false;
        @SuppressWarnings("unchecked") T other = (T)o;
        return getKey().equals(other.getKey());
    }

    /**
     * Gets the key value for this object.
     */
    public abstract K getKey();

    /**
     * By default sorts by key value.
     */
    public int compareTo(T other) {
        return getKey().compareTo(other.getKey());
    }

    /**
     * The default hashcode value is the hash code of the key value.
     */
    @Override
    public int hashCode() {
        return getKey().hashCode();
    }

    /**
     * Gets a string representation of this object.
     *
     * @see  #toStringImpl()
     */
    @Override
    final public String toString() {
        try {
            return toStringImpl();
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * The default string representation is that of the key value.
     */
    String toStringImpl() throws RemoteException {
        return getKey().toString();
    }

    /**
     * Gets an unmodifiable set of objects this object directly depends on.
     * This should result in a directed acyclic graph - there should never be any loops in the graph.
     * This acyclic graph, however, should be an exact mirror of the acyclic graph obtained from <code>getDependentObjects</code>.
     * By default, there are no dependencies.
     *
     * @see #getDependentObjects() for the opposite direction
     */
    final public Set<? extends AOServObject> getDependencies() throws RemoteException {
        UnionSet<AOServObject> unionSet = addDependencies(null);
        if(unionSet==null || unionSet.isEmpty()) return Collections.emptySet();
        else return Collections.unmodifiableSet(unionSet);
    }

    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        return unionSet;
    }

    /**
     * Gets the set of objects directly dependent upon this object.
     * This should result in a directed acyclic graph - there should never be any loops in the graph.
     * This acyclic graph, however, should be an exact mirror of the acyclic graph obtained from <code>getDependencies</code>.
     * By default, there are no dependent objects.
     *
     * @see #getDependencies() for the opposite direction
     */
    final public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        UnionSet<AOServObject> unionSet = addDependentObjects(null);
        if(unionSet==null || unionSet.isEmpty()) return Collections.emptySet();
        else return Collections.unmodifiableSet(unionSet);
    }

    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        return unionSet;
    }

    /**
     * Gets value of the column with the provided index, by using the SchemaColumn annotation.
     */
    @Override
    final public Object getColumn(int index) {
        try {
            return AOServObjectUtils.getMethodColumns(getClass()).get(index).getMethod().invoke(this);
        } catch(IllegalAccessException err) {
            throw new WrappedException(err);
        } catch(InvocationTargetException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Gets value of the column with the provided name, by using the SchemaColumn annotation.
     */
    @Override
    final public Object getColumn(String name) {
        try {
            return AOServObjectUtils.getMethodColumnMap(getClass()).get(name).getMethod().invoke(this);
        } catch(IllegalAccessException err) {
            throw new WrappedException(err);
        } catch(InvocationTargetException err) {
            throw new WrappedException(err);
        }
    }

    /* TODO
    final public int compareTo(AOServConnector<?,?> conn, AOServObject other, SQLExpression[] sortExpressions, boolean[] sortOrders) throws IllegalArgumentException, SQLException, UnknownHostException, IOException {
        int len=sortExpressions.length;
        for(int c=0;c<len;c++) {
            SQLExpression expr=sortExpressions[c];
            SchemaType type=expr.getType();
            int diff=type.compareTo(
                expr.getValue(conn, this),
                expr.getValue(conn, other)
            );
            if(diff!=0) return sortOrders[c]?diff:-diff;
        }
        return 0;
    }

    final public int compareTo(AOServConnector<?,?> conn, Comparable value, SQLExpression[] sortExpressions, boolean[] sortOrders) throws IllegalArgumentException, SQLException, UnknownHostException, IOException {
        int len=sortExpressions.length;
        for(int c=0;c<len;c++) {
            SQLExpression expr=sortExpressions[c];
            SchemaType type=expr.getType();
            int diff=type.compareTo(
                expr.getValue(conn, this),
                value
            );
            if(diff!=0) return sortOrders[c]?diff:-diff;
        }
        return 0;
    }

    final public int compareTo(AOServConnector<?,?> conn, Object[] OA, SQLExpression[] sortExpressions, boolean[] sortOrders) throws IllegalArgumentException, SQLException, UnknownHostException, IOException {
        int len=sortExpressions.length;
        if(len!=OA.length) throw new IllegalArgumentException("Array length mismatch when comparing AOServObject to Object[]: sortExpressions.length="+len+", OA.length="+OA.length);

        for(int c=0;c<len;c++) {
            SQLExpression expr=sortExpressions[c];
            SchemaType type=expr.getType();
            int diff=type.compareTo(
                expr.getValue(conn, this),
                OA[c]
            );
            if(diff!=0) return sortOrders[c]?diff:-diff;
        }
        return 0;
    }
     */
}
