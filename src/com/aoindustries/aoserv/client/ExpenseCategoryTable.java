package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * For AO Industries use only.
 *
 * @version  1.0a
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

    public ExpenseCategory get(Object pkey) {
	return getUniqueRow(ExpenseCategory.COLUMN_EXPENSE_CODE, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.EXPENSE_CATEGORIES;
    }
}