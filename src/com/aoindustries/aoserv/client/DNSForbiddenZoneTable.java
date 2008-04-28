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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(DNSForbiddenZone.COLUMN_ZONE_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public DNSForbiddenZone get(Object pkey) {
	return getUniqueRow(DNSForbiddenZone.COLUMN_ZONE, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.DNS_FORBIDDEN_ZONES;
    }
}