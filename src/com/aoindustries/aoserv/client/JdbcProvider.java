package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * A <code>JDBCProvider</code> provides connection information for a JDBC-enabled database.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public interface JdbcProvider {
    
    /**
     * Gets the classname of the driver used to contact the server.
     */
    String getJdbcDriver();

    /**
     * Gets the URL that should be used for JDBC connections.
     */
    String getJdbcUrl(boolean ipOnly);
    
    /**
     * Gets the URL of the JDBC documentation.
     */
    String getJdbcDocumentationUrl();
}
