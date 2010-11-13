package com.aoindustries.aoserv.client;

/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.Table;
import com.aoindustries.table.TableListener;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Utilities provided for various AOServService implementations.  Not intended for direct use.
 *
 * @author  AO Industries, Inc.
 */
final public class AOServServiceUtils {

    private AOServServiceUtils() {
    }

    /**
     * Gets the service name by the interface annotations.
     */
    public static ServiceName findServiceNameByAnnotation(Class<? extends AOServService> clazz) {
        ServiceName tname = null;
        for(Class<?> iface : clazz.getInterfaces()) {
            ServiceAnnotation annoServiceName = iface.getAnnotation(ServiceAnnotation.class);
            if(annoServiceName!=null) {
                ServiceName newTname = annoServiceName.value();
                if(tname!=null) {
                    if(tname!=newTname) throw new AssertionError("Incompatible services names found in interface hierarchy: "+tname+" and "+newTname);
                } else {
                    tname = newTname;
                }
            }
        }
        if(tname==null) throw new AssertionError("Unable to find service name: clazz="+clazz.getName());
        return tname;
    }

    /**
     * Sets the service on the provided object, possibly cloning it in the process.
     */
    @SuppressWarnings("unchecked")
    public static <K extends Comparable<K>,V extends AOServObject<K>> V setService(V obj, AOServService<?,?,K,?> service) throws RemoteException {
        return obj==null ? null : (V)obj.setService(service);
    }

    /**
     * Sets the service on an entire collection, and returns an unmodifiable set.
     */
    public static <K extends Comparable<K>,V extends AOServObject<K>> IndexedSet<V> setServices(IndexedSet<V> objs, AOServService<?,?,K,?> service) throws RemoteException {
        int size = objs.size();
        if(size==0) return IndexedSet.emptyIndexedSet();
        if(size==1) {
            V oldObj = objs.iterator().next();
            V newObj = setService(oldObj, service);
            return newObj==oldObj ? objs : new IndexedSet<V>(newObj);
        }
        // Only create a new set when the first new object is created
        boolean needsNewSet = false;
        for(V oldObj : objs) {
            V newObj = setService(oldObj, service);
            if(newObj!=oldObj) {
                needsNewSet = true;
                break;
            }
        }
        if(!needsNewSet) return objs;
        Set<V> set = new HashSet<V>(size*4/3+1);
        for(V oldObj : objs) set.add(setService(oldObj, service));
        return new IndexedSet<V>(set);
    }

    /**
     * Wraps the set to make it unmodifiable.  Takes advantage of
     * <code>Collections.emptySet</code> and <code>Collections.singleton</code> when possible.
     */
    public static <T> Set<T> unmodifiableSet(Set<? extends T> objs) {
        int size = objs.size();
        if(size==0) return Collections.emptySet();
        if(size==1) return Collections.singleton(objs.iterator().next());
        return Collections.unmodifiableSet(objs);
    }

