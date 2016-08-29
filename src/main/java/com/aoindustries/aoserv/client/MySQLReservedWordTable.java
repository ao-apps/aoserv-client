/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  MySQLReservedWord
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLReservedWordTable extends GlobalTableStringKey<MySQLReservedWord> {

	MySQLReservedWordTable(AOServConnector connector) {
		super(connector, MySQLReservedWord.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(MySQLReservedWord.COLUMN_WORD_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public MySQLReservedWord get(String word) throws IOException, SQLException {
		return getUniqueRow(MySQLReservedWord.COLUMN_WORD, word);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.MYSQL_RESERVED_WORDS;
	}
}
