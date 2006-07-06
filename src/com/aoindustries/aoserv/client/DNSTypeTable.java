package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  DNSType
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class DNSTypeTable extends GlobalTableStringKey<DNSType> {

    DNSTypeTable(AOServConnector connector) {
	super(connector, DNSType.class);
    }

    public DNSType get(Object pkey) {
	return getUniqueRow(DNSType.COLUMN_TYPE, pkey);
    }

    int getTableID() {
	return SchemaTable.DNS_TYPES;
    }
}