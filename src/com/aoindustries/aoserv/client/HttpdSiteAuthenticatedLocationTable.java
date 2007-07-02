package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  HttpdSiteAuthenticatedLocation
 *
 * @version  1.18
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdSiteAuthenticatedLocationTable extends CachedTableIntegerKey<HttpdSiteAuthenticatedLocation> {

    HttpdSiteAuthenticatedLocationTable(AOServConnector connector) {
	super(connector, HttpdSiteAuthenticatedLocation.class);
    }

    int addHttpdSiteAuthenticatedLocation(
        HttpdSite hs,
        String path,
        boolean isRegularExpression,
        String authName,
        String authGroupFile,
        String authUserFile,
        String require
    ) {
        return connector.requestIntQueryIL(AOServProtocol.ADD, SchemaTable.TableID.HTTPD_SITE_AUTHENTICATED_LOCATIONS, hs.getPKey(), path, isRegularExpression, authName, authGroupFile, authUserFile, require);
    }

    public HttpdSiteAuthenticatedLocation get(Object pkey) {
	return getUniqueRow(HttpdSiteAuthenticatedLocation.COLUMN_PKEY, pkey);
    }

    public HttpdSiteAuthenticatedLocation get(int pkey) {
	return getUniqueRow(HttpdSiteAuthenticatedLocation.COLUMN_PKEY, pkey);
    }

    List<HttpdSiteAuthenticatedLocation> getHttpdSiteAuthenticatedLocations(HttpdSite site) {
        return getIndexedRows(HttpdSiteAuthenticatedLocation.COLUMN_HTTPD_SITE, site.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_SITE_AUTHENTICATED_LOCATIONS;
    }
}
