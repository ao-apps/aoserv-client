package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  FailoverFileLog
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverFileLogTable extends AOServTable<Integer,FailoverFileLog> {

    FailoverFileLogTable(AOServConnector connector) {
	super(connector, FailoverFileLog.class);
    }

    int addFailoverFileLog(
        FailoverFileReplication replication,
        long startTime,
        long endTime,
        int scanned,
        int updated,
        long bytes,
        boolean isSuccessful
    ) {
    	return connector.requestIntQueryIL(
            AOServProtocol.ADD,
            SchemaTable.FAILOVER_FILE_LOG,
            replication.pkey,
            startTime,
            endTime,
            scanned,
            updated,
            bytes,
            isSuccessful
	);
    }

    public FailoverFileLog get(Object pkey) {
        return get(((Integer)pkey).intValue());
    }

    public FailoverFileLog get(int pkey) {
        return getObject(AOServProtocol.GET_OBJECT, SchemaTable.FAILOVER_FILE_LOG, pkey);
    }

    public List<FailoverFileLog> getRows() {
        List<FailoverFileLog> list=new ArrayList<FailoverFileLog>();
        getObjects(list, AOServProtocol.GET_TABLE, SchemaTable.FAILOVER_FILE_LOG);
        return list;
    }

    List<FailoverFileLog> getFailoverFileLogs(FailoverFileReplication replication, int maxRows) {
        List<FailoverFileLog> list=new ArrayList<FailoverFileLog>();
        getObjectsNoProgress(list, AOServProtocol.GET_FAILOVER_FILE_LOGS_FOR_REPLICATION, replication.pkey, maxRows);
        return list;
    }

    int getTableID() {
	return SchemaTable.FAILOVER_FILE_LOG;
    }

    protected FailoverFileLog getUniqueRowImpl(int col, Object value) {
        if(col!=0) throw new IllegalArgumentException("Not a unique column: "+col);
        return get(value);
    }
}