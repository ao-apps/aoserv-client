/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2020, 2021  AO Industries, Inc.
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
 * Synchronously notified with each object as the table is being loaded.
 * Implementations should execute quickly, so as to not slow down the table
 * loading process.  This is useful so that tasks may be completed during the
 * transfer, which may yield more efficient and interactive environment.
 *
 * @see  AOServTable#addTableLoadListener
 *
 * @author  AO Industries, Inc.
 */
// TODO: Figure-out the correct generics for this interface
public interface TableLoadListener {

	/**
	 * Whenever an {@link AOServTable} is starting to be loaded, this is called
	 * with the parameter that was provided in the
	 * {@link AOServTable#addTableLoadListener(com.aoindustries.aoserv.client.TableLoadListener, java.lang.Object)}
	 * call.
	 * <p>
	 * The object returned is stored and will be the parameter provided in the next call.
	 * </p>
	 */
	Object onTableLoadStarted(AOServTable<?, ?> table, Object param);

	/**
	 * Called once the number of rows that will be loaded is known or known to be unknown.
	 * <p>
	 * The number of rows is only known when a {@link ProgressListener} has been
	 * registered on the table.  If the row count is required, also add a
	 * {@link ProgressListener}.
	 * </p>
	 * <p>
	 * When a table load auto-retries on failure, this may be called more than once
	 * for a given {@link #onTableLoadStarted(com.aoindustries.aoserv.client.AOServTable, java.lang.Object)}.
	 * Each time, it indicates that the load is starting over.
	 * </p>
	 * <p>
	 * The object returned is stored and will be the parameter provided in the next call.
	 * </p>
	 *
	 * @see  ProgressListener
	 */
	Object onTableLoadRowCount(AOServTable<?, ?> table, Object param, Long rowCount);

	/**
	 * Called as each row is loaded.
	 * <p>
	 * The object returned is stored and will be the parameter provided in the next call.
	 * </p>
	 *
	 * @param  rowNumber  The row number loaded, started at zero.
	 */
	Object onTableRowLoaded(AOServTable<?, ?> table, Object param, long rowNumber, AOServObject<?, ?> object);

	/**
	 * Called when the table load has failed.
	 * <p>
	 * The object returned is stored and will be the parameter provided in the next call.
	 * </p>
	 */
	Object onTableLoadFailed(AOServTable<?, ?> table, Object param, Throwable cause);

	/**
	 * Called when the table is completely loaded.
	 * <p>
	 * The object returned is stored and will be the parameter provided in the next call.
	 * </p>
	 */
	Object onTableLoadCompleted(AOServTable<?, ?> table, Object param);
}
