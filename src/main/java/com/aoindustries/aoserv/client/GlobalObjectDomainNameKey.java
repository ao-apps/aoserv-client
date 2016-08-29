/*
 * Copyright 2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.DomainName;

/**
 * An object that is cached and uses a DomainName as its primary key,
 *
 * @author  AO Industries, Inc.
 */
public abstract class GlobalObjectDomainNameKey<T extends GlobalObjectDomainNameKey<T>> extends GlobalObject<DomainName,T> {

    protected DomainName pkey;

    @Override
    boolean equalsImpl(Object O) {
	return
            O!=null
            && O.getClass()==getClass()
            && ((GlobalObjectDomainNameKey)O).pkey.equals(pkey)
	;
    }

    public DomainName getKey() {
	return pkey;
    }

    @Override
    int hashCodeImpl() {
	return pkey.hashCode();
    }

    @Override
    String toStringImpl() {
	return pkey.toString();
    }
}
