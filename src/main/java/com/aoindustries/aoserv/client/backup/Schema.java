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

	private final BackupPartitionTable BackupPartition;
	public BackupPartitionTable getBackupPartition() {return BackupPartition;}

	private final BackupReportTable BackupReport;
	public BackupReportTable getBackupReport() {return BackupReport;}

	private final BackupRetentionTable BackupRetention;
	public BackupRetentionTable getBackupRetention() {return BackupRetention;}

	private final FileReplicationTable FileReplication;
	public FileReplicationTable getFileReplication() {return FileReplication;}

	private final FileReplicationLogTable FileReplicationLog;
	public FileReplicationLogTable getFileReplicationLog() {return FileReplicationLog;}

	private final FileReplicationScheduleTable FileReplicationSchedule;
	public FileReplicationScheduleTable getFileReplicationSchedule() {return FileReplicationSchedule;}

	private final FileReplicationSettingTable FileReplicationSetting;
	public FileReplicationSettingTable getFileReplicationSetting() {return FileReplicationSetting;}

	private final MysqlReplicationTable MysqlReplication;
	public MysqlReplicationTable getMysqlReplication() {return MysqlReplication;}

	final List<? extends AOServTable<?,?>> tables;

	public Schema(AOServConnector connector) throws IOException {
		super(connector);

		ArrayList<AOServTable<?,?>> newTables = new ArrayList<>();
		newTables.add(BackupPartition = new BackupPartitionTable(connector));
		newTables.add(BackupReport = new BackupReportTable(connector));
		newTables.add(BackupRetention = new BackupRetentionTable(connector));
		newTables.add(FileReplication = new FileReplicationTable(connector));
		newTables.add(FileReplicationLog = new FileReplicationLogTable(connector));
		newTables.add(FileReplicationSchedule = new FileReplicationScheduleTable(connector));
		newTables.add(FileReplicationSetting = new FileReplicationSettingTable(connector));
		newTables.add(MysqlReplication = new MysqlReplicationTable(connector));
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
