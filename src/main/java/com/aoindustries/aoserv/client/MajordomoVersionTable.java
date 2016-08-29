/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  MajordomoVersion
 *
 * @author  AO Industries, Inc.
 */
final public class MajordomoVersionTable extends GlobalTableStringKey<MajordomoVersion> {

	MajordomoVersionTable(AOServConnector connector) {
		super(connector, MajordomoVersion.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(MajordomoVersion.COLUMN_VERSION_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public MajordomoVersion get(String version) throws IOException, SQLException {
		return getUniqueRow(MajordomoVersion.COLUMN_VERSION, version);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.MAJORDOMO_VERSIONS;
	}
}
