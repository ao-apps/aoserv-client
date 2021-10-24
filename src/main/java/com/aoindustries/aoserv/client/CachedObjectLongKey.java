/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2012, 2016, 2017, 2018, 2021  AO Industries, Inc.
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

import java.io.IOException;
import java.sql.SQLException;

/**
 * An object that is cached and uses a long as its primary key,
 *
 * @author  AO Industries, Inc.
 */
public abstract class CachedObjectLongKey<V extends CachedObjectLongKey<V>> extends CachedObject<Long, V> {

	protected long pkey;

	@Override
	public boolean equals(Object obj) {
		return
			obj != null
			&& obj.getClass() == getClass()
			&& ((CachedObjectLongKey<?>)obj).pkey == pkey
		;
	}

	public long getPkey() {
		return pkey;
	}

	@Override
	public Long getKey() {
		return pkey;
	}

	@Override
	public int hashCode() {
		// Same approach as java.lang.Long
		return (int)(pkey ^ (pkey >>> 32));
	}

	@Override
	public String toStringImpl() throws IOException, SQLException {
		return Long.toString(pkey);
	}
}
