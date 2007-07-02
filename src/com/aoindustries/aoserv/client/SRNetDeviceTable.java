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
final public class SRNetDeviceTable extends ServerReportSectionTable<SRNetDevice> {

    SRNetDeviceTable(AOServConnector connector) {
	super(connector, SRNetDevice.class);
    }

    public SRNetDevice get(Object pkey) {
        return get(((Integer)pkey).intValue());
    }

    public SRNetDevice get(int pkey) {
	return getUniqueRow(SRNetDevice.COLUMN_PKEY, pkey);
    }

    List<SRNetDevice> getSRNetDevices(ServerReport sr) {
        return getIndexedRows(SRNetDevice.COLUMN_SERVER_REPORT, sr.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SR_NET_DEVICES;
    }
}