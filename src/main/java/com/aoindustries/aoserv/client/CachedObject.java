/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2009, 2014, 2016  AO Industries, Inc.
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

/**
 * A <code>CachedObject</code> is stored in
 * a <code>CachedTable</code> for greater
 * performance.
 *
 * @author  AO Industries, Inc.
 */
public abstract class CachedObject<K,T extends CachedObject<K,T>> extends AOServObject<K,T> implements SingleTableObject<K,T> {

	protected AOServTable<K,T> table;

	protected CachedObject() {
	}

	/**
	 * Gets the <code>AOServTable</code> that contains this <code>AOServObject</code>.
	 *
	 * @return  the <code>AOServTable</code>.
	 */
	@Override
	final public AOServTable<K,T> getTable() {
		return table;
	}

	@Override
	final public void setTable(AOServTable<K,T> table) {
		if(this.table!=null) throw new IllegalStateException("table already set");
		this.table=table;
	}
}
