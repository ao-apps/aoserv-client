/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2022  AO Industries, Inc.
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
import java.sql.SQLException;

/**
 * A <code>JDBCProvider</code> provides connection information for a JDBC-enabled database.
 *
 * @author  AO Industries, Inc.
 */
public interface JdbcProvider {

	/**
	 * Gets the classname of the driver used to contact the server.
	 */
	String getJdbcDriver() throws IOException, SQLException;

	/**
	 * Gets the URL that should be used for JDBC connections.
	 */
	String getJdbcUrl(boolean ipOnly) throws IOException, SQLException;

	/**
	 * Gets the URL of the JDBC documentation.
	 */
	String getJdbcDocumentationUrl() throws IOException, SQLException;
}
