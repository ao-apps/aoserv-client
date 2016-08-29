/*
 * Copyright 2012, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * An object that is cached and uses a long as its primary key,
 *
 * @author  AO Industries, Inc.
 */
public abstract class CachedObjectLongKey<V extends CachedObjectLongKey<V>> extends CachedObject<Long,V> {

	protected long pkey;

	@Override
	boolean equalsImpl(Object O) {
		return
			O!=null
			&& O.getClass()==getClass()
			&& ((CachedObjectLongKey<?>)O).pkey==pkey
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
	int hashCodeImpl() {
		// Same approach as java.lang.Long
		return (int)(pkey ^ (pkey >>> 32));
	}

	@Override
	String toStringImpl() throws IOException, SQLException {
		return Long.toString(pkey);
	}
}
