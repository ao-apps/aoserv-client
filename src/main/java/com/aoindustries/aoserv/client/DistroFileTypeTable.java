/*
 * Copyright 2001-2013, 2016 by AO Industries, Inc.,
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
final public class DistroFileTypeTable extends GlobalTableStringKey<DistroFileType> {

	DistroFileTypeTable(AOServConnector connector) {
		super(connector, DistroFileType.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(DistroFileType.COLUMN_TYPE_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public DistroFileType get(String type) throws IOException, SQLException {
		return getUniqueRow(DistroFileType.COLUMN_TYPE, type);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.DISTRO_FILE_TYPES;
	}
}
