/*
 * Copyright 2006-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  GlobalObjectIntegerKey
 *
 * @author  AO Industries, Inc.
 */
public abstract class GlobalTableIntegerKey<V extends GlobalObjectIntegerKey<V>> extends GlobalTable<Integer,V> {

	GlobalTableIntegerKey(AOServConnector connector, Class<V> clazz) {
		super(connector, clazz);
	}

	/**
	 * Gets the object with the provided key.  The key must be an Integer.
	 */
	@Override
	public V get(Object pkey) throws IOException, SQLException {
		return get(((Integer)pkey).intValue());
	}

	abstract public V get(int pkey) throws IOException, SQLException;
}
