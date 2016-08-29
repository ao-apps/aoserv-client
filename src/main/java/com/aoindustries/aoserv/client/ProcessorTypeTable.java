/*
 * Copyright 2008-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * The table containing all of the possible processor types.
 *
 * @author  AO Industries, Inc.
 */
final public class ProcessorTypeTable extends GlobalTableStringKey<ProcessorType> {

	ProcessorTypeTable(AOServConnector connector) {
		super(connector, ProcessorType.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(ProcessorType.COLUMN_SORT_ORDER_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public ProcessorType get(String type) throws IOException, SQLException {
		return getUniqueRow(ProcessorType.COLUMN_TYPE, type);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.PROCESSOR_TYPES;
	}
}
