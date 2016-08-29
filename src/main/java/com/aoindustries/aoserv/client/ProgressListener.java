/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * Synchronously notified of table loading progress.  Implementations
 * should execute quickly, so as to not slow down the table loading
 * process.
 *
 * @see  AOServTable#addProgressListener
 *
 * @author  AO Industries, Inc.
 */
public interface ProgressListener {

	/**
	 * Gets the scale of the progress returned.  The progress
	 * values will be from zero to this number, inclusive.
	 */
	int getScale();

	/**
	 * The progress is rounded off to the scale provided by <code>getScale</code>.
	 * Whenever the rounded-off value changes, this method is called.
	 */
	void progressChanged(int position, int scale, AOServTable table);
}
