package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
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