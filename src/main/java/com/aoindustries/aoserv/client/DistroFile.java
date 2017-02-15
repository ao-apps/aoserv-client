/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2000-2013, 2016, 2017  AO Industries, Inc.
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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * For AO Industries use only.
 *
 * @author  AO Industries, Inc.
 */
final public class DistroFile extends FilesystemCachedObject<Integer,DistroFile> {

	static final int COLUMN_PKEY=0;
	public static final int COLUMN_OPERATING_SYSTEM_VERSION=1;
	public static final int COLUMN_PATH=2;
	static final String COLUMN_PATH_name = "path";
	static final String COLUMN_OPERATING_SYSTEM_VERSION_name= "operating_system_version";

	static final int
		MAX_PATH_LENGTH=194, // select max(length(path)) from distro_files;
		MAX_TYPE_LENGTH=10,
		MAX_SYMLINK_TARGET_LENGTH=96, // select max(length(symlink_target)) from distro_files;
		MAX_LINUX_ACCOUNT_LENGTH=12,
		MAX_LINUX_GROUP_LENGTH=10
	;

	/**
	 * The size may not be available for certain file types.
	 */
	public static final long NULL_SIZE=-1;

	private int pkey;
	private int operating_system_version;
	private String path;
	private boolean optional;
	private String type;
	private long mode;
	private String linux_account;
	private String linux_group;
	private long size;
	private boolean has_file_sha256;
	private long file_sha256_0;
	private long file_sha256_1;
	private long file_sha256_2;
	private long file_sha256_3;
	private String symlink_target;

