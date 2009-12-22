package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * An entry set that may be used by any of the various services.
 *
 * @author  AO Industries, Inc.
 */
final class EntrySet<K extends Comparable<K>,V extends AOServObject<K,V>> extends AbstractSet<Map.Entry<K,V>> {

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