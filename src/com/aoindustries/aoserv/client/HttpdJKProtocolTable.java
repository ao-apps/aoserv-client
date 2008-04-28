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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(HttpdJKProtocol.COLUMN_PROTOCOL_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public HttpdJKProtocol get(Object pkey) {
	return getUniqueRow(HttpdJKProtocol.COLUMN_PROTOCOL, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_JK_PROTOCOLS;
    }
}