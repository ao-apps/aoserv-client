package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  HttpdJBossVersion
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdJBossVersionTable extends GlobalTableIntegerKey<HttpdJBossVersion> {

    HttpdJBossVersionTable(AOServConnector connector) {
	super(connector, HttpdJBossVersion.class);
    }

    public HttpdJBossVersion get(Object pkey) {
	return getUniqueRow(HttpdJBossVersion.COLUMN_VERSION, pkey);
    }

    public HttpdJBossVersion get(int pkey) {
	return getUniqueRow(HttpdJBossVersion.COLUMN_VERSION, pkey);
    }

    public HttpdJBossVersion getHttpdJBossVersion(String version, OperatingSystemVersion osv) {
	return get(
            connector
            .technologyNames
            .get(HttpdJBossVersion.TECHNOLOGY_NAME)
            .getTechnologyVersion(connector, version, osv)
            .getPKey()
	);
    }

    int getTableID() {
	return SchemaTable.HTTPD_JBOSS_VERSIONS;
    }
}