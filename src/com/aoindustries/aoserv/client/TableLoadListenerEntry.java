package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
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
