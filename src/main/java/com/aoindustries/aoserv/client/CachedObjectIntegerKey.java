/*
 * Copyright 2006-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * An object that is cached and uses an int as its primary key,
 *
 * @author  AO Industries, Inc.
 */
public abstract class CachedObjectIntegerKey<V extends CachedObjectIntegerKey<V>> extends CachedObject<Integer,V> {

	protected int pkey;

	@Override
	boolean equalsImpl(Object O) {
		return
			O!=null
			&& O.getClass()==getClass()
			&& ((CachedObjectIntegerKey)O).pkey==pkey
		;
	}

	public int getPkey() {
		return pkey;
	}

	@Override
	public Integer getKey() {
		return pkey;
	}

	@Override
	int hashCodeImpl() {
		return pkey;
	}

	@Override
	String toStringImpl() throws IOException, SQLException {
		return Integer.toString(pkey);
	}
}