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
final public class SRMemoryTable extends ServerReportSectionTable<SRMemory> {

    SRMemoryTable(AOServConnector connector) {
	super(connector, SRMemory.class);
    }

    public SRMemory get(Object serverReport) {
        return get(((Integer)serverReport).intValue());
    }

    public SRMemory get(int serverReport) {
	return getUniqueRow(SRMemory.COLUMN_SERVER_REPORT, serverReport);
    }

    int getTableID() {
	return SchemaTable.SR_MEMORY;
    }
}