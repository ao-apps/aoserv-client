package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
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

    public Bank get(Object name) {
	return getUniqueRow(Bank.COLUMN_NAME, name);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.BANKS;
    }
}