/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoindustries.aoserv.client.email;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.Strings;
import com.aoapps.lang.validation.ValidationException;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Disablable;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.account.DisableLog;
import com.aoindustries.aoserv.client.distribution.OperatingSystemVersion;
import com.aoindustries.aoserv.client.linux.GroupServer;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

/**
 * Any incoming email addressed to a {@link List} is immediately
 * forwarded on to all addresses contained in the list.
 *
 * @see  Address
 *
 * @author  AO Industries, Inc.
 */
public final class List extends CachedObjectIntegerKey<List> implements Removable, Disablable {

	static final int
		COLUMN_PKEY=0,
		COLUMN_LINUX_SERVER_ACCOUNT=2
	;
	static final String COLUMN_LINUX_SERVER_ACCOUNT_name = "linux_server_account";
	static final String COLUMN_PATH_name = "path";

	/**
	 * The directory that email lists are normally contained in.
	 */
	public static final String LIST_DIRECTORY="/etc/mail/lists";

	/**
	 * The maximum length of an email list name.
	 */
	public static final int MAX_NAME_LENGTH=64;

	private PosixPath path;
	private int linux_server_account;
	private int linux_server_group;
	private int disable_log;

	/**
	 * @deprecated  Only required for implementation, do not use directly.
	 *
	 * @see  #init(java.sql.ResultSet)
	 * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
	 */
	@Deprecated/* Java 9: (forRemoval = true) */
	public List() {
		// Do nothing
	}

	public int addEmailAddress(Address address) throws IOException, SQLException {
		return table.getConnector().getEmail().getListAddress().addEmailListAddress(address, this);
	}

	@Override
	public boolean canDisable() {
		return disable_log==-1;
	}

	@Override
	public boolean canEnable() throws SQLException, IOException {
		DisableLog dl=getDisableLog();
		if(dl==null) return false;
		else return
			dl.canEnable()
			&& !getLinuxServerGroup().getLinuxGroup().getPackage().isDisabled()
			&& !getLinuxServerAccount().isDisabled()
		;
	}

