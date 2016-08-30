/*
 * Copyright 2006-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * An object that is cached and uses an int as its primary key,
 *
 * @author  AO Industries, Inc.
 */
public abstract class CachedObjectStringKey<V extends CachedObjectStringKey<V>> extends CachedObject<String,V> {

	protected String pkey;

	@Override
	boolean equalsImpl(Object O) {
		return
			O!=null
			&& O.getClass()==getClass()
			&& ((CachedObjectStringKey)O).pkey.equals(pkey)
		;
	}

	@Override
	public String getKey() {
		return pkey;
	}

	@Override
	int hashCodeImpl() {
		return pkey.hashCode();
	}

	@Override
	String toStringImpl() {
		return pkey;
	}
}