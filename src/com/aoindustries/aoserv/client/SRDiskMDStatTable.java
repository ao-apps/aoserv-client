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
final public class SRDiskMDStatTable extends ServerReportSectionTable<SRDiskMDStat> {

    SRDiskMDStatTable(AOServConnector connector) {
	super(connector, SRDiskMDStat.class);
    }

    public SRDiskMDStat get(Object pkey) {
        return get(((Integer)pkey).intValue());
    }

    public SRDiskMDStat get(int pkey) {
	return getUniqueRow(SRDiskMDStat.COLUMN_PKEY, pkey);
    }

    List<SRDiskMDStat> getSRDiskMDStats(ServerReport sr) {
        return getIndexedRows(SRDiskMDStat.COLUMN_SERVER_REPORT, sr.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SR_DISK_MDSTAT;
    }
}