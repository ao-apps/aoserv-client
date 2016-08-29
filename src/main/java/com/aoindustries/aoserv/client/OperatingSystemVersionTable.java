/*
 * Copyright 2003-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * All of the operating system versions referenced from other tables.
 *
 * @see OperatingSystemVersion
 *
 * @author  AO Industries, Inc.
 */
final public class OperatingSystemVersionTable extends GlobalTableIntegerKey<OperatingSystemVersion> {

	OperatingSystemVersionTable(AOServConnector connector) {
		super(connector, OperatingSystemVersion.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(OperatingSystemVersion.COLUMN_SORT_ORDER_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	OperatingSystemVersion getOperatingSystemVersion(OperatingSystem os, String version, Architecture architecture) throws IOException, SQLException {
		String name=os.pkey;
		String arch=architecture.pkey;
		for(OperatingSystemVersion osv : getRows()) {
			if(
				osv.version_name.equals(name)
				&& osv.version_number.equals(version)
				&& osv.architecture.equals(arch)
			) return osv;
		}
		return null;
	}

	@Override
	public OperatingSystemVersion get(int pkey) throws IOException, SQLException {
		return getUniqueRow(OperatingSystemVersion.COLUMN_PKEY, pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.OPERATING_SYSTEM_VERSIONS;
	}
}
