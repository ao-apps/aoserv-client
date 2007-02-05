package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
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
