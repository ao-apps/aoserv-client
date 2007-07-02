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
 * @see  InterBaseReservedWord
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class InterBaseReservedWordTable extends GlobalTableStringKey<InterBaseReservedWord> {

    InterBaseReservedWordTable(AOServConnector connector) {
	super(connector, InterBaseReservedWord.class);
    }

    public InterBaseReservedWord get(Object pkey) {
	return getUniqueRow(InterBaseReservedWord.COLUMN_WORD, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.INTERBASE_RESERVED_WORDS;
    }
}