	@Override
	public void disable(DisableLog dl) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.DISABLE, Table.TableID.EMAIL_LISTS, dl.getPkey(), pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.ENABLE, Table.TableID.EMAIL_LISTS, pkey);
	}

	/**
	 * Gets the list of addresses that email will be sent to, one address per line.
	 * The list is obtained from a file on the server that hosts the list.
	 */
	public String getAddressList() throws IOException, SQLException {
		return table.getConnector().requestStringQuery(true, AoservProtocol.CommandID.GET_EMAIL_LIST_ADDRESS_LIST, pkey);
	}

	/**
	 * Gets the number of addresses in an address list.  The number of addresses is equal to the number
	 * of non-blank lines.
	 */
	public int getAddressListCount() throws IOException, SQLException {
		String list=getAddressList();
		java.util.List<String> lines=Strings.split(list, '\n');
		int count=0;
		for(String line : lines) {
			if(line.trim().length()>0) count++;
		}
		return count;
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case 1: return path;
			case COLUMN_LINUX_SERVER_ACCOUNT: return linux_server_account;
			case 3: return linux_server_group;
			case 4: return disable_log==-1?null:disable_log;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	@Override
	public boolean isDisabled() {
		return disable_log!=-1;
	}

	@Override
	public DisableLog getDisableLog() throws SQLException, IOException {
		if(disable_log==-1) return null;
		DisableLog obj=table.getConnector().getAccount().getDisableLog().get(disable_log);
		if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
		return obj;
	}

	public java.util.List<Address> getEmailAddresses() throws IOException, SQLException {
		return table.getConnector().getEmail().getListAddress().getEmailAddresses(this);
	}

	public java.util.List<ListAddress> getEmailListAddresses() throws IOException, SQLException {
		return table.getConnector().getEmail().getListAddress().getEmailListAddresses(this);
	}

	public int getLinuxServerAccount_pkey() {
		return linux_server_account;
	}

	public UserServer getLinuxServerAccount() throws SQLException, IOException {
		UserServer linuxServerAccountObject = table.getConnector().getLinux().getUserServer().get(linux_server_account);
		if (linuxServerAccountObject == null) throw new SQLException("Unable to find LinuxServerAccount: " + linux_server_account);
		return linuxServerAccountObject;
	}

	public int getLinuxServerGroup_pkey() {
		return linux_server_group;
	}

	public GroupServer getLinuxServerGroup() throws SQLException, IOException {
		GroupServer linuxServerGroupObject = table.getConnector().getLinux().getGroupServer().get(linux_server_group);
		if (linuxServerGroupObject == null) throw new SQLException("Unable to find LinuxServerGroup: " + linux_server_group);
		return linuxServerGroupObject;
	}

	/**
	 * Gets the full path that should be used for normal email lists.
	 *
	 * @see  OperatingSystemVersion#getEmailListPath(java.lang.String)
	 */
	public static PosixPath getListPath(String name, int osv) throws ValidationException {
		switch(osv) {
			case OperatingSystemVersion.CENTOS_5_I686_AND_X86_64 :
				if(name.length() > 1) {
					return PosixPath.valueOf(
						LIST_DIRECTORY
						+ '/'
						+ Character.toLowerCase(name.charAt(0))
						+ '/'
						+ name
					);
				} else {
					// This will always be invalid, exception expected
					String invalidPath = LIST_DIRECTORY + "//";
					PosixPath.valueOf(invalidPath);
					throw new AssertionError(invalidPath + " is invalid and should have already thrown " + ValidationException.class.getName());
				}
			case OperatingSystemVersion.CENTOS_7_X86_64 :
				if(name.length() > 1) {
					return PosixPath.valueOf(
						LIST_DIRECTORY
						+ '/'
						+ name
					);
				} else {
					// This will always be invalid, exception expected
					String invalidPath = LIST_DIRECTORY + "/";
					PosixPath.valueOf(invalidPath);
					throw new AssertionError(invalidPath + " is invalid and should have already thrown " + ValidationException.class.getName());
				}
			default :
				throw new AssertionError("Unexpected OperatingSystemVersion: " + osv);
		}
	}

	public MajordomoList getMajordomoList() throws IOException, SQLException {
		return table.getConnector().getEmail().getMajordomoList().get(pkey);
	}

	public PosixPath getPath() {
		return path;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.EMAIL_LISTS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = result.getInt(1);
			path = PosixPath.valueOf(result.getString(2));
			linux_server_account = result.getInt(3);
			linux_server_group = result.getInt(4);
			disable_log=result.getInt(5);
			if(result.wasNull()) disable_log=-1;
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	/**
	 * Checks the validity of a list name.
	 *
	 * TODO: Self-validating type
	 *
	 * @see  OperatingSystemVersion#isValidEmailListRegularPath(com.aoindustries.aoserv.client.linux.PosixPath)
	 */
	public static boolean isValidRegularPath(PosixPath path, int osv) {
		// Must start with LIST_DIRECTORY
		if(path == null) return false;
		String pathStr = path.toString();
		if(!pathStr.startsWith(LIST_DIRECTORY + '/')) return false;
		pathStr = pathStr.substring(LIST_DIRECTORY.length() + 1);
		switch(osv) {
			case OperatingSystemVersion.CENTOS_5_I686_AND_X86_64 : {
				if(pathStr.length() < 2) return false;
				char firstChar = pathStr.charAt(0);
				if(pathStr.charAt(1) != '/') return false;
				pathStr = pathStr.substring(2);
				int len = pathStr.length();
				if(len < 1 || len > MAX_NAME_LENGTH) return false;
				for(int c = 0; c < len; c++) {
					char ch = pathStr.charAt(c);
					if(c == 0) {
						if(
							(ch < '0' || ch > '9')
							&& (ch < 'a' || ch > 'z')
							&& (ch < 'A' || ch > 'Z')
						) return false;
						// First character must match with the name
						if(Character.toLowerCase(ch) != firstChar) return false;
					} else {
						if(
							(ch < '0' || ch > '9')
							&& (ch < 'a' || ch > 'z')
							&& (ch < 'A' || ch > 'Z')
							&& ch != '.'
							&& ch != '-'
							&& ch != '_'
						) return false;
					}
				}
				return true;
			}
			case OperatingSystemVersion.CENTOS_7_X86_64 : {
				int len = pathStr.length();
				if(len < 1 || len > MAX_NAME_LENGTH) return false;
				for(int c = 0; c < len; c++) {
					char ch = pathStr.charAt(c);
					if(c == 0) {
						if(
							(ch < '0' || ch > '9')
							&& (ch < 'a' || ch > 'z')
							&& (ch < 'A' || ch > 'Z')
						) return false;
					} else {
						if(
							(ch < '0' || ch > '9')
							&& (ch < 'a' || ch > 'z')
							&& (ch < 'A' || ch > 'Z')
							&& ch != '.'
							&& ch != '-'
							&& ch != '_'
						) return false;
					}
				}
				return true;
			}
			default :
				throw new AssertionError("Unexpected OperatingSystemVersion: " + osv);
		}
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			pkey=in.readCompressedInt();
			path = PosixPath.valueOf(in.readUTF());
			linux_server_account=in.readCompressedInt();
			linux_server_group=in.readCompressedInt();
			disable_log=in.readCompressedInt();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public java.util.List<CannotRemoveReason<?>> getCannotRemoveReasons() {
		return Collections.emptyList();
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(
			true,
			AoservProtocol.CommandID.REMOVE,
			Table.TableID.EMAIL_LISTS,
			pkey
		);
	}

	public void setAddressList(String addresses) throws IOException, SQLException {
		table.getConnector().requestUpdate(true, AoservProtocol.CommandID.SET_EMAIL_LIST_ADDRESS_LIST, pkey, addresses);
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(path.toString());
		out.writeCompressedInt(linux_server_account);
		out.writeCompressedInt(linux_server_group);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30)<=0) {
			out.writeShort(0);
			out.writeShort(7);
		}
		out.writeCompressedInt(disable_log);
	}
}
