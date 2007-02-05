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
final public class SRCpuTable extends ServerReportSectionTable<SRCpu> {

    SRCpuTable(AOServConnector connector) {
	super(connector, SRCpu.class);
    }

    public SRCpu get(Object pkey) {
        return get(((Integer)pkey).intValue());
    }

    public SRCpu get(int pkey) {
	return getUniqueRow(SRCpu.COLUMN_PKEY, pkey);
    }

    List<SRCpu> getSRCpus(ServerReport sr) {
        return getIndexedRows(SRCpu.COLUMN_SERVER_REPORT, sr.pkey);
    }

    int getTableID() {
	return SchemaTable.SR_CPU;
    }
}