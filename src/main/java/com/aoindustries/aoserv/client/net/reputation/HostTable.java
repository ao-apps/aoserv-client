/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2012, 2016, 2017, 2018, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.net.reputation;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableLongKey;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  Host
 *
 * @author  AO Industries, Inc.
 */
public final class HostTable extends CachedTableLongKey<Host> {

	HostTable(AOServConnector connector) {
		super(connector, Host.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Host.COLUMN_SET_name+'.'+Set.COLUMN_IDENTIFIER_name, ASCENDING),
		new OrderBy(Host.COLUMN_HOST_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public Host get(long pkey) throws IOException, SQLException {
		return getUniqueRow(Host.COLUMN_PKEY, pkey);
	}

	List<Host> getHosts(Set set) throws IOException, SQLException {
		return getIndexedRows(Host.COLUMN_SET, set.getPkey());
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.IP_REPUTATION_SET_HOSTS;
	}
}
