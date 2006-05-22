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
final public class SRNetUDPTable extends ServerReportSectionTable<SRNetUDP> {

    SRNetUDPTable(AOServConnector connector) {
	super(connector, SRNetUDP.class);
    }

    public SRNetUDP get(Object serverReport) {
        return get(((Integer)serverReport).intValue());
    }

    public SRNetUDP get(int serverReport) {
	return getUniqueRow(SRNetUDP.COLUMN_SERVER_REPORT, serverReport);
    }

    int getTableID() {
	return SchemaTable.SR_NET_UDP;
    }
}