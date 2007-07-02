package com.aoindustries.aoserv.client;

/*
 * Copyright 2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
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

    public WhoisHistory get(Object pkey) {
	return getUniqueRow(WhoisHistory.COLUMN_PKEY, pkey);
    }

    public WhoisHistory get(int pkey) {
	return getUniqueRow(WhoisHistory.COLUMN_PKEY, pkey);
    }

    List<WhoisHistory> getWhoisHistory(Business bu) {
        return getIndexedRows(WhoisHistory.COLUMN_ACCOUNTING, bu.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.WHOIS_HISTORY;
    }
}
