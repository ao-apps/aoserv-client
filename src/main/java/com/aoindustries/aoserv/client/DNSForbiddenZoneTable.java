/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  DNSForbiddenZone
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

	@Override
	public DNSForbiddenZone get(String zone) throws IOException, SQLException {
		return getUniqueRow(DNSForbiddenZone.COLUMN_ZONE, zone);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.DNS_FORBIDDEN_ZONES;
	}
}
