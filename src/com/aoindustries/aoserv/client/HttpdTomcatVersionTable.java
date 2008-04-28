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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(HttpdTomcatVersion.COLUMN_VERSION_name+'.'+TechnologyVersion.COLUMN_VERSION_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
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
            .getPkey()
	);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_TOMCAT_VERSIONS;
    }
}