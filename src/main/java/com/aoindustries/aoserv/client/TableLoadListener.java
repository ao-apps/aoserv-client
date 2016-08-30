/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2013, 2016  AO Industries, Inc.
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
