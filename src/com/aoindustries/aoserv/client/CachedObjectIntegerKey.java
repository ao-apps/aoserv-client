package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * An object that is cached and uses an int as its primary key,
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
abstract class CachedObjectIntegerKey<V extends CachedObjectIntegerKey<V>> extends CachedObject<Integer,V> {

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

    public Integer getKey() {
	return pkey;
    }

    @Override
    int hashCodeImpl() {
	return pkey;
    }

    @Override
    String toStringImpl() {
	return Integer.toString(pkey);
    }
}
