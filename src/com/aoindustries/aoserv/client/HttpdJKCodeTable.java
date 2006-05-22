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
 * @see  HttpdJKCode
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdJKCodeTable extends GlobalTableStringKey<HttpdJKCode> {

    HttpdJKCodeTable(AOServConnector connector) {
	super(connector, HttpdJKCode.class);
    }

    public HttpdJKCode get(Object pkey) {
	return getUniqueRow(HttpdJKCode.COLUMN_CODE, pkey);
    }

    int getTableID() {
	return SchemaTable.HTTPD_JK_CODES;
    }
}