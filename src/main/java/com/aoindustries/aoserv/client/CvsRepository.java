/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2002-2013, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

/**
 * A <code>CvsRepository</code> represents on repository directory for the CVS pserver.
 *
 * @author  AO Industries, Inc.
 */
final public class CvsRepository extends CachedObjectIntegerKey<CvsRepository> implements Removable, Disablable {

	static final int
		COLUMN_PKEY=0,
		COLUMN_LINUX_SERVER_ACCOUNT=2
	;
	static final String COLUMN_LINUX_SERVER_ACCOUNT_name = "linux_server_account";
	static final String COLUMN_PATH_name = "path";

	/**
	 * The default directory containing CVS repositories.
	 */
	public static final UnixPath DEFAULT_CVS_DIRECTORY;
	static {
		try {
			DEFAULT_CVS_DIRECTORY = UnixPath.valueOf("/var/cvs").intern();
		} catch(ValidationException e) {
			throw new AssertionError("These hard-coded values are valid", e);
		}
	}

	/**
	 * The default permissions for a CVS repository.
	 */
	public static final long DEFAULT_MODE=0770;

	public static long[] getValidModes() {
		return new long[] {
			0700,
			0750,
			DEFAULT_MODE,
			0755,
			0775,
			02770,
			03770
		};
	}

	/**
	 * Allowed CVS repository paths are constrained beyond the general
	 * requirements of {@link UnixPath}.
	 * May only contain characters in the set:
	 * <code>[a-z] [A-Z] [0-9] _ . - /</code>
	 */
	public static boolean isValidPath(UnixPath path) {
		if(path == null) return false;
		String pathStr = path.toString();
		int len = pathStr.length();
		for(int c = 1; c < len; c++) {
			char ch = pathStr.charAt(c);
			if(
				(ch<'a' || ch>'z')
				&& (ch<'A' || ch>'Z')
				&& (ch<'0' || ch>'9')
				&& ch!='_'
				&& ch!='.'
				&& ch!='-'
				&& ch!='/'
			) return false;
		}
		return true;
	}

	UnixPath path;
	int linux_server_account;
	int linux_server_group;
	private long mode;
	private long created;
	int disable_log;

	@Override
	public boolean canDisable() {
		return disable_log==-1;
	}

	@Override
	public boolean canEnable() throws SQLException, IOException {
		DisableLog dl=getDisableLog();
		if(dl==null) return false;
		else return dl.canEnable() && getLinuxServerAccount().disable_log==-1;
	}

	@Override
	public void disable(DisableLog dl) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.CVS_REPOSITORIES, dl.pkey, pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.CVS_REPOSITORIES, pkey);
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case 1: return path;
			case COLUMN_LINUX_SERVER_ACCOUNT: return linux_server_account;
			case 3: return linux_server_group;
			case 4: return mode;
			case 5: return getCreated();
			case 6: return disable_log==-1?null:disable_log;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	@Override
	public boolean isDisabled() {
		return disable_log!=-1;
	}

	@Override
	public DisableLog getDisableLog() throws SQLException, IOException {
		if(disable_log==-1) return null;
		DisableLog obj=table.connector.getDisableLogs().get(disable_log);
		if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
		return obj;
	}

	public UnixPath getPath() {
		return path;
	}

	public LinuxServerAccount getLinuxServerAccount() throws SQLException, IOException {
		LinuxServerAccount lsa=table.connector.getLinuxServerAccounts().get(linux_server_account);
		if(lsa==null) throw new SQLException("Unable to find LinuxServerAccount: "+linux_server_account);
		return lsa;
	}

	public LinuxServerGroup getLinuxServerGroup() throws SQLException, IOException {
		LinuxServerGroup lsg=table.connector.getLinuxServerGroups().get(linux_server_group);
		if(lsg==null) throw new SQLException("Unable to find LinuxServerGroup: "+linux_server_group);
		return lsg;
	}

	public long getMode() {
		return mode;
	}

	public Timestamp getCreated() {
		return new Timestamp(created);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.CVS_REPOSITORIES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey=result.getInt(1);
			path = UnixPath.valueOf(result.getString(2));
			linux_server_account=result.getInt(3);
			linux_server_group=result.getInt(4);
			mode=result.getLong(5);
			created=result.getTimestamp(6).getTime();
			disable_log=result.getInt(7);
			if(result.wasNull()) disable_log=-1;
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey=in.readCompressedInt();
			path = UnixPath.valueOf(in.readUTF());
			linux_server_account=in.readCompressedInt();
			linux_server_group=in.readCompressedInt();
			mode=in.readLong();
			created=in.readLong();
			disable_log=in.readCompressedInt();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<CannotRemoveReason> getCannotRemoveReasons() {
		return Collections.emptyList();
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.CVS_REPOSITORIES, pkey);
	}

	public void setMode(long mode) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_CVS_REPOSITORY_MODE, pkey, mode);
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(path.toString());
		out.writeCompressedInt(linux_server_account);
		out.writeCompressedInt(linux_server_group);
		out.writeLong(mode);
		out.writeLong(created);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
			out.writeShort(0);
			out.writeShort(7);
		}
		out.writeCompressedInt(disable_log);
	}
}
