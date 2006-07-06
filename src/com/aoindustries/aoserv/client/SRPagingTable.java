package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SRPagingTable extends ServerReportSectionTable<SRPaging> {

    SRPagingTable(AOServConnector connector) {
	super(connector, SRPaging.class);
    }

    public SRPaging get(Object serverReport) {
        return get(((Integer)serverReport).intValue());
    }

    public SRPaging get(int serverReport) {
	return getUniqueRow(SRPaging.COLUMN_SERVER_REPORT, serverReport);
    }

    int getTableID() {
	return SchemaTable.SR_PAGING;
    }
}