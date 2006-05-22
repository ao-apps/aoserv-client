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
final public class SRNetIPTable extends ServerReportSectionTable<SRNetIP> {

    SRNetIPTable(AOServConnector connector) {
	super(connector, SRNetIP.class);
    }

    public SRNetIP get(Object serverReport) {
        return get(((Integer)serverReport).intValue());
    }

    public SRNetIP get(int serverReport) {
	return getUniqueRow(SRNetIP.COLUMN_SERVER_REPORT, serverReport);
    }

    int getTableID() {
	return SchemaTable.SR_NET_IP;
    }
}