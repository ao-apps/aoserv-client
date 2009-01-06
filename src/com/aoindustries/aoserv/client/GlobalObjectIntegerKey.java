package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * An object that is cached and uses an int as its primary key,
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public abstract class GlobalObjectIntegerKey<T extends GlobalObjectIntegerKey<T>> extends GlobalObject<Integer,T> {

    protected int pkey;

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

    String toStringImpl() {
	return Integer.toString(pkey);
    }
}
