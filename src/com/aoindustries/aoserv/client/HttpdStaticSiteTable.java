package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(HttpdStaticSite.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_SITE_NAME_name, ASCENDING),
        new OrderBy(HttpdStaticSite.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public HttpdStaticSite get(Object pkey) {
	return getUniqueRow(HttpdStaticSite.COLUMN_HTTPD_SITE, pkey);
    }

    public HttpdStaticSite get(int pkey) {
	return getUniqueRow(HttpdStaticSite.COLUMN_HTTPD_SITE, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_STATIC_SITES;
    }
}