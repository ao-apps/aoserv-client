package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  HttpdStaticSite
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdStaticSiteTable extends CachedTableIntegerKey<HttpdStaticSite> {

    HttpdStaticSiteTable(AOServConnector connector) {
	super(connector, HttpdStaticSite.class);
    }

    public HttpdStaticSite get(Object pkey) {
	return getUniqueRow(HttpdStaticSite.COLUMN_HTTPD_SITE, pkey);
    }

    public HttpdStaticSite get(int pkey) {
	return getUniqueRow(HttpdStaticSite.COLUMN_HTTPD_SITE, pkey);
    }

    int getTableID() {
	return SchemaTable.HTTPD_STATIC_SITES;
    }
}