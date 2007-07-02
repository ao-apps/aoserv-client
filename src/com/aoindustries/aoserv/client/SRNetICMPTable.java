package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
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

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SR_NET_ICMP;
    }
}