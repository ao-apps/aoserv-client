package com.aoindustries.aoserv.client;

/*
 * Copyright 2005-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  TransactionType
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class TransactionTypeTable extends GlobalTableStringKey<TransactionType> {

    TransactionTypeTable(AOServConnector connector) {
	super(connector, TransactionType.class);
    }

    int getTableID() {
	return SchemaTable.TRANSACTION_TYPES;
    }

    public TransactionType get(Object pkey) {
	return getUniqueRow(TransactionType.COLUMN_NAME, pkey);
    }
}
