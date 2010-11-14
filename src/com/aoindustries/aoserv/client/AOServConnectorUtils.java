/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.util.ArraySet;
import com.aoindustries.util.HashCodeComparator;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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
            return Collections.unmodifiableMap(serviceMap);
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
        List<V> elements = new ArrayList<V>(size);
        for(V oldObj : objs) elements.add(setConnector(oldObj, connector));
        Collections.sort(elements, HashCodeComparator.getInstance());
        return IndexedSet.wrap(objs.getServiceName(), new ArraySet<V>(elements));
    }
}
