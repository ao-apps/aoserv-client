/*
 * Copyright 2003-2012, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  FileBackupSettingTable
 *
 * @author  AO Industries, Inc.
 */
final public class FileBackupSettingTable extends CachedTableIntegerKey<FileBackupSetting> {

	FileBackupSettingTable(AOServConnector connector) {
		super(connector, FileBackupSetting.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(FileBackupSetting.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(FileBackupSetting.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
		new OrderBy(FileBackupSetting.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_BACKUP_PARTITION_name+'.'+BackupPartition.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(FileBackupSetting.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_BACKUP_PARTITION_name+'.'+BackupPartition.COLUMN_PATH_name, ASCENDING),
		new OrderBy(FileBackupSetting.COLUMN_PATH_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addFileBackupSetting(FailoverFileReplication replication, String path, boolean backupEnabled, boolean required) throws IOException, SQLException {
		return connector.requestIntQueryIL(
			true,
			AOServProtocol.CommandID.ADD,
			SchemaTable.TableID.FILE_BACKUP_SETTINGS,
			replication.pkey,
			path,
			backupEnabled,
			required
		);
	}

	@Override
	public FileBackupSetting get(int pkey) throws IOException, SQLException {
		return getUniqueRow(FileBackupSetting.COLUMN_PKEY, pkey);
	}

	FileBackupSetting getFileBackupSetting(FailoverFileReplication ffr, String path) throws IOException, SQLException {
		// Use index first
		for(FileBackupSetting fbs : getFileBackupSettings(ffr)) if(fbs.path.equals(path)) return fbs;
		return null;
	}

	List<FileBackupSetting> getFileBackupSettings(FailoverFileReplication ffr) throws IOException, SQLException {
		return getIndexedRows(FileBackupSetting.COLUMN_REPLICATION, ffr.pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.FILE_BACKUP_SETTINGS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_FILE_BACKUP_SETTING)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_FILE_BACKUP_SETTING, args, 4, err)) {
				out.println(
					connector.getSimpleAOClient().addFileBackupSetting(
						AOSH.parseInt(args[1], "replication"),
						args[2],
						AOSH.parseBoolean(args[3], "backup_enabled"),
						AOSH.parseBoolean(args[4], "required")
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_FILE_BACKUP_SETTING)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_FILE_BACKUP_SETTING, args, 2, err)) {
				connector.getSimpleAOClient().removeFileBackupSetting(
					AOSH.parseInt(args[1], "replication"),
					args[2]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_FILE_BACKUP_SETTING)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_FILE_BACKUP_SETTING, args, 4, err)) {
				connector.getSimpleAOClient().setFileBackupSetting(
					AOSH.parseInt(args[1], "replication"),
					args[2],
					AOSH.parseBoolean(args[3], "backup_enabled"),
					AOSH.parseBoolean(args[4], "required")
				);
			}
			return true;
		}
		return false;
	}

	void setFileBackupSettings(
		final FailoverFileReplication ffr,
		final List<String> paths,
		final List<Boolean> backupEnableds,
		final List<Boolean> requireds
	) throws IOException, SQLException {
		if(paths.size()!=backupEnableds.size()) throw new IllegalArgumentException("paths.size()!=backupEnableds.size(): "+paths.size()+"!="+backupEnableds.size());
		if(paths.size()!=requireds.size()) throw new IllegalArgumentException("paths.size()!=requireds.size(): "+paths.size()+"!="+requireds.size());

		connector.requestUpdate(
			true,
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(AOServProtocol.CommandID.SET_FILE_BACKUP_SETTINGS_ALL_AT_ONCE.ordinal());
					out.writeCompressedInt(ffr.getPkey());
					int size = paths.size();
					out.writeCompressedInt(size);
					for(int c=0;c<size;c++) {
						out.writeUTF(paths.get(c));
						out.writeBoolean(backupEnableds.get(c));
						out.writeBoolean(requireds.get(c));
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
