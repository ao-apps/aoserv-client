/*
 * Copyright 2005-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  TransactionType
 *
 * @author  AO Industries, Inc.
 */
public final class TransactionTypeTable extends GlobalTableStringKey<TransactionType> {

	TransactionTypeTable(AOServConnector connector) {
		super(connector, TransactionType.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(TransactionType.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.TRANSACTION_TYPES;
	}

	@Override
	public TransactionType get(String name) throws IOException, SQLException {
		return getUniqueRow(TransactionType.COLUMN_NAME, name);
	}
}
