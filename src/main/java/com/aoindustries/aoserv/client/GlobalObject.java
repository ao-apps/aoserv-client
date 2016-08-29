/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * A <code>GlobalObject</code> is stored in
 * a <code>GlobalTable</code> and shared by all users
 * for greater performance.
 *
 * @author  AO Industries, Inc.
 */
abstract public class GlobalObject<K,T extends GlobalObject<K,T>> extends AOServObject<K,T> {

	protected GlobalObject() {
	}
}
