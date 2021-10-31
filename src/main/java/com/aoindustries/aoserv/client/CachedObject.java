/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2014, 2016, 2017, 2019, 2021  AO Industries, Inc.
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

/**
 * A <code>CachedObject</code> is stored in
 * a <code>CachedTable</code> for greater
 * performance.
 *
 * @author  AO Industries, Inc.
 */
public abstract class CachedObject<K, T extends CachedObject<K, T>> extends AOServObject<K, T> implements SingleTableObject<K, T> {

	protected AOServTable<K, T> table;

	protected CachedObject() {
	}

	@Override
	public final AOServTable<K, T> getTable() {
		return table;
	}

	@Override
	public final void setTable(AOServTable<K, T> table) {
		if(this.table!=null) throw new IllegalStateException("table already set");
		this.table=table;
	}
}
