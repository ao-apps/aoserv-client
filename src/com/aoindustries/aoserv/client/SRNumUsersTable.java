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
final public class SRNumUsersTable extends ServerReportSectionTable<SRNumUsers> {

    SRNumUsersTable(AOServConnector connector) {
	super(connector, SRNumUsers.class);
    }

    public SRNumUsers get(Object serverReport) {
        return get(((Integer)serverReport).intValue());
    }

    public SRNumUsers get(int serverReport) {
	return getUniqueRow(SRNumUsers.COLUMN_SERVER_REPORT, serverReport);
    }

    int getTableID() {
	return SchemaTable.SR_NUM_USERS;
    }
}