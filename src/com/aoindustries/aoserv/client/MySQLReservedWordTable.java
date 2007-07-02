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

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MYSQL_RESERVED_WORDS;
    }
}