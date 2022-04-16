/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2008, 2009, 2016, 2017, 2018, 2020, 2021, 2022  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoindustries.aoserv.client.infrastructure;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.GlobalTableStringKey;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;

/**
 * The table containing all of the possible processor types.
 *
 * @author  AO Industries, Inc.
 */
public final class ProcessorTypeTable extends GlobalTableStringKey<ProcessorType> {

	ProcessorTypeTable(AOServConnector connector) {
		super(connector, ProcessorType.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(ProcessorType.COLUMN_SORT_ORDER_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public ProcessorType get(String type) throws IOException, SQLException {
		return getUniqueRow(ProcessorType.COLUMN_TYPE, type);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.PROCESSOR_TYPES;
	}
}
