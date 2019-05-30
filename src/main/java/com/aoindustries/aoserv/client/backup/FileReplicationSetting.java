/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2012, 2016, 2017, 2018, 2019  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * A <code>FileBackupSetting</code> overrides the default backup behavior.
 *
 * @author  AO Industries, Inc.
 */
final public class FileReplicationSetting extends CachedObjectIntegerKey<FileReplicationSetting> implements Removable {

	static final int
		COLUMN_PKEY=0,
		COLUMN_REPLICATION=1
	;
	static final String COLUMN_REPLICATION_name = "replication";
	static final String COLUMN_PATH_name = "path";

	int replication;
	String path;
	private boolean backup_enabled;
	private boolean required;

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() {
		return Collections.emptyList();
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_REPLICATION: return replication;
			case 2: return path;
			case 3: return backup_enabled;
			case 4: return required;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public FileReplication getReplication() throws SQLException, IOException {
		FileReplication ffr = table.getConnector().getBackup().getFileReplication().get(replication);
		if(ffr==null) throw new SQLException("Unable to find FailoverFileReplication: "+replication);
		return ffr;
	}

	public String getPath() {
		return path;
	}

	public boolean getBackupEnabled() {
		return backup_enabled;
	}

	/**
	 * All required file backup settings must match in the filesystem for the
	 * backup to be considered successful.  This is used to detect missing filesystems
	 * or files during a backup pass.
	 */
	public boolean isRequired() {
		return required;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.FILE_BACKUP_SETTINGS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		replication=result.getInt(2);
		path=result.getString(3);
		backup_enabled = result.getBoolean(4);
		required = result.getBoolean(5);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		replication=in.readCompressedInt();
		path=in.readUTF();
		backup_enabled = in.readBoolean();
		required = in.readBoolean();
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(
			true,
			AoservProtocol.CommandID.REMOVE,
			Table.TableID.FILE_BACKUP_SETTINGS,
			pkey
		);
	}

	public void setSettings(
		String path,
		boolean backupEnabled,
		boolean required
	) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(
			true,
			AoservProtocol.CommandID.SET_FILE_BACKUP_SETTINGS,
			pkey,
			path,
			backupEnabled,
			required
		);
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_31)>=0) {
			out.writeCompressedInt(replication);
		} else {
			out.writeCompressedInt(-1); // server
		}
		out.writeUTF(path);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_31)>=0) {
			out.writeBoolean(backup_enabled);
		} else {
			out.writeCompressedInt(308); // package (hard-coded AOINDUSTRIES)
			out.writeShort(backup_enabled ? 1 : 0); // backup_level
			out.writeShort(7); // backup_retention
			out.writeBoolean(backup_enabled); // recurse
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_62)>=0) {
			out.writeBoolean(required);
		}
	}
}
