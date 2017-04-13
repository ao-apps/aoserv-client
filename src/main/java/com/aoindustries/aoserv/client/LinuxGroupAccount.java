/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2009, 2014, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.validator.GroupId;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Each <code>LinuxGroup</code> may be accessed by any number
 * of <code>LinuxAccount</code>s.  The accounts are granted access
 * to a group via a <code>LinuxGroupAccount</code>.  One account
 * may access a maximum of 31 different groups.  Also, a
 * <code>LinuxAccount</code> must have one and only one primary
 * <code>LinuxGroupAccount</code>.
 *
 * @see  LinuxAccount
 * @see  LinuxGroup
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxGroupAccount extends CachedObjectIntegerKey<LinuxGroupAccount> implements Removable {

	static final int COLUMN_PKEY=0;
	static final String COLUMN_GROUP_NAME_name = "group_name";
	static final String COLUMN_USERNAME_name = "username";

	/**
	 * The maximum number of groups allowed for one account.
	 * 
	 * <pre>/usr/include/linux/limits.h:#define NGROUPS_MAX    65536</pre>
	 */
	public static final int MAX_GROUPS = 65536;

	GroupId group_name;
	UserId username;
	boolean is_primary;
	int operating_system_version;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case 1: return group_name;
			case 2: return username;
			case 3: return is_primary;
			case 4: return operating_system_version==-1 ? null : operating_system_version;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public LinuxAccount getLinuxAccount() throws SQLException, IOException {
		LinuxAccount usernameObject = table.connector.getUsernames().get(username).getLinuxAccount();
		if (usernameObject == null) throw new SQLException("Unable to find LinuxAccount: " + username);
		return usernameObject;
	}

	public LinuxGroup getLinuxGroup() throws SQLException, IOException {
		LinuxGroup groupNameObject = table.connector.getLinuxGroups().get(group_name);
		if (groupNameObject == null) throw new SQLException("Unable to find LinuxGroup: " + group_name);
		return groupNameObject;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.LINUX_GROUP_ACCOUNTS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = result.getInt(1);
			group_name = GroupId.valueOf(result.getString(2));
			username = UserId.valueOf(result.getString(3));
			is_primary = result.getBoolean(4);
			operating_system_version = result.getInt(5);
			if(result.wasNull()) operating_system_version = -1;
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	public boolean isPrimary() {
		return is_primary;
	}

	public OperatingSystemVersion getOperatingSystemVersion() throws SQLException, IOException {
		if(operating_system_version == -1) return null;
		OperatingSystemVersion osv = table.connector.getOperatingSystemVersions().get(operating_system_version);
		if(osv == null) throw new SQLException("Unable to find OperatingSystemVersion: " + operating_system_version);
		return osv;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = in.readCompressedInt();
			group_name = GroupId.valueOf(in.readUTF()).intern();
			username = UserId.valueOf(in.readUTF()).intern();
			is_primary = in.readBoolean();
			operating_system_version = in.readCompressedInt();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<CannotRemoveReason<LinuxGroupAccount>> getCannotRemoveReasons() {
		List<CannotRemoveReason<LinuxGroupAccount>> reasons=new ArrayList<>();
		if(is_primary) reasons.add(new CannotRemoveReason<>("Not allowed to drop a primary group", this));
		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.REMOVE,
			SchemaTable.TableID.LINUX_GROUP_ACCOUNTS,
			pkey
		);
	}

	void setAsPrimary() throws IOException, SQLException {
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.SET_PRIMARY_LINUX_GROUP_ACCOUNT,
			pkey
		);
	}

	@Override
	String toStringImpl() {
		return group_name.toString()+'|'+username.toString()+(is_primary?"|p":"|a");
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(group_name.toString());
		out.writeUTF(username.toString());
		out.writeBoolean(is_primary);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_80_1_SNAPSHOT) >= 0) {
			out.writeCompressedInt(operating_system_version);
		}
	}
}
