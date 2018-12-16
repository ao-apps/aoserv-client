/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2018  AO Industries, Inc.
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author  AO Industries, Inc.
 */
public class Schema extends com.aoindustries.aoserv.client.Schema {

	private final BackupPartitionTable backupPartitionTable;
	public BackupPartitionTable getBackupPartitions() {return backupPartitionTable;}

	private final BackupReportTable backupReportTable;
	public BackupReportTable getBackupReports() {return backupReportTable;}

	private final BackupRetentionTable backupRetentionTable;
	public BackupRetentionTable getBackupRetentions() {return backupRetentionTable;}

	private final FileReplicationTable fileReplicationTable;
	public FileReplicationTable getFailoverFileReplications() {return fileReplicationTable;}

	private final FileReplicationLogTable fileReplicationLogTable;
	public FileReplicationLogTable getFailoverFileLogs() {return fileReplicationLogTable;}

	private final FileReplicationScheduleTable fileReplicationScheduleTable;
	public FileReplicationScheduleTable getFailoverFileSchedules() {return fileReplicationScheduleTable;}

	private final FileReplicationSettingTable fileReplicationSettingTable;
	public FileReplicationSettingTable getFileBackupSettings() {return fileReplicationSettingTable;}

	private final MysqlReplicationTable mysqlReplicationTable;
	public MysqlReplicationTable getFailoverMySQLReplications() {return mysqlReplicationTable;}

	final List<? extends AOServTable<?,?>> tables;

	public Schema(AOServConnector connector) throws IOException {
		super(connector);

		ArrayList<AOServTable<?,?>> newTables = new ArrayList<>();
		newTables.add(backupPartitionTable = new BackupPartitionTable(connector));
		newTables.add(backupReportTable = new BackupReportTable(connector));
		newTables.add(backupRetentionTable = new BackupRetentionTable(connector));
		newTables.add(fileReplicationTable = new FileReplicationTable(connector));
		newTables.add(fileReplicationLogTable = new FileReplicationLogTable(connector));
		newTables.add(fileReplicationScheduleTable = new FileReplicationScheduleTable(connector));
		newTables.add(fileReplicationSettingTable = new FileReplicationSettingTable(connector));
		newTables.add(mysqlReplicationTable = new MysqlReplicationTable(connector));
		newTables.trimToSize();
		tables = Collections.unmodifiableList(newTables);
	}

	@Override
	public List<? extends AOServTable<?,?>> getTables() {
		return tables;
	}

	@Override
	public String getName() {
		return "backup";
	}
}
