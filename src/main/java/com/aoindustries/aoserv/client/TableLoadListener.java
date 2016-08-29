/*
 * Copyright 2001-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * Notified with each object as the table is being loaded.  This
 * is useful so that tasks may be completed during the transfer,
 * which may yield more efficient and interactive environment.
 *
 * @see  AOServTable#addTableLoadListener
 *
 * @author  AO Industries, Inc.
 */
public interface TableLoadListener {

	/**
	 * Called when the table is completely loaded.
	 */
	Object tableLoadCompleted(AOServTable table, Object param);

	/**
	 * Whenever an <code>AOServTable</code> is starting to be loaded,
	 * this is called with the parameter that was provided in
	 * the <code>addTableLoadListener</code> call.  The object
	 * returned is stored and will be the parameter provided in
	 * the next call.
	 */
	Object tableLoadStarted(AOServTable table, Object param);

	/**
	 * Called as each row is loaded.
	 */
	Object tableRowLoaded(AOServTable table, AOServObject object, int rowNumber, Object param);
}