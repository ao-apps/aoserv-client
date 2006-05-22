package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  MySQLReservedWord
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLReservedWordTable extends GlobalTableStringKey<MySQLReservedWord> {

    MySQLReservedWordTable(AOServConnector connector) {
	super(connector, MySQLReservedWord.class);
    }

    public MySQLReservedWord get(Object pkey) {
	return getUniqueRow(MySQLReservedWord.COLUMN_WORD, pkey);
    }

    int getTableID() {
	return SchemaTable.MYSQL_RESERVED_WORDS;
    }
}