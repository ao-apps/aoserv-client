package com.aoindustries.aoserv.client;

/*
 * Copyright 2007-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  WhoisHistory
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class WhoisHistoryTable extends CachedTableIntegerKey<WhoisHistory> {

    WhoisHistoryTable(AOServConnector connector) {
	super(connector, WhoisHistory.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(WhoisHistory.COLUMN_ACCOUNTING_name, ASCENDING),
        new OrderBy(WhoisHistory.COLUMN_ZONE_name, ASCENDING),
        new OrderBy(WhoisHistory.COLUMN_TIME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public WhoisHistory get(Object pkey) {
	return getUniqueRow(WhoisHistory.COLUMN_PKEY, pkey);
    }

    public WhoisHistory get(int pkey) {
	return getUniqueRow(WhoisHistory.COLUMN_PKEY, pkey);
    }

    @Override
    public List<WhoisHistory> getIndexedRows(int col, Object value) {
        if(col==WhoisHistory.COLUMN_WHOIS_OUTPUT) throw new UnsupportedOperationException("getIndexedRows not supported for whois_history.whois_output");
        return super.getIndexedRows(col, value);
    }

    List<WhoisHistory> getWhoisHistory(Business bu) {
        return getIndexedRows(WhoisHistory.COLUMN_ACCOUNTING, bu.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.WHOIS_HISTORY;
    }
}
