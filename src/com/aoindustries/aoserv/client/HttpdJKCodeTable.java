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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(HttpdJKCode.COLUMN_CODE_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public HttpdJKCode get(Object pkey) {
	return getUniqueRow(HttpdJKCode.COLUMN_CODE, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_JK_CODES;
    }
}