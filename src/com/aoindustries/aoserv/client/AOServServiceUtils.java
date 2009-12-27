package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
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
import java.util.SortedSet;
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
                    if(!tname.equals(newTname)) throw new AssertionError("Incompatible services names found in interface hierarchy: "+tname+" and "+newTname);
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
    public static <K extends Comparable<K>,V extends AOServObject<K,V>> V setService(V obj, AOServService<?,?,K,V> service) {
        return obj==null ? null : obj.setService(service);
    }

    /**
     * Sets the service on an entire collection, and returns an unmodifiable set.
     */
    public static <K extends Comparable<K>,V extends AOServObject<K,V>> Set<V> setServices(Set<V> objs, AOServService<?,?,K,V> service) {
        int size = objs.size();
        if(size==0) return Collections.emptySet();
        if(size==1) return Collections.singleton(setService(objs.iterator().next(), service));
        Set<V> set = new HashSet<V>(size*4/3+1);
        for(V obj : objs) set.add(setService(obj, service));
        return Collections.unmodifiableSet(set);
    }

    /**
     * Sets the service on an entire collection, and returns an unmodifiable sorted set.
     */
    public static <K extends Comparable<K>,V extends AOServObject<K,V>> SortedSet<V> setServices(SortedSet<V> objs, AOServService<?,?,K,V> service) {
        SortedSet<V> sortedSet = new TreeSet<V>();
        for(V obj : objs) sortedSet.add(setService(obj, service));
        return Collections.unmodifiableSortedSet(sortedSet);
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
    final public static class AnnotationTable<K extends Comparable<K>,V extends AOServObject<K,V>> implements Table<MethodColumn,V> {

        final private AOServService<?,?,K,V> service;
        final private List<? extends MethodColumn> columns;

        public AnnotationTable(AOServService<?,?,K,V> service, Class<V> valueClass) {
            this.service = service;
            columns = AOServObject.getMethodColumns(valueClass);
        }

        public void addTableListener(TableListener<? super MethodColumn, ? super V> listener) {
            addTableListener(listener, 1000);
        }

        public void addTableListener(TableListener<? super MethodColumn, ? super V> listener, long batchTime) {
            throw new RuntimeException("TODO: Implement method");
        }

        public void removeTableListener(TableListener<? super MethodColumn, ? super V> listener) {
            throw new RuntimeException("TODO: Implement method");
        }

        /**
         * Gets the table name by finding the first implemented interface that has the TableName annotation.
         */
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
        public List<? extends MethodColumn> getColumns() {
            return columns;
        }

        /**
         * Gets the rows as a result of <code>getSortedSet</code>.
         */
        public Iterator<V> getRows() {
            try {
                return service.getSortedSet().iterator();
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }
    }

    /**
     * Used by ServiceMap.
     */
    final static class EntrySet<K extends Comparable<K>,V extends AOServObject<K,V>> extends AbstractSet<Map.Entry<K,V>> {

        private Set<V> objs;

        EntrySet(Set<V> objs) {
            this.objs = objs;
        }

        public int size() {
            return objs.size();
        }

        public Iterator<Map.Entry<K,V>> iterator() {
            return new Iterator<Map.Entry<K,V>>() {
                private final Iterator<V> iter = objs.iterator();

                public void remove() {
                    throw new UnsupportedOperationException();
                }

                public boolean hasNext() {
                    return iter.hasNext();
                }

                public Map.Entry<K,V> next() {
                    final V value = iter.next();
                    final K key = value.getKey();
                    Map.Entry<K,V> next = new Map.Entry<K,V>() {
                        public V setValue(V value) {
                            throw new UnsupportedOperationException();
                        }
                        public V getValue() {
                            return value;
                        }
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
    final static class KeySet<K extends Comparable<K>,V extends AOServObject<K,V>> extends AbstractSet<K> {

        private Set<V> objs;

        KeySet(Set<V> objs) {
            this.objs=objs;
        }

        public int size() {
            return objs.size();
        }

        public Iterator<K> iterator() {
            return new Iterator<K>() {

                private final Iterator<V> iter = objs.iterator();

                public void remove() {
                    throw new UnsupportedOperationException();
                }

                public boolean hasNext() {
                    return iter.hasNext();
                }

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
    final public static class ServiceMap<K extends Comparable<K>,V extends AOServObject<K,V>> implements Map<K,V> {

        private final AOServService<?,?,K,V> service;
        private final Class<K> keyClass;
        private final Class<V> valueClass;

        public ServiceMap(AOServService<?,?,K,V> service, Class<K> keyClass, Class<V> valueClass) {
            this.service = service;
            this.keyClass = keyClass;
            this.valueClass = valueClass;
        }

        public V get(Object key) {
            try {
                return service.get(keyClass.cast(key));
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }

        public Set<Entry<K,V>> entrySet() {
            try {
                return new EntrySet<K,V>(service.getSet());
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }

        public Collection<V> values() {
            try {
                return service.getSet();
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }

        public Set<K> keySet() {
            try {
                return new KeySet<K,V>(service.getSet());
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }

        public void clear() {
            throw new UnsupportedOperationException();
        }

        public void putAll(Map<? extends K, ? extends V> t) {
            throw new UnsupportedOperationException();
        }

        public V remove(Object key) {
            throw new UnsupportedOperationException();
        }

        public V put(K key, V value) {
            throw new UnsupportedOperationException();
        }

        public boolean containsValue(Object value) {
            return containsKey(valueClass.cast(value).getKey());
        }

        public boolean containsKey(Object key) {
            try {
                return service.get(keyClass.cast(key))!=null;
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }

        public boolean isEmpty() {
            try {
                return service.isEmpty();
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }

        public int size() {
            try {
                return service.getSize();
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }
    }
}
