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
final public class SRNetICMPTable extends ServerReportSectionTable<SRNetICMP> {

    SRNetICMPTable(AOServConnector connector) {
	super(connector, SRNetICMP.class);
    }

    public SRNetICMP get(Object serverReport) {
        return get(((Integer)serverReport).intValue());
    }

    public SRNetICMP get(int serverReport) {
	return getUniqueRow(SRNetICMP.COLUMN_SERVER_REPORT, serverReport);
    }

    int getTableID() {
	return SchemaTable.SR_NET_ICMP;
    }
}