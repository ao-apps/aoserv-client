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
final public class BankTransactionTypeTable extends CachedTableStringKey<BankTransactionType> {

    BankTransactionTypeTable(AOServConnector connector) {
	super(connector, BankTransactionType.class);
    }

    public BankTransactionType get(Object name) {
	return getUniqueRow(BankTransactionType.COLUMN_NAME, name);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.BANK_TRANSACTION_TYPES;
    }
}