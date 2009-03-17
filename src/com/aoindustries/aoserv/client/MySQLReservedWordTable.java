package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.WrappedException;
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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(MySQLReservedWord.COLUMN_WORD_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public MySQLReservedWord get(Object pkey) {
        try {
            return getUniqueRow(MySQLReservedWord.COLUMN_WORD, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MYSQL_RESERVED_WORDS;
    }
}