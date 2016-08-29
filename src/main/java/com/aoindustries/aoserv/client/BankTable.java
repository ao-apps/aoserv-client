package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;

/**
 * For AO Industries use only.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class BankTable extends CachedTableStringKey<Bank> {

    BankTable(AOServConnector connector) {
	super(connector, Bank.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(Bank.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public Bank get(String name) throws IOException, SQLException {
        return getUniqueRow(Bank.COLUMN_NAME, name);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.BANKS;
    }
}