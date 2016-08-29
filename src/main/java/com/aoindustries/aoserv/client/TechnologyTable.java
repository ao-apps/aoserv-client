/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  Technology
 *
 * @author  AO Industries, Inc.
 */
final public class TechnologyTable extends GlobalTableIntegerKey<Technology> {

	TechnologyTable(AOServConnector connector) {
		super(connector, Technology.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Technology.COLUMN_NAME_name, ASCENDING),
		new OrderBy(Technology.COLUMN_CLASS_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.TECHNOLOGIES;
	}

	@Override
	public Technology get(int pkey) throws IOException, SQLException {
		return getUniqueRow(Technology.COLUMN_PKEY, pkey);
	}

	List<Technology> getTechnologies(TechnologyName techName) throws IOException, SQLException {
		return getIndexedRows(Technology.COLUMN_NAME, techName.pkey);
	}
}
