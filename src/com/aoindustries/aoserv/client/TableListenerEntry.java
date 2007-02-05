package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.*;

/**
 * Used by <code>AOServTable</code> to store the list of
 * <code>TableListener</code>s.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class TableListenerEntry {

    TableListener listener;
    long delay;
    long delayStart=-1;

    TableListenerEntry(TableListener listener, long delay) {
	this.listener=listener;
	this.delay=delay;
    }
}
