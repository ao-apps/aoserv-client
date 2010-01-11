/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  FailoverFileLog
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.failover_file_log)
public interface FailoverFileLogService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServServiceIntegerKey<C,F,FailoverFileLog> {

    /* TODO
    int addFailoverFileLog(
        FailoverFileReplication replication,
        long startTime,
        long endTime,
        int scanned,
        int updated,
        long bytes,
        boolean isSuccessful
    ) throws IOException, SQLException {
    	return connector.requestIntQueryIL(
            true,
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.FAILOVER_FILE_LOG,
            replication.pkey,
            startTime,
            endTime,
            scanned,
            updated,
            bytes,
            isSuccessful
	);
    }

    List<FailoverFileLog> getFailoverFileLogs(FailoverFileReplication replication, int maxRows) throws IOException, SQLException {
        List<FailoverFileLog> list=new ArrayList<FailoverFileLog>();
        getObjectsNoProgress(true, list, AOServProtocol.CommandID.GET_FAILOVER_FILE_LOGS_FOR_REPLICATION, replication.pkey, maxRows);
        return list;
    }
     */
}
