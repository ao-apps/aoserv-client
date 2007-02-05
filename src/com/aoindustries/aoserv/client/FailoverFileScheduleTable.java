package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see FailoverFileSchedule
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverFileScheduleTable extends CachedTableIntegerKey<FailoverFileSchedule> {

    FailoverFileScheduleTable(AOServConnector connector) {
	super(connector, FailoverFileSchedule.class);
    }

    List<FailoverFileSchedule> getFailoverFileSchedules(FailoverFileReplication replication) {
        return getIndexedRows(FailoverFileSchedule.COLUMN_REPLICATION, replication.pkey);
    }

    public FailoverFileSchedule get(Object pkey) {
	return getUniqueRow(FailoverFileSchedule.COLUMN_PKEY, pkey);
    }

    public FailoverFileSchedule get(int pkey) {
	return getUniqueRow(FailoverFileSchedule.COLUMN_PKEY, pkey);
    }

    int getTableID() {
	return SchemaTable.FAILOVER_FILE_SCHEDULE;
    }
}