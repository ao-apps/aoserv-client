/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2017  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.validator.MySQLUserId;

/**
 * An object that is cached and uses {@link MySQLUserId} as its primary key,
 *
 * @author  AO Industries, Inc.
 */
public abstract class CachedObjectMySQLUserIdKey<V extends CachedObjectMySQLUserIdKey<V>> extends CachedObject<MySQLUserId,V> {

	protected MySQLUserId pkey;

	@Override
	boolean equalsImpl(Object O) {
		return
			O!=null
			&& O.getClass()==getClass()
			&& ((CachedObjectMySQLUserIdKey<?>)O).pkey.equals(pkey)
		;
	}

	@Override
	public MySQLUserId getKey() {
		return pkey;
	}

	@Override
	int hashCodeImpl() {
		return pkey.hashCode();
	}

	@Override
	String toStringImpl() {
		return pkey.toString();
	}
}
