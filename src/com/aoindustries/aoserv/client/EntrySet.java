package com.aoindustries.aoserv.client;

/*
 * Copyright 2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * An entry set that may be used by any of the various tables.
 *
 * @author  AO Industries, Inc.
 */
final class EntrySet<K,V extends AOServObject<K,V>> extends AbstractSet<Map.Entry<K,V>> {

    private List<V> objs;

    EntrySet(List<V> objs) {
        this.objs=objs;
    }

    public int size() {
        return objs.size();
    }

    public Iterator<Map.Entry<K,V>> iterator() {
        return new Iterator<Map.Entry<K,V>>() {

            private int cursor=0;

            private int lastRet=-1;

            public void remove() {
                throw new UnsupportedOperationException();
            }

            public boolean hasNext() {
                return cursor < objs.size();
            }

            public Map.Entry<K,V> next() {
                try {
                    final V value=objs.get(cursor);
                    final K key=value.getKey();
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
                    lastRet = cursor++;
                    return next;
                } catch(IndexOutOfBoundsException e) {
                    throw new NoSuchElementException();
                }
            }
        };
    }
}