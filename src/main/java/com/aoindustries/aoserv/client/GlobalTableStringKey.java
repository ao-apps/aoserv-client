/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2006-2013, 2016, 2017, 2018  AO Industries, Inc.
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
 * @see  GlobalObjectStringKey
 *
 * @author  AO Industries, Inc.
 */
public abstract class GlobalTableStringKey<V extends GlobalObjectStringKey<V>> extends GlobalTable<String,V> {

	protected GlobalTableStringKey(AOServConnector connector, Class<V> clazz) {
		super(connector, clazz);
	}

	/**
	 * Gets the object with the provided key.  The key must be a string.
	 *
	 * @deprecated  Always try to lookup by specific keys; the compiler will help you more when types change.
	 */
	@Deprecated
	@Override
	public V get(Object pkey) throws IOException, SQLException {
		return get((String)pkey);
	}

	/**
	 * @see  #get(java.lang.Object)
	 */
	abstract public V get(String pkey) throws IOException, SQLException;
}
