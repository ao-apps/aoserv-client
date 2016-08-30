/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2007-2013, 2016  AO Industries, Inc.
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
import java.util.List;

/**
 * @see  WhoisHistory
 *
 * @author  AO Industries, Inc.
 */
final public class WhoisHistoryTable extends CachedTableIntegerKey<WhoisHistory> {

	WhoisHistoryTable(AOServConnector connector) {
		super(connector, WhoisHistory.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(WhoisHistory.COLUMN_ACCOUNTING_name, ASCENDING),
		new OrderBy(WhoisHistory.COLUMN_ZONE_name, ASCENDING),
		new OrderBy(WhoisHistory.COLUMN_TIME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public WhoisHistory get(int pkey) throws IOException, SQLException {
		return getUniqueRow(WhoisHistory.COLUMN_PKEY, pkey);
	}

	@Override
	public List<WhoisHistory> getIndexedRows(int col, Object value) throws IOException, SQLException {
		if(col==WhoisHistory.COLUMN_WHOIS_OUTPUT) throw new UnsupportedOperationException("getIndexedRows not supported for whois_history.whois_output");
		return super.getIndexedRows(col, value);
	}

	List<WhoisHistory> getWhoisHistory(Business bu) throws IOException, SQLException {
		return getIndexedRows(WhoisHistory.COLUMN_ACCOUNTING, bu.pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.WHOIS_HISTORY;
	}
}
