/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2006-2009, 2016, 2017  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
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
