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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(HttpdSiteAuthenticatedLocation.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_SITE_NAME_name, ASCENDING),
        new OrderBy(HttpdSiteAuthenticatedLocation.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addHttpdSiteAuthenticatedLocation(
        HttpdSite hs,
        String path,
        boolean isRegularExpression,
        String authName,
        String authGroupFile,
        String authUserFile,
        String require
    ) throws IOException, SQLException {
        return connector.requestIntQueryIL(true, AOServProtocol.CommandID.ADD, SchemaTable.TableID.HTTPD_SITE_AUTHENTICATED_LOCATIONS, hs.getPkey(), path, isRegularExpression, authName, authGroupFile, authUserFile, require);
    }

    public HttpdSiteAuthenticatedLocation get(int pkey) throws IOException, SQLException {
        return getUniqueRow(HttpdSiteAuthenticatedLocation.COLUMN_PKEY, pkey);
    }

    List<HttpdSiteAuthenticatedLocation> getHttpdSiteAuthenticatedLocations(HttpdSite site) throws IOException, SQLException {
        return getIndexedRows(HttpdSiteAuthenticatedLocation.COLUMN_HTTPD_SITE, site.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_SITE_AUTHENTICATED_LOCATIONS;
    }
}
