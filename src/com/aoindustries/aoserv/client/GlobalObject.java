package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * A <code>GlobalObject</code> is stored in
 * a <code>GlobalTable</code> and shared by all users
 * for greater performance.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
abstract public class GlobalObject<K,T extends GlobalObject<K,T>> extends AOServObject<K,T> {

    protected GlobalObject() {
    }
}
