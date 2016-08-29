/*
 * Copyright 2006-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  CachedObjectStringKey
 *
 * @author  AO Industries, Inc.
 */
public abstract class CachedTableStringKey<V extends CachedObjectStringKey<V>> extends CachedTable<String,V> {

	CachedTableStringKey(AOServConnector connector, Class<V> clazz) {
		super(connector, clazz);
	}

	/**
	 * Gets the object with the provided key.  The key must be a string.
	 */
	@Override
	public V get(Object pkey) throws IOException, SQLException {
		return get((String)pkey);
	}

	abstract public V get(String pkey) throws IOException, SQLException;
}
