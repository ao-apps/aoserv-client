/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.util.AoCollections;
import com.aoindustries.util.UnionClassSet;
import com.aoindustries.util.graph.Edge;
import com.aoindustries.util.graph.SymmetricGraph;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.rmi.RemoteException;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Utilities provided for various AOServConnector implementations.  Not intended for direct use.
 *
 * @author  AO Industries, Inc.
 */
final public class AOServConnectorUtils {

    private AOServConnectorUtils() {
    }

    private static void addTable(Map<ServiceName,AOServService<?,?>> ts, AOServService<?,?> table) throws RemoteException {
        ServiceName tableName = table.getServiceName();
        if(ts.put(tableName, table)!=null) throw new AssertionError("Table found more than once: "+tableName);
    }

    /**
     * @see AOServConnector#getServices()
     */
    public static Map<ServiceName,AOServService<?,?>> createServiceMap(AOServConnector connector) throws RemoteException {
        try {
            Map<ServiceName,AOServService<?,?>> serviceMap = new EnumMap<ServiceName,AOServService<?,?>>(ServiceName.class);
            // Use reflection
            for(Method method : connector.getClass().getMethods()) {
                if(
                    Modifier.isPublic(method.getModifiers())
                    && method.getParameterTypes().length==0
                    && AOServService.class.isAssignableFrom(method.getReturnType())
                ) {
                    addTable(serviceMap, AOServService.class.cast(method.invoke(connector)));
                }
            }
            // Make sure every table has been added
            for(ServiceName tableName : ServiceName.values) {
                if(!serviceMap.containsKey(tableName)) throw new AssertionError("Table not found: "+tableName);
            }
            return AoCollections.optimalUnmodifiableMap(serviceMap);
        } catch(IllegalAccessException err) {
            throw new RemoteException(err.getMessage(), err);
        } catch(InvocationTargetException err) {
            throw new RemoteException(err.getMessage(), err);
        }
    }

    /**
     * Sets the connector on the provided object, possibly cloning it in the process.
     */
    @SuppressWarnings("unchecked")
    public static <V extends AOServObject<?>> V setConnector(V obj, AOServConnector connector) throws RemoteException {
        return obj==null ? null : (V)obj.setConnector(connector);
    }

    /**
     * Sets the connector on an entire collection, and returns an unmodifiable set.
     */
    public static <V extends AOServObject<?>> IndexedSet<V> setConnector(IndexedSet<V> objs, AOServConnector connector) throws RemoteException {
        int size = objs.size();
        if(size==0) return objs;
        if(size==1) {
            V oldObj = objs.iterator().next();
            V newObj = setConnector(oldObj, connector);
            return newObj==oldObj ? objs : IndexedSet.wrap(objs.getServiceName(), newObj);
        }
        // Only create a new set when the first new object is created
        boolean needsNewSet = false;
        for(V oldObj : objs) {
            V newObj = setConnector(oldObj, connector);
            if(newObj!=oldObj) {
                needsNewSet = true;
                break;
            }
        }
        if(!needsNewSet) return objs;
        ArrayList<V> elements = new ArrayList<V>(size);
        for(V oldObj : objs) elements.add(setConnector(oldObj, connector));
        return IndexedSet.wrap(objs.getServiceName(), elements);
    }

    /**
     * @see  AOServConnector#getDependencyGraph()
     */
    public static SymmetricGraph<AOServObject<?>,Edge<AOServObject<?>>,RemoteException> createDependencyMap(final AOServConnector conn) {
        return new SymmetricGraph<AOServObject<?>,Edge<AOServObject<?>>,RemoteException>() {

            @Override
            public Set<AOServObject<?>> getVertices() throws RemoteException {
                Set<AOServObject<?>> vertices = new UnionClassSet<AOServObject<?>>();
                for(AOServService<?,?> service : conn.getServices().values()) {
                    // UnionServices do not add any new types of rows.
                    if(!(service instanceof UnionService)) vertices.addAll(service.getSet());
                }
                return AoCollections.optimalUnmodifiableSet(vertices);
            }

            @Override
            public Set<Edge<AOServObject<?>>> getEdgesFrom(final AOServObject<?> from) throws RemoteException {
                final Set<? extends AOServObject<?>> tos = from.getDependencies();
                return new AbstractSet<Edge<AOServObject<?>>>() {
                    @Override
                    public int size() {
                        return tos.size();
                    }
                    @Override
                    public boolean isEmpty() {
                        return tos.isEmpty();
                    }
                    @Override
                    public boolean contains(Object o) {
                        if(!(o instanceof Edge)) return false;
                        @SuppressWarnings("unchecked")
                        Edge<AOServObject<?>> other = (Edge<AOServObject<?>>)o;
                        return
                            from.equals(other.getFrom())
                            && tos.contains(other.getTo())
                        ;
                    }
                    @Override
                    public Iterator<Edge<AOServObject<?>>> iterator() {
                        final Iterator<? extends AOServObject<?>> toIter = tos.iterator();
                        return new Iterator<Edge<AOServObject<?>>>() {
                            @Override
                            public boolean hasNext() {
                                return toIter.hasNext();
                            }
                            @Override
                            public Edge<AOServObject<?>> next() {
                                return new Edge<AOServObject<?>>(from, toIter.next());
                            }
                            @Override
                            public void remove() {
                                throw new UnsupportedOperationException();
                            }
                        };
                    }
                };
            }

            @Override
            public Set<Edge<AOServObject<?>>> getEdgesTo(final AOServObject<?> to) throws RemoteException {
                final Set<? extends AOServObject<?>> froms = to.getDependentObjects();
                return new AbstractSet<Edge<AOServObject<?>>>() {
                    @Override
                    public int size() {
                        return froms.size();
                    }
                    @Override
                    public boolean isEmpty() {
                        return froms.isEmpty();
                    }
                    @Override
                    public boolean contains(Object o) {
                        if(!(o instanceof Edge)) return false;
                        @SuppressWarnings("unchecked")
                        Edge<AOServObject<?>> other = (Edge<AOServObject<?>>)o;
                        return
                            to.equals(other.getTo())
                            && froms.contains(other.getFrom())
                        ;
                    }
                    @Override
                    public Iterator<Edge<AOServObject<?>>> iterator() {
                        final Iterator<? extends AOServObject<?>> fromIter = froms.iterator();
                        return new Iterator<Edge<AOServObject<?>>>() {
                            @Override
                            public boolean hasNext() {
                                return fromIter.hasNext();
                            }
                            @Override
                            public Edge<AOServObject<?>> next() {
                                return new Edge<AOServObject<?>>(fromIter.next(), to);
                            }
                            @Override
                            public void remove() {
                                throw new UnsupportedOperationException();
                            }
                        };
                    }
                };
            }
        };
    }
}
