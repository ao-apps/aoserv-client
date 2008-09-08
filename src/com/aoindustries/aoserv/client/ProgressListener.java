package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * Synchronously notified of table loading progress.  Implementations
 * should execute quickly, so as to not slow down the table loading
 * process.
 *
 * @see  AOServTable#addProgressListener
 *
 * @version  1.0a
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
