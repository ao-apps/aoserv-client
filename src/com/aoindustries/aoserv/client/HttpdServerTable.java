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
 * @see  HttpdServer
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdServerTable extends CachedTableIntegerKey<HttpdServer> {

    HttpdServerTable(AOServConnector connector) {
	super(connector, HttpdServer.class);
    }

    public HttpdServer get(Object pkey) {
	return getUniqueRow(HttpdServer.COLUMN_PKEY, pkey);
    }

    public HttpdServer get(int pkey) {
	return getUniqueRow(HttpdServer.COLUMN_PKEY, pkey);
    }

    List<HttpdServer> getHttpdServers(AOServer ao) {
        return getIndexedRows(HttpdServer.COLUMN_AO_SERVER, ao.pkey);
    }

    List<HttpdServer> getHttpdServers(Package pk) {
        return getIndexedRows(HttpdServer.COLUMN_PACKAGE, pk.pkey);
    }

    int getTableID() {
	return SchemaTable.HTTPD_SERVERS;
    }
}