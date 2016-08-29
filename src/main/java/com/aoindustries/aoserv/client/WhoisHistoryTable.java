/*
 * Copyright 2007-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
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
