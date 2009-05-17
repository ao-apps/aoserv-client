package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;

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

    public DNSForbiddenZone get(String zone) throws IOException, SQLException {
        return getUniqueRow(DNSForbiddenZone.COLUMN_ZONE, zone);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.DNS_FORBIDDEN_ZONES;
    }
}