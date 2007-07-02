package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * @see  PostgresReservedWord
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresReservedWordTable extends GlobalTableStringKey<PostgresReservedWord> {

    PostgresReservedWordTable(AOServConnector connector) {
	super(connector, PostgresReservedWord.class);
    }

    public PostgresReservedWord get(Object pkey) {
	return getUniqueRow(PostgresReservedWord.COLUMN_WORD, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.POSTGRES_RESERVED_WORDS;
    }
}