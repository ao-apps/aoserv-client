/*
 * Copyright 2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * For AO Industries use only.
 *
 * @author  AO Industries, Inc.
 */
final public class DistroReportTypeTable extends GlobalTableStringKey<DistroReportType> {

	DistroReportTypeTable(AOServConnector connector) {
		super(connector, DistroReportType.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(DistroReportType.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public DistroReportType get(String name) throws IOException, SQLException {
		return getUniqueRow(DistroReportType.COLUMN_NAME, name);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.DISTRO_REPORT_TYPES;
	}
}
