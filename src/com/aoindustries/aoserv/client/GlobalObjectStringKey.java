package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * An object that is cached and uses an int as its primary key,
 *
 * @author  AO Industries, Inc.
 */
public abstract class GlobalObjectStringKey<T extends GlobalObjectStringKey<T>> extends GlobalObject<String,T> {

    protected String pkey;

    @Override
    boolean equalsImpl(Object O) {
	return
            O!=null
            && O.getClass()==getClass()
            && ((GlobalObjectStringKey)O).pkey.equals(pkey)
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
    String toStringImpl() {
	return pkey;
    }
}
