/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  HttpdJKCode
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdJKCodeTable extends GlobalTableStringKey<HttpdJKCode> {

	HttpdJKCodeTable(AOServConnector connector) {
		super(connector, HttpdJKCode.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(HttpdJKCode.COLUMN_CODE_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public HttpdJKCode get(String code) throws IOException, SQLException {
		return getUniqueRow(HttpdJKCode.COLUMN_CODE, code);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_JK_CODES;
	}
}
