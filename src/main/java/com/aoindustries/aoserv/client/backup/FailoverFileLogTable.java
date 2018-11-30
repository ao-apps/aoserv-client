/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.backup;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServTable;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.AOServer;
import com.aoindustries.aoserv.client.net.Server;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
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

	public FailoverFileLogTable(AOServConnector connector) {
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
	protected OrderBy[] getDefaultOrderBy() {
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
			replication.getPkey(),
			startTime,
			endTime,
			scanned,
			updated,
			bytes,
			isSuccessful
		);
	}

	/**
	 * @deprecated  Always try to lookup by specific keys; the compiler will help you more when types change.
	 */
	@Deprecated
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
		getObjectsNoProgress(true, list, AOServProtocol.CommandID.GET_FAILOVER_FILE_LOGS_FOR_REPLICATION, replication.getPkey(), maxRows);
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
