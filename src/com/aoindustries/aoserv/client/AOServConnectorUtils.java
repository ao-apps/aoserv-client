package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Utilities provided for various AOServConnector implementations.  Not intended for direct use.
 *
 * @author  AO Industries, Inc.
 */
final public class AOServConnectorUtils {

    private AOServConnectorUtils() {
    }

    private static <C2 extends AOServConnector<C2,F2>, F2 extends AOServConnectorFactory<C2,F2>> void addTable(Map<ServiceName,AOServService<C2,F2,?,?>> ts, AOServService<C2,F2,?,?> table) throws RemoteException {
        ServiceName tableName = table.getServiceName();
        if(ts.put(tableName, table)!=null) throw new AssertionError("Table found more than once: "+tableName);
    }

    /**
     * @see AOServConnector#getServices()
     */
    @SuppressWarnings("unchecked")
    public static <C2 extends AOServConnector<C2,F2>, F2 extends AOServConnectorFactory<C2,F2>> Map<ServiceName,AOServService<C2,F2,?,?>> createServiceMap(AOServConnector<C2,F2> connector) throws RemoteException {
        try {
            Map<ServiceName,AOServService<C2,F2,?,?>> serviceMap = new EnumMap<ServiceName,AOServService<C2,F2,?,?>>(ServiceName.class);
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
}