    /**
     * Obtains the table information from the annotations on the provided service.
     *
     * @see AOServService#getTable()
     */
    final public static class AnnotationTable<
        K extends Comparable<K>,
        V extends AOServObject<K> & Comparable<V> & DtoFactory<?>
    > implements Table<MethodColumn,V> {

        final private AOServService<?,?,K,V> service;
        final private List<? extends MethodColumn> columns;
        final private Map<String,? extends MethodColumn> columnMap;

        public AnnotationTable(AOServService<?,?,K,V> service, Class<V> valueClass) {
            this.service = service;
            columns = AOServObjectUtils.getMethodColumns(valueClass);
            columnMap = AOServObjectUtils.getMethodColumnMap(valueClass);
        }

        @Override
        public void addTableListener(TableListener<? super MethodColumn, ? super V> listener) {
            addTableListener(listener, 1000);
        }

        @Override
        public void addTableListener(TableListener<? super MethodColumn, ? super V> listener, long batchTime) {
            throw new RuntimeException("TODO: Implement method");
        }

        @Override
        public void removeTableListener(TableListener<? super MethodColumn, ? super V> listener) {
            throw new RuntimeException("TODO: Implement method");
        }

        /**
         * Gets the table name by finding the first implemented interface that has the TableName annotation.
         */
        @Override
        public String getTableName() {
            try {
                return service.getServiceName().name();
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }

        /**
         * Gets the column names based on the methods that are marked as ColumnName annotation in class of this object.
         */
        @Override
        public List<? extends MethodColumn> getColumns() {
            return columns;
        }

        /**
         * Gets a column given its unique name.
         *
         * @exception  IllegalArgumentException when the column doesn't exist in this table.
         */
        public MethodColumn getColumn(String columnName) throws IllegalArgumentException {
            MethodColumn column = columnMap.get(columnName);
            if(column==null) throw new IllegalArgumentException("Column not found: "+columnName);
            return column;
        }

        /**
         * Gets the sorted rows as a result of <code>getSet</code>.
         *
         * @see AOServService#getSet()
         */
        @Override
        public Iterator<V> getRows() {
            try {
                return new TreeSet<V>(service.getSet()).iterator();
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }
    }

    /**
     * Used by ServiceMap.
     */
    final static class EntrySet<K extends Comparable<K>,V extends AOServObject<K>> extends AbstractSet<Map.Entry<K,V>> {

        private Set<V> objs;

        EntrySet(Set<V> objs) {
            this.objs = objs;
        }

        @Override
        public int size() {
            return objs.size();
        }

        @Override
        public Iterator<Map.Entry<K,V>> iterator() {
            return new Iterator<Map.Entry<K,V>>() {
                private final Iterator<V> iter = objs.iterator();

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public boolean hasNext() {
                    return iter.hasNext();
                }

                @Override
                public Map.Entry<K,V> next() {
                    final V value = iter.next();
                    final K key = value.getKey();
                    Map.Entry<K,V> next = new Map.Entry<K,V>() {
                        @Override
                        public V setValue(V value) {
                            throw new UnsupportedOperationException();
                        }
                        @Override
                        public V getValue() {
                            return value;
                        }
                        @Override
                        public K getKey() {
                            return key;
                        }
                    };
                    return next;
                }
            };
        }
    }

    /**
     * Used by ServiceMap.
     */
    final static class KeySet<K extends Comparable<K>,V extends AOServObject<K>> extends AbstractSet<K> {

        private Set<V> objs;

        KeySet(Set<V> objs) {
            this.objs=objs;
        }

        @Override
        public int size() {
            return objs.size();
        }

        @Override
        public Iterator<K> iterator() {
            return new Iterator<K>() {

                private final Iterator<V> iter = objs.iterator();

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public boolean hasNext() {
                    return iter.hasNext();
                }

                @Override
                public K next() {
                    return iter.next().getKey();
                }
            };
        }
    }

    /**
     * A map view of the provided service.
     *
     * @see AOServService#getMap()
     */
    final public static class ServiceMap<
        K extends Comparable<K>,
        V extends AOServObject<K> & Comparable<V> & DtoFactory<?>
    > implements Map<K,V> {

        private final AOServService<?,?,K,V> service;
        private final Class<K> keyClass;
        private final Class<V> valueClass;

        public ServiceMap(AOServService<?,?,K,V> service, Class<K> keyClass, Class<V> valueClass) {
            this.service = service;
            this.keyClass = keyClass;
            this.valueClass = valueClass;
        }

        @Override
        public V get(Object key) {
            try {
                return service.get(keyClass.cast(key));
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }

        @Override
        public Set<Entry<K,V>> entrySet() {
            try {
                return new EntrySet<K,V>(service.getSet());
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }

        @Override
        public Collection<V> values() {
            try {
                return service.getSet();
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }

        @Override
        public Set<K> keySet() {
            try {
                return new KeySet<K,V>(service.getSet());
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V put(K key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsValue(Object value) {
            return containsKey(valueClass.cast(value).getKey());
        }

        @Override
        public boolean containsKey(Object key) {
            try {
                return service.get(keyClass.cast(key))!=null;
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }

        @Override
        public boolean isEmpty() {
            try {
                return service.isEmpty();
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }

        @Override
        public int size() {
            try {
                return service.getSize();
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }
    }

    /**
     * Determines if two classes match.  Classes must be exactly the same to be considered a match - subclasses
     * are not considered a match.  Allows primitive to wrapper conversions for compatibility with auto boxing.
     */
    public static boolean classesMatch(Class<?> class1, Class<?> class2) {
        if(class1==class2) return true;
        // int
        if(class1==Integer.class) return class2==Integer.TYPE;
        if(class1==Integer.TYPE) return class2==Integer.class;
        // short
        if(class1==Short.class) return class2==Short.TYPE;
        if(class1==Short.TYPE) return class2==Short.class;
        // byte
        if(class1==Byte.class) return class2==Byte.TYPE;
        if(class1==Byte.TYPE) return class2==Byte.class;
        // long
        if(class1==Long.class) return class2==Long.TYPE;
        if(class1==Long.TYPE) return class2==Long.class;
        // float
        if(class1==Float.class) return class2==Float.TYPE;
        if(class1==Float.TYPE) return class2==Float.class;
        // double
        if(class1==Double.class) return class2==Double.TYPE;
        if(class1==Double.TYPE) return class2==Double.class;
        // char
        if(class1==Character.class) return class2==Character.TYPE;
        if(class1==Character.TYPE) return class2==Character.class;
        // boolean
        if(class1==Boolean.class) return class2==Boolean.TYPE;
        if(class1==Boolean.TYPE) return class2==Boolean.class;
        // other classes need an exact match
        return false;
    }
}
