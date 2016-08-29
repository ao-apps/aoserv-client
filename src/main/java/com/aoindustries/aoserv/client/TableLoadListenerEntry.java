/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * Used by <code>AOServTable</code> to store the list of
 * <code>TableLoadListener</code>s.
 *
 * @author  AO Industries, Inc.
 */
final public class TableLoadListenerEntry {

	final TableLoadListener listener;
	Object param;

	TableLoadListenerEntry(TableLoadListener listener, Object param) {
		this.listener=listener;
		this.param=param;
	}
}
