/*
 * Copyright 2003-2012, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

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
final public class FileBackupSetting extends CachedObjectIntegerKey<FileBackupSetting> implements Removable {

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
	public List<CannotRemoveReason> getCannotRemoveReasons() {
		return Collections.emptyList();
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_REPLICATION: return replication;
			case 2: return path;
			case 3: return backup_enabled;
			case 4: return required;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public FailoverFileReplication getReplication() throws SQLException, IOException {
		FailoverFileReplication ffr = table.connector.getFailoverFileReplications().get(replication);
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
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.FILE_BACKUP_SETTINGS;
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
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.REMOVE,
			SchemaTable.TableID.FILE_BACKUP_SETTINGS,
			pkey
		);
	}

	public void setSettings(
		String path,
		boolean backupEnabled,
		boolean required
	) throws IOException, SQLException {
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.SET_FILE_BACKUP_SETTINGS,
			pkey,
			path,
			backupEnabled,
			required
		);
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_31)>=0) {
			out.writeCompressedInt(replication);
		} else {
			out.writeCompressedInt(-1); // server
		}
		out.writeUTF(path);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_31)>=0) {
			out.writeBoolean(backup_enabled);
		} else {
			out.writeCompressedInt(308); // package (hard-coded AOINDUSTRIES)
			out.writeShort(backup_enabled ? 1 : 0); // backup_level
			out.writeShort(7); // backup_retention
			out.writeBoolean(backup_enabled); // recurse
		}
		if(version.compareTo(AOServProtocol.Version.VERSION_1_62)>=0) {
			out.writeBoolean(required);
		}
	}
}