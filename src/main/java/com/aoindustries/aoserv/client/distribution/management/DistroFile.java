/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.distribution.management;

import com.aoindustries.aoserv.client.FilesystemCachedObject;
import com.aoindustries.aoserv.client.distribution.OperatingSystemVersion;
import com.aoindustries.aoserv.client.linux.LinuxAccount;
import com.aoindustries.aoserv.client.linux.LinuxGroup;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.aoserv.client.validator.GroupId;
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.validation.ValidationException;
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

	// TODO: These fixed sizes being hard-coded is not very nice.  Maybe query
	//       the master for the longest sizes before downloading the records?
	//       Or hack the protocol a bit for this table and begin the transfer with a set of int's giving the lengths.
	static final int
		MAX_PATH_LENGTH=194, // select max(length(path)) from distro_files;
		MAX_TYPE_LENGTH=10,
		MAX_SYMLINK_TARGET_LENGTH=96, // select max(length(symlink_target)) from distro_files;
		MAX_LINUX_ACCOUNT_LENGTH=15, // select max(length(linux_account)) from distro_files;
		MAX_LINUX_GROUP_LENGTH=15 // select max(length(linux_group)) from distro_files;
	;

	/**
	 * The size may not be available for certain file types.
	 */
	public static final long NULL_SIZE=-1;

	private int pkey;
	private int operating_system_version;
	private UnixPath path;
	private boolean optional;
	private String type;
	private long mode;
	private UserId linux_account;
	private GroupId linux_group;
	private long size;
	private boolean has_file_sha256;
	private long file_sha256_0;
	private long file_sha256_1;
	private long file_sha256_2;
	private long file_sha256_3;
	private String symlink_target;

	@Override
	public boolean equalsImpl(Object O) {
		return
			O instanceof DistroFile
			&& ((DistroFile)O).pkey==pkey
		;
	}

	@Override
	protected Object getColumnImpl(int i) {
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
		OperatingSystemVersion osv=table.getConnector().getOperatingSystemVersions().get(operating_system_version);
		if(osv==null) throw new SQLException("Unable to find OperatingSystemVersion: "+operating_system_version);
		return osv;
	}

	public UnixPath getPath() {
		return path;
	}

	public boolean isOptional() {
		return optional;
	}

	public DistroFileType getType() throws SQLException, IOException {
		DistroFileType fileType=table.getConnector().getDistroFileTypes().get(type);
		if(fileType==null) throw new SQLException("Unable to find DistroFileType: "+type);
		return fileType;
	}

	public long getMode() {
		return mode;
	}

	public LinuxAccount getLinuxAccount() throws SQLException, IOException {
		if(table==null) throw new NullPointerException("table is null");
		if(table.getConnector()==null) throw new NullPointerException("table.getConnector() is null");
		LinuxAccount linuxAccount=table.getConnector().getLinuxAccounts().get(linux_account);
		if(linuxAccount==null) throw new SQLException("Unable to find LinuxAccount: "+linux_account);
		return linuxAccount;
	}

	public LinuxGroup getLinuxGroup() throws SQLException, IOException {
		LinuxGroup linuxGroup=table.getConnector().getLinuxGroups().get(linux_group);
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
		try {
			int pos = 1;
			pkey = result.getInt(pos++);
			operating_system_version = result.getInt(pos++);
			path = UnixPath.valueOf(result.getString(pos++));
			optional = result.getBoolean(pos++);
			type = result.getString(pos++);
			mode = result.getLong(pos++);
			linux_account = UserId.valueOf(result.getString(pos++));
			linux_group = GroupId.valueOf(result.getString(pos++));
			size = result.getLong(pos++);
			if(result.wasNull()) size = NULL_SIZE;
			file_sha256_0 = result.getLong(pos++);
			file_sha256_1 = result.getLong(pos++);
			file_sha256_2 = result.getLong(pos++);
			file_sha256_3 = result.getLong(pos++);
			has_file_sha256 = !result.wasNull();
			symlink_target = result.getString(pos++);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = in.readCompressedInt();
			operating_system_version = in.readCompressedInt();
			path = UnixPath.valueOf(in.readCompressedUTF());
			optional = in.readBoolean();
			type = in.readCompressedUTF().intern();
			mode = in.readLong();
			linux_account = UserId.valueOf(in.readCompressedUTF()).intern();
			linux_group = GroupId.valueOf(in.readCompressedUTF()).intern();
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
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	private static void writeChars(String s, DataOutputStream out) throws IOException {
		out.writeInt(s.length());
		out.writeChars(s);
	}

	private static String readChars(DataInputStream in) throws IOException {
		int len = in.readInt();
		char[] chars = new char[len];
		for(int i=0; i<len; i++) {
			chars[i] = in.readChar();
		}
		return new String(chars);
	}

	@Override
	public void readRecord(DataInputStream in) throws IOException {
		try {
			pkey = in.readInt();
			operating_system_version = in.readInt();
			path = UnixPath.valueOf(readChars(in));
			optional = in.readBoolean();
			type = readChars(in).intern();
			mode = in.readLong();
			linux_account = UserId.valueOf(readChars(in)).intern();
			linux_group = GroupId.valueOf(readChars(in)).intern();
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
			symlink_target = in.readBoolean() ? readChars(in) : null;
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_0_A_108) >= 0) {
			out.writeCompressedInt(pkey);
			out.writeCompressedInt(operating_system_version);
		}
		out.writeCompressedUTF(path.toString(), 0);
		out.writeBoolean(optional);
		out.writeCompressedUTF(type, 1);
		out.writeLong(mode);
		out.writeCompressedUTF(linux_account.toString(), 2);
		out.writeCompressedUTF(linux_group.toString(), 3);
		out.writeLong(size);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_80) >= 0) {
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
		String pathStr = path.toString();
		if(pathStr.length()>MAX_PATH_LENGTH) throw new IOException("path.length()>"+MAX_PATH_LENGTH+": "+pathStr.length());
		writeChars(pathStr, out);
		out.writeBoolean(optional);
		if(type.length()>MAX_TYPE_LENGTH) throw new IOException("type.length()>"+MAX_TYPE_LENGTH+": "+type.length());
		writeChars(type, out);
		out.writeLong(mode);
		{
			String linux_accountStr = linux_account.toString();
			if(linux_accountStr.length()>MAX_LINUX_ACCOUNT_LENGTH) throw new IOException("linux_account.length()>"+MAX_LINUX_ACCOUNT_LENGTH+": "+linux_accountStr.length());
			writeChars(linux_accountStr, out);
		}
		{
			String linux_groupStr = linux_group.toString();
			if(linux_groupStr.length()>MAX_LINUX_GROUP_LENGTH) throw new IOException("linux_group.length()>"+MAX_LINUX_GROUP_LENGTH+": "+linux_groupStr.length());
			writeChars(linux_groupStr, out);
		}
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
			writeChars(symlink_target, out);
		}
	}
}
