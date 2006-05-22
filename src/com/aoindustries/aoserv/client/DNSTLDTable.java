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
 * @see  DNSTLD
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class DNSTLDTable extends GlobalTableStringKey<DNSTLD> {

    DNSTLDTable(AOServConnector connector) {
	super(connector, DNSTLD.class);
    }

    public DNSTLD get(Object pkey) {
	return getUniqueRow(DNSTLD.COLUMN_DOMAIN, pkey);
    }

    int getTableID() {
	return SchemaTable.DNS_TLDS;
    }
}