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
final public class SRKernelTable extends ServerReportSectionTable<SRKernel> {

    SRKernelTable(AOServConnector connector) {
	super(connector, SRKernel.class);
    }

    public SRKernel get(Object serverReport) {
        return get(((Integer)serverReport).intValue());
    }

    public SRKernel get(int serverReport) {
	return getUniqueRow(SRKernel.COLUMN_SERVER_REPORT, serverReport);
    }

    int getTableID() {
	return SchemaTable.SR_KERNEL;
    }
}