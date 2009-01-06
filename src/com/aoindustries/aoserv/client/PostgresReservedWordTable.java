package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(PostgresReservedWord.COLUMN_WORD_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public PostgresReservedWord get(Object pkey) {
	return getUniqueRow(PostgresReservedWord.COLUMN_WORD, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.POSTGRES_RESERVED_WORDS;
    }
}