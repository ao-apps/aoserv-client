package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

/**
 * An entry set that may be used by any of the various services.
 *
 * @author  AO Industries, Inc.
 */
final class KeySet<K extends Comparable<K>,V extends AOServObject<K,V>> extends AbstractSet<K> {

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