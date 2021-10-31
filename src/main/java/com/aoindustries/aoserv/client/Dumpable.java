/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Flags an <code>AOServObject</code>s as being able to dump its contents into
 * a <code>Writer</code>.
 *
 * @see  AOServObject
 *
 * @author  AO Industries, Inc.
 */
public interface Dumpable {

	/**
	 * Dumps the contents of this object into a <code>Writer</code>
	 */
	void dump(PrintWriter out) throws IOException, SQLException;
}
