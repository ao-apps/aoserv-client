package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.util.List;

/**
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SRSwapSizeTable extends ServerReportSectionTable<SRSwapSize> {

    SRSwapSizeTable(AOServConnector connector) {
	super(connector, SRSwapSize.class);
    }

    public SRSwapSize get(Object pkey) {
        return get(((Integer)pkey).intValue());
    }

    public SRSwapSize get(int pkey) {
	return getUniqueRow(SRSwapSize.COLUMN_PKEY, pkey);
    }

    List<SRSwapSize> getSRSwapSizes(ServerReport sr) {
        return getIndexedRows(SRSwapSize.COLUMN_SERVER_REPORT, sr.pkey);
    }

    int getTableID() {
	return SchemaTable.SR_SWAP_SIZE;
    }
}