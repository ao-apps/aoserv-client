/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
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
