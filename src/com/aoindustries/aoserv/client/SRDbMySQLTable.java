package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SRDbMySQLTable extends ServerReportSectionTable<SRDbMySQL> {

    SRDbMySQLTable(AOServConnector connector) {
	super(connector, SRDbMySQL.class);
    }

    public SRDbMySQL get(Object serverReport) {
        return get(((Integer)serverReport).intValue());
    }

    public SRDbMySQL get(int serverReport) {
	return getUniqueRow(SRDbMySQL.COLUMN_SERVER_REPORT, serverReport);
    }

    int getTableID() {
	return SchemaTable.SR_DB_MYSQL;
    }
}