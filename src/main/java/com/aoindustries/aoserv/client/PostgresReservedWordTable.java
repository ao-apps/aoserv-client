/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  PostgresReservedWord
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresReservedWordTable extends GlobalTableStringKey<PostgresReservedWord> {

	PostgresReservedWordTable(AOServConnector connector) {
		super(connector, PostgresReservedWord.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(PostgresReservedWord.COLUMN_WORD_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public PostgresReservedWord get(String word) throws IOException, SQLException {
		return getUniqueRow(PostgresReservedWord.COLUMN_WORD, word);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.POSTGRES_RESERVED_WORDS;
	}
}
