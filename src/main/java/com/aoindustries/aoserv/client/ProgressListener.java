/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018, 2020  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client;

/**
 * Synchronously notified of table loading progress.  Implementations
 * should execute quickly, so as to not slow down the table loading
 * process.
 * <p>
 * Please note that registering a progress listener can increase the server-side
 * overhead, as in some implementations the result sets must be traversed twice:
 * once to count rows then once to return them.
 * </p>
 *
 * @see  AOServTable#addProgressListener
 *
 * @author  AO Industries, Inc.
 */
// TODO: Figure-out the correct generics for this interface
public interface ProgressListener {

	/**
	 * Gets the scale of the progress returned.  The progress
	 * values will be from zero to this number, inclusive.
	 */
	int getScale();

	/**
	 * The progress is rounded off to the scale provided by <code>getScale</code>.
	 * Whenever the rounded-off value changes, this method is called.
	 * <p>
	 * When a table load fails and auto-retries, this progress may
	 * start back at zero again.
	 * </p>
	 */
	void onProgressChanged(AOServTable<?,?> table, int position, int scale);
}
