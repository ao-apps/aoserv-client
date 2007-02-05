package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * @see  ServerReport
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class ServerReportTable extends CachedTableIntegerKey<ServerReport> {

    ServerReportTable(AOServConnector connector) {
	super(connector, ServerReport.class);
    }

    public ServerReport get(Object pkey) {
	return getUniqueRow(ServerReport.COLUMN_PKEY, pkey);
    }

    public ServerReport get(int pkey) {
	return getUniqueRow(ServerReport.COLUMN_PKEY, pkey);
    }

    int getTableID() {
	return SchemaTable.SERVER_REPORTS;
    }
}