/*
 * aoserv-client - Java client for the AOServ platform.
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

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  CachedObjectIntegerKey
 *
 * @author  AO Industries, Inc.
 */
public abstract class CachedTableIntegerKey<V extends CachedObjectIntegerKey<V>> extends CachedTable<Integer,V> {

	CachedTableIntegerKey(AOServConnector connector, Class<V> clazz) {
		super(connector, clazz);
	}

	/**
	 * Gets the object with the provided key.  The key must be either an Integer or a String.
	 * If a String, will be parsed to an integer.
	 *
	 * @exception IllegalArgumentException if pkey is neither an Integer nor a String.
	 * @exception NumberFormatException if String cannot be parsed to an Integer
	 *
	 * @deprecated  Always try to lookup by specific keys; the compiler will help you more when types change.
	 */
	@Deprecated
	@Override
	public V get(Object pkey) throws IOException, SQLException, IllegalArgumentException, NumberFormatException {
		if(pkey instanceof Integer) return get(((Integer)pkey).intValue());
		else if(pkey instanceof String) return get(Integer.parseInt((String)pkey));
		else throw new IllegalArgumentException("pkey is neither an Integer nor a String: "+pkey);
	}

	abstract public V get(int pkey) throws IOException, SQLException;
}
