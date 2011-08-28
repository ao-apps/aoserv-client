/*
 * Copyright 2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.Table;
import java.util.Map;

/**
 * A base implementation of AOServService to avoid repetitive implementation details.
 *
 * @author  AO Industries, Inc.
 */
abstract public class AbstractService<K extends Comparable<K>, V extends AOServObject<K>> implements AOServService<K,V> {

    protected final Class<K> keyClass;
    protected final Class<V> valueClass;
    private final ServiceName serviceName;
    protected final AOServServiceUtils.AnnotationTable<K,V> table;
    private final Map<K,V> map;

    protected AbstractService(Class<K> keyClass, Class<V> valueClass) {
        this.keyClass = keyClass;
        this.valueClass = valueClass;
        serviceName = AOServServiceUtils.findServiceNameByAnnotation(getClass());
        table = new AOServServiceUtils.AnnotationTable<K,V>(this, valueClass);
        map = new AOServServiceUtils.ServiceMap<K,V>(this, keyClass, valueClass);
    }

    @Override
    final public String toString() {
        return serviceName.toString();
    }

    @Override
    final public ServiceName getServiceName() {
        return serviceName;
    }

    @Override
    final public Table<MethodColumn,V> getTable() {
        return table;
    }

    @Override
    final public Map<K,V> getMap() {
        return map;
    }
}
