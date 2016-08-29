/*
 * Copyright 2012, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  CachedObjectLongKey
 *
 * @author  AO Industries, Inc.
 */
public abstract class CachedTableLongKey<V extends CachedObjectLongKey<V>> extends CachedTable<Long,V> {

	CachedTableLongKey(AOServConnector connector, Class<V> clazz) {
		super(connector, clazz);
	}

	/**
	 * Gets the object with the provided key.  The key must be either a Long or a String.
	 * If a String, will be parsed to a long.
	 *
	 * @exception IllegalArgumentException if pkey is neither a Long nor a String.
	 * @exception NumberFormatException if String cannot be parsed to a Long
	 */
	@Override
	public V get(Object pkey) throws IOException, SQLException, IllegalArgumentException, NumberFormatException {
		if(pkey instanceof Long) return get(((Long)pkey).longValue());
		else if(pkey instanceof String) return get(Long.parseLong((String)pkey));
		else throw new IllegalArgumentException("pkey is neither a Long nor a String: "+pkey);
	}

	abstract public V get(long pkey) throws IOException, SQLException;
}
