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
 * @see  DNSForbiddenZone
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class DNSForbiddenZoneTable extends GlobalTableStringKey<DNSForbiddenZone> {

    DNSForbiddenZoneTable(AOServConnector connector) {
	super(connector, DNSForbiddenZone.class);
    }

    public DNSForbiddenZone get(Object pkey) {
	return getUniqueRow(DNSForbiddenZone.COLUMN_ZONE, pkey);
    }

    int getTableID() {
	return SchemaTable.DNS_FORBIDDEN_ZONES;
    }
}