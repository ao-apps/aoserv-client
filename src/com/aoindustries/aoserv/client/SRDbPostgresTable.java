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
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SRDbPostgresTable extends ServerReportSectionTable<SRDbPostgres> {

    SRDbPostgresTable(AOServConnector connector) {
	super(connector, SRDbPostgres.class);
    }

    public SRDbPostgres get(Object serverReport) {
        return get(((Integer)serverReport).intValue());
    }

    public SRDbPostgres get(int serverReport) {
	return getUniqueRow(SRDbPostgres.COLUMN_SERVER_REPORT, serverReport);
    }

    int getTableID() {
	return SchemaTable.SR_DB_POSTGRES;
    }
}