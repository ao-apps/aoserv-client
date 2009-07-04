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
public abstract class GlobalObjectIntegerKey<T extends GlobalObjectIntegerKey<T>> extends GlobalObject<Integer,T> {

    protected int pkey;

    @Override
    boolean equalsImpl(Object O) {
	return
            O!=null
            && O.getClass()==getClass()
            && ((GlobalObjectIntegerKey)O).pkey==pkey
	;
    }

    public int getPkey() {
	return pkey;
    }

    public Integer getKey() {
	return pkey;
    }

    int hashCodeImpl() {
	return pkey;
    }

    String toStringImpl(Locale userLocale) {
	return Integer.toString(pkey);
    }
}
