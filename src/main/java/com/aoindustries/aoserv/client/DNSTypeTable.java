/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  DNSType
 *
 * @author  AO Industries, Inc.
 */
final public class DNSTypeTable extends GlobalTableStringKey<DNSType> {

	DNSTypeTable(AOServConnector connector) {
		super(connector, DNSType.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(DNSType.COLUMN_DESCRIPTION_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public DNSType get(String type) throws IOException, SQLException {
		return getUniqueRow(DNSType.COLUMN_TYPE, type);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.DNS_TYPES;
	}
}