	@Override
	boolean equalsImpl(Object O) {
		return
			O instanceof DistroFile
			&& ((DistroFile)O).pkey==pkey
		;
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_OPERATING_SYSTEM_VERSION: return operating_system_version;
			case COLUMN_PATH: return path;
			case 3: return optional;
			case 4: return type;
			case 5: return mode;
			case 6: return linux_account;
			case 7: return linux_group;
			case 8: return size == NULL_SIZE ? null : size;
			case 9: return has_file_sha256 ? file_sha256_0 : null;
			case 10: return has_file_sha256 ? file_sha256_1 : null;
			case 11: return has_file_sha256 ? file_sha256_2 : null;
			case 12: return has_file_sha256 ? file_sha256_3 : null;
			case 13: return symlink_target;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public int getPkey() {
		return pkey;
	}

	public OperatingSystemVersion getOperatingSystemVersion() throws SQLException, IOException {
		OperatingSystemVersion osv=table.connector.getOperatingSystemVersions().get(operating_system_version);
		if(osv==null) throw new SQLException("Unable to find OperatingSystemVersion: "+operating_system_version);
		return osv;
	}

	public String getPath() {
		return path;
	}

	public boolean isOptional() {
		return optional;
	}

	public DistroFileType getType() throws SQLException, IOException {
		DistroFileType fileType=table.connector.getDistroFileTypes().get(type);
		if(fileType==null) throw new SQLException("Unable to find DistroFileType: "+type);
		return fileType;
	}

	public long getMode() {
		return mode;
	}

	public LinuxAccount getLinuxAccount() throws SQLException, IOException {
		if(table==null) throw new NullPointerException("table is null");
		if(table.connector==null) throw new NullPointerException("table.connector is null");
		LinuxAccount linuxAccount=table.connector.getLinuxAccounts().get(linux_account);
		if(linuxAccount==null) throw new SQLException("Unable to find LinuxAccount: "+linux_account);
		return linuxAccount;
	}

	public LinuxGroup getLinuxGroup() throws SQLException, IOException {
		LinuxGroup linuxGroup=table.connector.getLinuxGroups().get(linux_group);
		if(linuxGroup==null) throw new SQLException("Unable to find LinuxGroup: "+linux_group);
		return linuxGroup;
	}

	public long getSize() {
		return size;
	}

	public boolean hasFileSha256() {
		return has_file_sha256;
	}

	public long getFileSha256_0() {
		return file_sha256_0;
	}

	public long getFileSha256_1() {
		return file_sha256_1;
	}

	public long getFileSha256_2() {
		return file_sha256_2;
	}

	public long getFileSha256_3() {
		return file_sha256_3;
	}

	public String getSymlinkTarget() {
		return symlink_target;
	}

	@Override
	public Integer getKey() {
		return pkey;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.DISTRO_FILES;
	}

	@Override
	public int hashCodeImpl() {
		return pkey;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		int pos = 1;
		pkey = result.getInt(pos++);
		operating_system_version = result.getInt(pos++);
		path = result.getString(pos++);
		optional = result.getBoolean(pos++);
		type = result.getString(pos++);
		mode = result.getLong(pos++);
		linux_account = result.getString(pos++);
		linux_group = result.getString(pos++);
		size = result.getLong(pos++);
		if(result.wasNull()) size = NULL_SIZE;
		file_sha256_0 = result.getLong(pos++);
		file_sha256_1 = result.getLong(pos++);
		file_sha256_2 = result.getLong(pos++);
		file_sha256_3 = result.getLong(pos++);
		has_file_sha256 = !result.wasNull();
		symlink_target = result.getString(pos++);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey = in.readCompressedInt();
		operating_system_version = in.readCompressedInt();
		path = in.readCompressedUTF();
		optional = in.readBoolean();
		type = in.readCompressedUTF().intern();
		mode = in.readLong();
		linux_account = in.readCompressedUTF().intern();
		linux_group = in.readCompressedUTF().intern();
		size = in.readLong();
		has_file_sha256 = in.readBoolean();
		if(has_file_sha256) {
			file_sha256_0 = in.readLong();
			file_sha256_1 = in.readLong();
			file_sha256_2 = in.readLong();
			file_sha256_3 = in.readLong();
		} else {
			file_sha256_0 = 0;
			file_sha256_1 = 0;
			file_sha256_2 = 0;
			file_sha256_3 = 0;
		}
		symlink_target = in.readBoolean() ? in.readCompressedUTF() : null;
	}

	@Override
	public void readRecord(DataInputStream in) throws IOException {
		pkey = in.readInt();
		operating_system_version = in.readInt();
		path = in.readUTF();
		optional = in.readBoolean();
		type = in.readUTF().intern();
		mode = in.readLong();
		linux_account = in.readUTF().intern();
		linux_group = in.readUTF().intern();
		size = in.readLong();
		has_file_sha256 = in.readBoolean();
		if(has_file_sha256) {
			file_sha256_0 = in.readLong();
			file_sha256_1 = in.readLong();
			file_sha256_2 = in.readLong();
			file_sha256_3 = in.readLong();
		} else {
			file_sha256_0 = 0;
			file_sha256_1 = 0;
			file_sha256_2 = 0;
			file_sha256_3 = 0;
		}
		symlink_target = in.readBoolean() ? in.readUTF() : null;
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_108) >= 0) {
			out.writeCompressedInt(pkey);
			out.writeCompressedInt(operating_system_version);
		}
		out.writeCompressedUTF(path, 0);
		out.writeBoolean(optional);
		out.writeCompressedUTF(type, 1);
		out.writeLong(mode);
		out.writeCompressedUTF(linux_account, 2);
		out.writeCompressedUTF(linux_group, 3);
		out.writeLong(size);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_80) >= 0) {
			out.writeBoolean(has_file_sha256);
			if(has_file_sha256) {
				out.writeLong(file_sha256_0);
				out.writeLong(file_sha256_1);
				out.writeLong(file_sha256_2);
				out.writeLong(file_sha256_3);
			}
		} else {
			out.writeBoolean(false); // has_file_md5
		}
		out.writeBoolean(symlink_target != null);
		if(symlink_target != null) out.writeCompressedUTF(symlink_target, 4);
	}

	@Override
	public void writeRecord(DataOutputStream out) throws IOException {
		out.writeInt(pkey);
		out.writeInt(operating_system_version);
		if(path.length()>MAX_PATH_LENGTH) throw new IOException("path.length()>"+MAX_PATH_LENGTH+": "+path.length());
		out.writeUTF(path);
		out.writeBoolean(optional);
		if(type.length()>MAX_TYPE_LENGTH) throw new IOException("type.length()>"+MAX_TYPE_LENGTH+": "+type.length());
		out.writeUTF(type);
		out.writeLong(mode);
		if(linux_account.length()>MAX_LINUX_ACCOUNT_LENGTH) throw new IOException("linux_account.length()>"+MAX_LINUX_ACCOUNT_LENGTH+": "+linux_account.length());
		out.writeUTF(linux_account);
		if(linux_group.length()>MAX_LINUX_GROUP_LENGTH) throw new IOException("linux_group.length()>"+MAX_LINUX_GROUP_LENGTH+": "+linux_group.length());
		out.writeUTF(linux_group);
		out.writeLong(size);
		out.writeBoolean(has_file_sha256);
		if(has_file_sha256) {
			out.writeLong(file_sha256_0);
			out.writeLong(file_sha256_1);
			out.writeLong(file_sha256_2);
			out.writeLong(file_sha256_3);
		}
		out.writeBoolean(symlink_target!=null);
		if(symlink_target!=null) {
			if(symlink_target.length()>MAX_SYMLINK_TARGET_LENGTH) throw new IOException("symlink_target.length()>"+MAX_SYMLINK_TARGET_LENGTH+": "+symlink_target.length());
			out.writeUTF(symlink_target);
		}
	}
}
