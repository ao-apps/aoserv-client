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

import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.StringUtility;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Any incoming email addressed to a <code>EmailList</code> is immediately
 * forwarded on to all addresses contained in the list.
 *
 * @see  EmailAddress
 *
 * @author  AO Industries, Inc.
 */
final public class EmailList extends CachedObjectIntegerKey<EmailList> implements Removable, Disablable {

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

	UnixPath path;
	int linux_server_account;
	int linux_server_group;
	int disable_log;

	public int addEmailAddress(EmailAddress address) throws IOException, SQLException {
		return table.connector.getEmailListAddresses().addEmailListAddress(address, this);
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
			&& getLinuxServerGroup().getLinuxGroup().getPackage().disable_log==-1
			&& getLinuxServerAccount().disable_log==-1
		;
	}

	@Override
	public void disable(DisableLog dl) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.EMAIL_LISTS, dl.pkey, pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.EMAIL_LISTS, pkey);
	}

	/**
	 * Gets the list of addresses that email will be sent to, one address per line.
	 * The list is obtained from a file on the server that hosts the list.
	 */
	public String getAddressList() throws IOException, SQLException {
		return table.connector.requestStringQuery(true, AOServProtocol.CommandID.GET_EMAIL_LIST_ADDRESS_LIST, pkey);
	}

	/**
	 * Gets the number of addresses in an address list.  The number of addresses is equal to the number
	 * of non-blank lines.
	 */
	public int getAddressListCount() throws IOException, SQLException {
		String list=getAddressList();
		List<String> lines=StringUtility.splitString(list, '\n');
		int count=0;
		for(String line : lines) {
			if(line.trim().length()>0) count++;
		}
		return count;
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case 1: return path;
			case COLUMN_LINUX_SERVER_ACCOUNT: return linux_server_account;
			case 3: return linux_server_group;
			case 4: return disable_log==-1?null:disable_log;
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

	public List<EmailAddress> getEmailAddresses() throws IOException, SQLException {
		return table.connector.getEmailListAddresses().getEmailAddresses(this);
	}

	public List<EmailListAddress> getEmailListAddresses() throws IOException, SQLException {
		return table.connector.getEmailListAddresses().getEmailListAddresses(this);
	}

	public LinuxServerAccount getLinuxServerAccount() throws SQLException, IOException {
		LinuxServerAccount linuxServerAccountObject = table.connector.getLinuxServerAccounts().get(linux_server_account);
		if (linuxServerAccountObject == null) throw new SQLException("Unable to find LinuxServerAccount: " + linux_server_account);
		return linuxServerAccountObject;
	}

	public LinuxServerGroup getLinuxServerGroup() throws SQLException, IOException {
		LinuxServerGroup linuxServerGroupObject = table.connector.getLinuxServerGroups().get(linux_server_group);
		if (linuxServerGroupObject == null) throw new SQLException("Unable to find LinuxServerGroup: " + linux_server_group);
		return linuxServerGroupObject;
	}

	/**
	 * Gets the full path that should be used for normal email lists.
	 */
	public static String getListPath(String name) {
		if(name.length()>1) {
			char ch=name.charAt(0);
			if(ch>='A' && ch<='Z') ch+=32;
			return LIST_DIRECTORY+'/'+ch+'/'+name;
		} else return LIST_DIRECTORY+"//";
	}

	public MajordomoList getMajordomoList() throws IOException, SQLException {
		return table.connector.getMajordomoLists().get(pkey);
	}

	public UnixPath getPath() {
		return path;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.EMAIL_LISTS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = result.getInt(1);
			path = UnixPath.valueOf(result.getString(2));
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
	 */
	public static boolean isValidRegularPath(UnixPath path) {
		// Must start with LIST_DIRECTORY
		if(path==null) return false;
		String pathStr = path.toString();
		if(!pathStr.startsWith(LIST_DIRECTORY+'/')) return false;
		pathStr=pathStr.substring(LIST_DIRECTORY.length()+1);
		if(pathStr.length()<2) return false;
		char firstChar=pathStr.charAt(0);
		if(pathStr.charAt(1)!='/') return false;
		pathStr=pathStr.substring(2);
		int len = pathStr.length();
		if (len < 1 || len > MAX_NAME_LENGTH) return false;
		for (int c = 0; c < len; c++) {
			char ch = pathStr.charAt(c);
			if (c == 0) {
				if ((ch < '0' || ch > '9') && (ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z')) return false;
				// First character must match with the name
				if(ch>='A' && ch<='Z') ch+=32;
				if(ch!=firstChar) return false;
			} else {
				if ((ch < '0' || ch > '9') && (ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z') && ch != '.' && ch != '-' && ch != '_') return false;
			}
		}
		return true;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey=in.readCompressedInt();
			path = UnixPath.valueOf(in.readUTF());
			linux_server_account=in.readCompressedInt();
			linux_server_group=in.readCompressedInt();
			disable_log=in.readCompressedInt();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() {
		return Collections.emptyList();
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.REMOVE,
			SchemaTable.TableID.EMAIL_LISTS,
			pkey
		);
	}

	public void setAddressList(String addresses) throws IOException, SQLException {
		table.connector.requestUpdate(true, AOServProtocol.CommandID.SET_EMAIL_LIST_ADDRESS_LIST, pkey, addresses);
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(path.toString());
		out.writeCompressedInt(linux_server_account);
		out.writeCompressedInt(linux_server_group);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
			out.writeShort(0);
			out.writeShort(7);
		}
		out.writeCompressedInt(disable_log);
	}
}
