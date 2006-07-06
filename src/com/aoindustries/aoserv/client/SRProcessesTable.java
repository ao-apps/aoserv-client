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
final public class SRProcessesTable extends ServerReportSectionTable<SRProcesses> {

    SRProcessesTable(AOServConnector connector) {
	super(connector, SRProcesses.class);
    }

    public SRProcesses get(Object serverReport) {
        return get(((Integer)serverReport).intValue());
    }

    public SRProcesses get(int serverReport) {
	return getUniqueRow(SRProcesses.COLUMN_SERVER_REPORT, serverReport);
    }

    int getTableID() {
	return SchemaTable.SR_PROCESSES;
    }
}