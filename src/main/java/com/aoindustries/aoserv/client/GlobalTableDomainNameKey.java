/*
 * Copyright 2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.DomainName;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  GlobalObjectDomainNameKey
 *
 * @author  AO Industries, Inc.
 */
public abstract class GlobalTableDomainNameKey<V extends GlobalObjectDomainNameKey<V>> extends GlobalTable<DomainName,V> {

	GlobalTableDomainNameKey(AOServConnector connector, Class<V> clazz) {
		super(connector, clazz);
	}

	/**
	 * Gets the object with the provided key.  The key must be a DomainName.
	 */
	@Override
	public V get(Object pkey) throws IOException, SQLException {
		return get((DomainName)pkey);
	}

	abstract public V get(DomainName pkey) throws IOException, SQLException;
}
