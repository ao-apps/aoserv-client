package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SRSwapRateTable extends ServerReportSectionTable<SRSwapRate> {

    SRSwapRateTable(AOServConnector connector) {
	super(connector, SRSwapRate.class);
    }

    public SRSwapRate get(Object serverReport) {
        return get(((Integer)serverReport).intValue());
    }

    public SRSwapRate get(int serverReport) {
	return getUniqueRow(SRSwapRate.COLUMN_SERVER_REPORT, serverReport);
    }

    int getTableID() {
	return SchemaTable.SR_SWAP_RATE;
    }
}