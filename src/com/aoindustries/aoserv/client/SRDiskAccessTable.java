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
final public class SRDiskAccessTable extends ServerReportSectionTable<SRDiskAccess> {

    SRDiskAccessTable(AOServConnector connector) {
	super(connector, SRDiskAccess.class);
    }

    public SRDiskAccess get(Object pkey) {
        return get(((Integer)pkey).intValue());
    }

    public SRDiskAccess get(int pkey) {
	return getUniqueRow(SRDiskAccess.COLUMN_PKEY, pkey);
    }

    List<SRDiskAccess> getSRDiskAccesses(ServerReport sr) {
        return getIndexedRows(SRDiskAccess.COLUMN_SERVER_REPORT, sr.pkey);
    }

    int getTableID() {
	return SchemaTable.SR_DISK_ACCESS;
    }
}