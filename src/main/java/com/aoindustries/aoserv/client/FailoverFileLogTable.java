/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  FailoverFileLog
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverFileLogTable extends AOServTable<Integer,FailoverFileLog> {

	FailoverFileLogTable(AOServConnector connector) {
		super(connector, FailoverFileLog.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(FailoverFileLog.COLUMN_END_TIME_name, DESCENDING),
		new OrderBy(FailoverFileLog.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(FailoverFileLog.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
		new OrderBy(FailoverFileLog.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_BACKUP_PARTITION_name+'.'+BackupPartition.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(FailoverFileLog.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_BACKUP_PARTITION_name+'.'+BackupPartition.COLUMN_PATH_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

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

	@Override
	public FailoverFileLog get(Object pkey) throws IOException, SQLException {
		return get(((Integer)pkey).intValue());
	}

	public FailoverFileLog get(int pkey) throws IOException, SQLException {
		return getObject(true, AOServProtocol.CommandID.GET_OBJECT, SchemaTable.TableID.FAILOVER_FILE_LOG, pkey);
	}

	@Override
	public List<FailoverFileLog> getRows() throws IOException, SQLException {
		List<FailoverFileLog> list=new ArrayList<>();
		getObjects(true, list, AOServProtocol.CommandID.GET_TABLE, SchemaTable.TableID.FAILOVER_FILE_LOG);
		return list;
	}

	List<FailoverFileLog> getFailoverFileLogs(FailoverFileReplication replication, int maxRows) throws IOException, SQLException {
		List<FailoverFileLog> list=new ArrayList<>();
		getObjectsNoProgress(true, list, AOServProtocol.CommandID.GET_FAILOVER_FILE_LOGS_FOR_REPLICATION, replication.pkey, maxRows);
		return list;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.FAILOVER_FILE_LOG;
	}

	@Override
	protected FailoverFileLog getUniqueRowImpl(int col, Object value) throws IOException, SQLException {
		if(col!=0) throw new IllegalArgumentException("Not a unique column: "+col);
		return get(value);
	}
}