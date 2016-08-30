/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2008-2009, 2016  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
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
