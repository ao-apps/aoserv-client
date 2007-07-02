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

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SR_NET_IP;
    }
}