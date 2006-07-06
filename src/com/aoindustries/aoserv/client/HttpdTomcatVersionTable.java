package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  HttpdTomcatVersion
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatVersionTable extends GlobalTableIntegerKey<HttpdTomcatVersion> {

    HttpdTomcatVersionTable(AOServConnector connector) {
	super(connector, HttpdTomcatVersion.class);
    }

    public HttpdTomcatVersion get(Object pkey) {
	return getUniqueRow(HttpdTomcatVersion.COLUMN_VERSION, pkey);
    }

    public HttpdTomcatVersion get(int pkey) {
	return getUniqueRow(HttpdTomcatVersion.COLUMN_VERSION, pkey);
    }

    public HttpdTomcatVersion getHttpdTomcatVersion(String version, OperatingSystemVersion osv) {
	return get(
            connector
            .technologyNames
            .get(HttpdTomcatVersion.TECHNOLOGY_NAME)
            .getTechnologyVersion(connector, version, osv)
            .getPKey()
	);
    }

    int getTableID() {
	return SchemaTable.HTTPD_TOMCAT_VERSIONS;
    }
}