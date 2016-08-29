/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * For AO Industries use only.
 *
 * @author  AO Industries, Inc.
 */
final public class ExpenseCategoryTable extends CachedTableStringKey<ExpenseCategory> {

	ExpenseCategoryTable(AOServConnector connector) {
		super(connector, ExpenseCategory.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(ExpenseCategory.COLUMN_EXPENSE_CODE_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public ExpenseCategory get(String expense_code) throws IOException, SQLException {
		return getUniqueRow(ExpenseCategory.COLUMN_EXPENSE_CODE, expense_code);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.EXPENSE_CATEGORIES;
	}
}
