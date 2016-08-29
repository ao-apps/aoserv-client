/*
 * Copyright 2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  CachedObjectAccountingCodeKey
 *
 * @author  AO Industries, Inc.
 */
public abstract class CachedTableAccountingCodeKey<V extends CachedObjectAccountingCodeKey<V>> extends CachedTable<AccountingCode,V> {

	CachedTableAccountingCodeKey(AOServConnector connector, Class<V> clazz) {
		super(connector, clazz);
	}

	/**
	 * Gets the object with the provided key.  The key must be an AccountingCode.
	 */
	@Override
	public V get(Object pkey) throws IOException, SQLException {
		return get((AccountingCode)pkey);
	}

	abstract public V get(AccountingCode pkey) throws IOException, SQLException;
}
