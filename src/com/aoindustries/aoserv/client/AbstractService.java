package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.Column;
import com.aoindustries.table.Table;
import com.aoindustries.table.TableListener;
import com.aoindustries.util.WrappedException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
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
 * @author  AO Industries, Inc.
 */
abstract public class AbstractService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>, K extends Comparable<K>,V extends AOServObject<K,V>> implements AOServService<C,F,K,V> {

    protected final C connector;
    protected final Class<K> keyClass;
    protected final Class<V> valueClass;
    protected final ServiceName serviceName;
    protected final Table<V> table;

    protected AbstractService(C connector, Class<K> keyClass, Class<V> valueClass) {
        this.connector = connector;
        this.keyClass = keyClass;
        this.valueClass = valueClass;
        ServiceName tname = null;
        for(Class<?> iface : AbstractService.this.getClass().getInterfaces()) {
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
        if(tname==null) throw new AssertionError("Unable to find service name");
        serviceName = tname;
        table = new AnnotationTable();
    }

    @Override
    final public String toString() {
        return getServiceName().getDisplay();
    }

    final public C getConnector() {
        return connector;
    }

    final protected V setService(V obj) {
        return obj==null ? null : obj.setService(this);
    }

    final protected Set<V> setServices(Collection<V> objs) {
        int size = objs.size();
        if(size==0) return Collections.emptySet();
        if(size==1) return Collections.singleton(objs.iterator().next().setService(this));
        Set<V> set = new HashSet<V>(size*4/3+1);
        for(V obj : objs) set.add(obj.setService(this));
        return Collections.unmodifiableSet(set);
    }

    abstract public Set<V> getSet() throws RemoteException;

    public SortedSet<V> getSortedSet() throws RemoteException {
        Set<V> set = getSet();
        if(set instanceof SortedSet) return (SortedSet<V>)set;
        return Collections.unmodifiableSortedSet(new TreeSet<V>(set));
    }

    final public ServiceName getServiceName() {
        return serviceName;
    }

    /**
     * Obtains the table information from the annotations.
     */
    final class AnnotationTable implements Table<V> {
        final private List<Column> columns;
        AnnotationTable() {
            List<Column> tColumns = new ArrayList<Column>();
            Set<String> columnNames = new HashSet<String>();
            for(Method method : valueClass.getMethods()) {
                SchemaColumn schemaColumn = method.getAnnotation(SchemaColumn.class);
                if(schemaColumn!=null) {
                    String cname = schemaColumn.name();
                    if(!columnNames.add(cname)) throw new AssertionError("Column name found twice in table: "+getTableName()+'.'+cname);
                    tColumns.add(new Column(cname, schemaColumn.unique()));
                }
            }
            int size = tColumns.size();
            if(size==0) throw new AssertionError("No columns found");
            if(size==1) columns = Collections.singletonList(tColumns.get(0));
            else columns = Collections.unmodifiableList(tColumns);
        }

        public void addTableListener(TableListener<? super V> listener) {
            addTableListener(listener, 1000);
        }

        public void addTableListener(TableListener<? super V> listener, long batchTime) {
            throw new RuntimeException("TODO: Implement method");
        }

        public void removeTableListener(TableListener<? super V> listener) {
            throw new RuntimeException("TODO: Implement method");
        }

        /**
         * Gets the table name by finding the first implemented interface that has the TableName annotation.
         */
        public String getTableName() {
            return AbstractService.this.getServiceName().name();
        }

        /**
         * Gets the column names based on the methods that are marked as ColumnName annotation in class of this object.
         */
        public List<Column> getColumns() {
            return columns;
        }

        /**
         * Gets the rows as a result of <code>getSortedSet</code>.
         */
        public Iterator<V> getRows() {
            try {
                return getSortedSet().iterator();
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }
    }

    final public Table<V> getTable() {
        return table;
    }

    private final Map<K,V> map = new Map<K,V>() {
        // Map methods
        public V get(Object key) {
            try {
                return AbstractService.this.get(keyClass.cast(key));
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }

        public Set<Entry<K,V>> entrySet() {
            try {
                return new EntrySet<K,V>(getSet());
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }

        public Collection<V> values() {
            try {
                return getSet();
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }

        public Set<K> keySet() {
            try {
                return new KeySet<K,V>(getSet());
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
                return AbstractService.this.get(keyClass.cast(key))!=null;
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }

        public boolean isEmpty() {
            try {
                return AbstractService.this.isEmpty();
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }

        public int size() {
            try {
                return AbstractService.this.size();
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }
    };

    final public Map<K,V> getMap() {
        return map;
    }

    /**
     * This default implemention iterates through all of the objects returned by getSet
     * and looks for the first one with a matching key.  Subclasses are encouraged to
     * provide more efficient implementations.
     */
    public V get(K key) throws RemoteException {
        for(V obj : getSet()) if(obj.getKey().equals(key)) return obj;
        return null;
    }

    /**
     * This default implementation calls size to determine if the service is empty.
     * Subclasses are encouraged to provide more efficient implementations.
     */
    public boolean isEmpty() throws RemoteException {
        return size()==0;
    }

    /**
     * This default implementation calls getSet to determine if the size of the service.
     * Subclasses are encouraged to provide more efficient implementations.
     */
    public int size() throws RemoteException {
        return getSet().size();
    }

    /**
     * Calls size().
     */
    final public int getSize() throws RemoteException {
        return size();
    }
}
