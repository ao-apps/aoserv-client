package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * Used by <code>AOServTable</code> to store the list of
 * <code>TableLoadListener</code>s.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class TableLoadListenerEntry {

    TableLoadListener listener;
    Object param;

    TableLoadListenerEntry(TableLoadListener listener, Object param) {
	this.listener=listener;
	this.param=param;
    }
}
