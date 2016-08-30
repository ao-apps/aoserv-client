/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2003-2009, 2016  AO Industries, Inc.
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
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see FailoverFileSchedule
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverFileScheduleTable extends CachedTableIntegerKey<FailoverFileSchedule> {

	FailoverFileScheduleTable(AOServConnector connector) {
		super(connector, FailoverFileSchedule.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(FailoverFileSchedule.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(FailoverFileSchedule.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
		new OrderBy(FailoverFileSchedule.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_BACKUP_PARTITION_name+'.'+BackupPartition.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(FailoverFileSchedule.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_BACKUP_PARTITION_name+'.'+BackupPartition.COLUMN_PATH_name, ASCENDING),
		new OrderBy(FailoverFileSchedule.COLUMN_HOUR_name, ASCENDING),
		new OrderBy(FailoverFileSchedule.COLUMN_MINUTE_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	List<FailoverFileSchedule> getFailoverFileSchedules(FailoverFileReplication replication) throws IOException, SQLException {
		return getIndexedRows(FailoverFileSchedule.COLUMN_REPLICATION, replication.pkey);
	}

	@Override
	public FailoverFileSchedule get(int pkey) throws IOException, SQLException {
		return getUniqueRow(FailoverFileSchedule.COLUMN_PKEY, pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.FAILOVER_FILE_SCHEDULE;
	}

	void setFailoverFileSchedules(final FailoverFileReplication ffr, final List<Short> hours, final List<Short> minutes) throws IOException, SQLException {
		if(hours.size()!=minutes.size()) throw new IllegalArgumentException("hours.size()!=minutes.size(): "+hours.size()+"!="+minutes.size());

		connector.requestUpdate(
			true,
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(AOServProtocol.CommandID.SET_FAILOVER_FILE_SCHEDULES.ordinal());
					out.writeCompressedInt(ffr.getPkey());
					int size = hours.size();
					out.writeCompressedInt(size);
					for(int c=0;c<size;c++) {
						out.writeShort(hours.get(c));
						out.writeShort(minutes.get(c));
					}
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AOServProtocol.DONE) {
						invalidateList=AOServConnector.readInvalidateList(in);
					} else {
						AOServProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public void afterRelease() {
					connector.tablesUpdated(invalidateList);
				}
			}
		);
	}
}
