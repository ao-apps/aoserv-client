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
 * @see  HttpdBind
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdBindTable extends CachedTableIntegerKey<HttpdBind> {

    HttpdBindTable(AOServConnector connector) {
	super(connector, HttpdBind.class);
    }

    List<HttpdBind> getHttpdBinds(HttpdServer server) {
        return getIndexedRows(HttpdBind.COLUMN_HTTPD_SERVER, server.pkey);
    }

    public HttpdBind get(Object pkey) {
	return getUniqueRow(HttpdBind.COLUMN_NET_BIND, pkey);
    }

    public HttpdBind get(int pkey) {
	return getUniqueRow(HttpdBind.COLUMN_NET_BIND, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_BINDS;
    }
}