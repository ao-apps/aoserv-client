package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.Column;
import com.aoindustries.table.Row;
import com.aoindustries.util.i18n.LocalizedToString;
import com.aoindustries.util.WrappedException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
abstract public class AOServObject<K extends Comparable<K>,T extends AOServObject<K,T>> implements Row, Serializable, LocalizedToString, Comparable<T>, Cloneable {

    private static final long serialVersionUID = 1L;

    private static final Collator collator = Collator.getInstance(Locale.ENGLISH);
    static int compareIgnoreCaseConsistentWithEquals(String S1, String S2) {
        int diff = collator.compare(S1, S2);
        if(diff!=0) return diff;
        return S1.compareTo(S2);
    }

    static int compare(int i1, int i2) {
        return i1<i2 ? -1 : i1==i2 ? 0 : 1;
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
     *          returns this object.  Otherwise, returns a clone with the service field updated.
     */
    @SuppressWarnings("unchecked")
    final public T setService(AOServService<?,?,K,T> service) {
        if(this.service==null) {
            this.service = service;
            return (T)this;
        } else if(this.service==service) {
            return (T)this;
        } else {
            T newObj = clone();
            newObj.service = service;
            return newObj;
        }
    }

    /**
     * Every object's equality is based on being of the same class and having the same key value.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if(o==null || getClass()!=o.getClass()) return false;
        return getKey().equals(((T)o).getKey());
    }

    /**
     * Gets the key value for this object.
     */
    public abstract K getKey();

    final public int compareTo(T other) {
        try {
            return compareToImpl(other);
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * By default sortes by key value, if the key is <code>Comparable</code>,
     * otherwise throws exception.
     *
     * @throws  ClassCastException if either key is not comparable.
     */
    protected int compareToImpl(T other) throws RemoteException {
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
     * Gets a string representation of this object in the connector's current locale.
     *
     * @see  #toString(java.util.Locale)
     */
    @Override
    final public String toString() {
        try {
            return toString(service.getConnector().getLocale());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Gets a string representation of this object in the provided locale.
     *
     * @see  #toString()
     */
    final public String toString(Locale userLocale) {
        try {
            return toStringImpl(userLocale);
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * The default string representation is that of the key value.
     */
    String toStringImpl(Locale userLocale) throws RemoteException {
        K key=getKey();
        if(key instanceof LocalizedToString) return ((LocalizedToString)key).toString(userLocale);
        return key.toString();
    }

    /**
     * Returns an unmodifiable set of the provided objects, not including any null values.
     */
    static Set<? extends AOServObject> createDependencySet() {
        return Collections.emptySet();
    }

    /**
     * Returns an unmodifiable set of the provided objects, not including any null values.
     */
    static Set<? extends AOServObject> createDependencySet(AOServObject obj) {
        if(obj==null) return Collections.emptySet();
        return Collections.singleton(obj);
    }

    /**
     * Returns an unmodifiable set of the provided objects, not including any null values.
     * It is assumed that the set passed-in is unmodifiable and it will be returned directly if
     * it contains no null values.
     */
    static Set<? extends AOServObject> createDependencySet(Set<? extends AOServObject> objs) {
        boolean hasNull = false;
        for(AOServObject obj : objs) {
            if(obj==null) {
                hasNull = true;
                break;
            }
        }
        if(!hasNull) return objs;
        Set<AOServObject> set = new HashSet<AOServObject>(objs.size()*4/3+1);
        for(AOServObject obj : objs) {
            if(obj!=null) set.add(obj);
        }
        return AOServServiceUtils.unmodifiableSet(set);
    }

    /**
     * Returns an unmodifiable set of the provided objects, not including any null values.
     */
    static Set<? extends AOServObject> createDependencySet(Set<? extends AOServObject>... objss) {
        int totalSize = 0;
        for(Set<? extends AOServObject> objs : objss) totalSize+=objs.size();
        Set<AOServObject> set = new HashSet<AOServObject>(totalSize*4/3+1);
        for(Set<? extends AOServObject> objs : objss) {
            for(AOServObject obj : objs) {
                if(obj!=null) set.add(obj);
            }
        }
        if(set.size()==0) return Collections.emptySet();
        return AOServServiceUtils.unmodifiableSet(set);
    }

    /**
     * Returns an unmodifiable set of the provided objects, not including any null values.
     */
    static Set<? extends AOServObject> createDependencySet(AOServObject... objs) {
        Set<AOServObject> set = new HashSet<AOServObject>(objs.length*4/3+1);
        for(AOServObject obj : objs) {
            if(obj!=null) set.add(obj);
        }
        if(set.size()==0) return Collections.emptySet();
        return AOServServiceUtils.unmodifiableSet(set);
    }

    /**
     * Gets an unmodifiable set of objects this object directly depends on.
     * This should result in a directed acyclic graph - there should never be any loops in the graph.
     * This acyclic graph, however, should be an exact mirror of the acyclic graph obtained from <code>getDependentObjects</code>.
     * By default, there are no dependencies.
     *
     * @see #getDependentObjects() for the opposite direction
     */
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return createDependencySet(
        );
    }

    /**
     * Gets the set of objects directly dependent upon this object.
     * This should result in a directed acyclic graph - there should never be any loops in the graph.
     * This acyclic graph, however, should be an exact mirror of the acyclic graph obtained from <code>getDependencies</code>.
     * By default, there are no dependent objects.
     *
     * @see #getDependencies() for the opposite direction
     */
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return createDependencySet(
        );
    }

    final static class MethodColumn extends Column {

        private final Method method;

        MethodColumn(String columnName, boolean unique, Method method) {
            super(columnName, unique);
            this.method = method;
        }

        Method getMethod() {
            return method;
        }
    }

    private static final ConcurrentMap<Class<? extends AOServObject>,List<MethodColumn>> columns = new ConcurrentHashMap<Class<? extends AOServObject>, List<MethodColumn>>();

    /**
     * Gets the columns for the provided class, in column index order.
     * Also ensures that no column name is duplicated.
     * Also ensures that there is no gap in column index numbers.
     */
    static List<MethodColumn> getMethodColumns(Class<? extends AOServObject> clazz) {
        List<MethodColumn> methodColumns = columns.get(clazz);
        if(methodColumns==null) {
            ArrayList<MethodColumn> newColumns = new ArrayList<MethodColumn>();
            Set<String> columnNames = new HashSet<String>();
            for(Method method : clazz.getMethods()) {
                SchemaColumn schemaColumn = method.getAnnotation(SchemaColumn.class);
                if(schemaColumn!=null) {
                    String cname = schemaColumn.name();
                    if(!columnNames.add(cname)) throw new AssertionError("Column name found twice: "+clazz.getName()+"->"+cname);
                    int order = schemaColumn.order();
                    newColumns.ensureCapacity(order+1);
                    if(newColumns.set(order, new MethodColumn(cname, schemaColumn.unique(), method))!=null) throw new AssertionError("Column index found twice: "+clazz.getName()+"->"+order);
                }
            }
            int size = newColumns.size();
            // Make sure each column index is used in succession
            if(size!=columnNames.size()) {
                // Find missing column(s)
                StringBuilder SB = new StringBuilder("The following column indexes do not have a corresponding column method: "+clazz.getName()+"->");
                boolean didOne = false;
                for(int c=0; c<size; c++) {
                    if(newColumns.get(c)==null) {
                        if(didOne) SB.append(", ");
                        else didOne = true;
                        SB.append(c);
                    }
                }
                throw new AssertionError(SB.toString());
            }
            // Make unmodifiable
            List<MethodColumn> unmod;
            if(size==0) throw new AssertionError("No columns found");
            if(size==1) unmod = Collections.singletonList(newColumns.get(0));
            else unmod = Collections.unmodifiableList(newColumns);
            // Put in cache
            List<MethodColumn> existingColumns = columns.putIfAbsent(clazz, unmod);
            methodColumns = existingColumns==null ? unmod : existingColumns;
        }
        return methodColumns;
    }

    /**
     * Gets value of the column with the provided index, by using the SchemaColumn annotation.
     */
    final public Object getColumn(int index) {
        try {
            return getMethodColumns(getClass()).get(index).getMethod().invoke(this);
        } catch(IllegalAccessException err) {
            throw new WrappedException(err);
        } catch(InvocationTargetException err) {
            throw new WrappedException(err);
        }
    }
}
