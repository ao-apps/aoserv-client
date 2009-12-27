package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
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

    /**
     * Value used when data has been filtered.
     */
    public static final String FILTERED = "*";

    private static final Collator collator = Collator.getInstance(Locale.ENGLISH);
    static int compareIgnoreCaseConsistentWithEquals(String S1, String S2) {
        int diff = collator.compare(S1, S2);
        if(diff!=0) return diff;
        return S1.compareTo(S2);
    }

    static int compare(int i1, int i2) {
        return i1<i2 ? -1 : i1==i2 ? 0 : 1;
    }

    static int compare(short s1, short s2) {
        return s1<s2 ? -1 : s1==s2 ? 0 : 1;
    }

    static int compareHostnames(String host1, String host2) {
        while(host1.length()>0 && host2.length()>0) {
            int pos=host1.lastIndexOf('.');
            String section1;
            if(pos==-1) {
                section1=host1;
                host1="";
            } else {
                section1=host1.substring(pos+1);
                host1=host1.substring(0, pos);
            }

            pos=host2.lastIndexOf('.');
            String section2;
            if(pos==-1) {
                section2=host2;
                host2="";
            } else {
                section2=host2.substring(pos+1);
                host2=host2.substring(0, pos);
            }

            int diff=compareIgnoreCaseConsistentWithEquals(section1, section2);
            if(diff!=0) return diff;
        }
        return compareIgnoreCaseConsistentWithEquals(host1, host2);
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
                    if(order<0) throw new AssertionError("Column order<0: "+clazz.getName()+"->"+order);
                    int newSize = order+1;
                    newColumns.ensureCapacity(newSize);
                    while(newColumns.size()<newSize) newColumns.add(null);
                    if(newColumns.set(order, new MethodColumn(cname, schemaColumn.unique(), method, schemaColumn))!=null) throw new AssertionError("Column index found twice: "+clazz.getName()+"->"+order);
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
            else {
                newColumns.trimToSize();
                unmod = Collections.unmodifiableList(newColumns);
            }
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
    /* TODO
    final public int compareTo(AOServConnector conn, AOServObject other, SQLExpression[] sortExpressions, boolean[] sortOrders) throws IllegalArgumentException, SQLException, UnknownHostException, IOException {
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

    final public int compareTo(AOServConnector conn, Comparable value, SQLExpression[] sortExpressions, boolean[] sortOrders) throws IllegalArgumentException, SQLException, UnknownHostException, IOException {
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

    final public int compareTo(AOServConnector conn, Object[] OA, SQLExpression[] sortExpressions, boolean[] sortOrders) throws IllegalArgumentException, SQLException, UnknownHostException, IOException {
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
