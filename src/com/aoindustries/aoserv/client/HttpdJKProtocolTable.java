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
 * @see  HttpdJKProtocol
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdJKProtocolTable extends GlobalTableStringKey<HttpdJKProtocol> {

    HttpdJKProtocolTable(AOServConnector connector) {
	super(connector, HttpdJKProtocol.class);
    }

    public HttpdJKProtocol get(Object pkey) {
	return getUniqueRow(HttpdJKProtocol.COLUMN_PROTOCOL, pkey);
    }

    int getTableID() {
	return SchemaTable.HTTPD_JK_PROTOCOLS;
    }
}