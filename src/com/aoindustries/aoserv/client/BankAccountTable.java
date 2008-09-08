package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
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
final public class BankAccountTable extends CachedTableStringKey<BankAccount> {

    BankAccountTable(AOServConnector connector) {
	super(connector, BankAccount.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(BankAccount.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public BankAccount get(Object name) {
	return getUniqueRow(BankAccount.COLUMN_NAME, name);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.BANK_ACCOUNTS;
    }
}