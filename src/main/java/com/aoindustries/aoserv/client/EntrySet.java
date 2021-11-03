/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2006-2009, 2016, 2017, 2019, 2021  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client;

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
final class EntrySet<K, V extends AOServObject<K, V>> extends AbstractSet<Map.Entry<K, V>> {

	private final List<V> objs;

	EntrySet(List<V> objs) {
		this.objs = objs;
	}

	@Override
	public int size() {
		return objs.size();
	}

	@Override
	public Iterator<Map.Entry<K, V>> iterator() {
		// Java 9: new Iterator<>
		return new Iterator<Map.Entry<K, V>>() {

			private int cursor = 0;

			@Override
			public boolean hasNext() {
				return cursor < objs.size();
			}

			@Override
			public Map.Entry<K, V> next() throws NoSuchElementException {
				if(cursor >= objs.size()) throw new NoSuchElementException();
				final V value = objs.get(cursor);
				final K key = value.getKey();
				// Java 9: new Map.Entry<>
				Map.Entry<K, V> next = new Map.Entry<K, V>() {

					@Override
					public V setValue(V value) {
						throw new UnsupportedOperationException();
					}

					@Override
					public V getValue() {
						return value;
					}

					@Override
					public K getKey() {
						return key;
					}
				};
				cursor++;
				return next;
			}
		};
	}
}
