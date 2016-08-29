/*
 * Copyright 2006-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An entry set that may be used by any of the various tables.
 *
 * @author  AO Industries, Inc.
 */
final class KeySet<K,V extends AOServObject<K,V>> extends AbstractSet<K> {

	private final List<V> objs;

	KeySet(List<V> objs) {
		this.objs=objs;
	}

	@Override
	public int size() {
		return objs.size();
	}

	@Override
	public Iterator<K> iterator() {
		return new Iterator<K>() {

			private int cursor=0;

			private int lastRet=-1;

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean hasNext() {
				return cursor < objs.size();
			}

			@Override
			public K next() {
				try {
					V value=objs.get(cursor);
					K next=value.getKey();
					lastRet = cursor++;
					return next;
				} catch(IndexOutOfBoundsException e) {
					throw new NoSuchElementException();
				}
			}
		};
	}
}
