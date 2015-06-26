/*
 * Copyright 2003-2009, 2015 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
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
