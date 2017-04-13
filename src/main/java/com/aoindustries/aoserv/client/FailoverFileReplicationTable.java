/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2009, 2015, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.io.TerminalWriter;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see FailoverFileReplication
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverFileReplicationTable extends CachedTableIntegerKey<FailoverFileReplication> {

	FailoverFileReplicationTable(AOServConnector connector) {
		super(connector, FailoverFileReplication.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(FailoverFileReplication.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(FailoverFileReplication.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
		new OrderBy(FailoverFileReplication.COLUMN_BACKUP_PARTITION_name+'.'+BackupPartition.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(FailoverFileReplication.COLUMN_BACKUP_PARTITION_name+'.'+BackupPartition.COLUMN_PATH_name, ASCENDING)
	};

	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	List<FailoverFileReplication> getFailoverFileReplications(Server server) throws IOException, SQLException {
		return getIndexedRows(FailoverFileReplication.COLUMN_SERVER, server.pkey);
	}

	@Override
	public FailoverFileReplication get(int pkey) throws IOException, SQLException {
		return getUniqueRow(FailoverFileReplication.COLUMN_PKEY, pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.FAILOVER_FILE_REPLICATIONS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.GET_FAILOVER_FILE_REPLICATION_ACTIVITY)) {
			if(AOSH.checkParamCount(AOSHCommand.GET_FAILOVER_FILE_REPLICATION_ACTIVITY, args, 3, err)) {
				FailoverFileReplication.Activity activity = connector.getSimpleAOClient().getFailoverFileReplicationActivity(
					args[1],
					args[2],
					args[3]
				);
				long timeSince = activity.getTimeSince();
				if(timeSince == -1) out.println("No activity available");
				else {
					out.println(StringUtility.getDecimalTimeLengthString(timeSince));
					out.println(activity.getMessage());
				}
				out.flush();
			}
			return true;
		}
		return false;
	}
}
