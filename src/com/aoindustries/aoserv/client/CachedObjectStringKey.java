package com.aoindustries.aoserv.client;

import java.util.Locale;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
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

    public String getKey() {
	return pkey;
    }

    @Override
    int hashCodeImpl() {
	return pkey.hashCode();
    }

    @Override
    String toStringImpl(Locale userLocale) {
	return pkey;
    }
}
