package com.aoindustries.aoserv.client;

/*
 * Copyright 2005-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.WrappedException;
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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(TransactionType.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.TRANSACTION_TYPES;
    }

    public TransactionType get(Object pkey) {
        try {
            return getUniqueRow(TransactionType.COLUMN_NAME, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }
}
