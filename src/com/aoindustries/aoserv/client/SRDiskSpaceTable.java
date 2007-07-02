package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SRDiskSpaceTable extends ServerReportSectionTable<SRDiskSpace> {

    SRDiskSpaceTable(AOServConnector connector) {
	super(connector, SRDiskSpace.class);
    }

    public SRDiskSpace get(Object pkey) {
        return get(((Integer)pkey).intValue());
    }

    public SRDiskSpace get(int pkey) {
	return getUniqueRow(SRDiskSpace.COLUMN_PKEY, pkey);
    }

    List<SRDiskSpace> getSRDiskSpaces(ServerReport sr) {
        return getIndexedRows(SRDiskSpace.COLUMN_SERVER_REPORT, sr.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SR_DISK_SPACE;
    }
}