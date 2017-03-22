/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2000-2009, 2016, 2017  AO Industries, Inc.
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
import java.util.Collections;
import java.util.List;

/**
 * A <code>MajordomoServer</code> provides Majordomo functionality for
 * a <code>EmailDomain</code>.  Once the <code>MajordomoServer</code>
 * is established, any number of <code>MajordomoList</code>s may be
 * added to it.
 *
 * @see  EmailDomain
 * @see  MajordomoList
 *
 * @author  AO Industries, Inc.
 */
final public class MajordomoServer extends CachedObjectIntegerKey<MajordomoServer> implements Removable {

	static final int COLUMN_DOMAIN=0;
	static final String COLUMN_DOMAIN_name = "domain";

	/**
	 * The directory that stores the majordomo servers.
	 */
	public static final UnixPath MAJORDOMO_SERVER_DIRECTORY;
	static {
		try {
			MAJORDOMO_SERVER_DIRECTORY = UnixPath.valueOf("/etc/mail/majordomo");
		} catch(ValidationException e) {
			throw new AssertionError("These hard-coded values are valid", e);
		}
	}

	/**
	 * The username part of the email address used to directly email majordomo.
	 */
	public static final String MAJORDOMO_ADDRESS="majordomo";

	/**
	 * The username part of the email address used to directly email the majordomo owner.
	 */
	public static final String
		OWNER_MAJORDOMO_ADDRESS="owner-majordomo",
		MAJORDOMO_OWNER_ADDRESS="majordomo-owner"
	;

	int linux_server_account;
	int linux_server_group;
	String version;
	int majordomo_pipe_address;
	int owner_majordomo_add;
	int majordomo_owner_add;

	public int addMajordomoList(
		String listName
	) throws SQLException, IOException {
		return table.connector.getMajordomoLists().addMajordomoList(this, listName);
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() {
		return Collections.emptyList();
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_DOMAIN: return pkey;
			case 1: return linux_server_account;
			case 2: return linux_server_group;
			case 3: return version;
			case 4: return majordomo_pipe_address;
			case 5: return owner_majordomo_add;
			case 6: return majordomo_owner_add;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public EmailDomain getDomain() throws IOException, SQLException {
		EmailDomain obj=table.connector.getEmailDomains().get(pkey);
		if(obj==null) throw new SQLException("Unable to find EmailDomain: "+pkey);
		return obj;
	}

	public LinuxServerAccount getLinuxServerAccount() throws SQLException, IOException {
		LinuxServerAccount obj=table.connector.getLinuxServerAccounts().get(linux_server_account);
		if(obj==null) throw new SQLException("Unable to find LinuxServerAccount: "+linux_server_account);
		return obj;
	}

	public LinuxServerGroup getLinuxServerGroup() throws SQLException, IOException {
		LinuxServerGroup obj=table.connector.getLinuxServerGroups().get(linux_server_group);
		if(obj==null) throw new SQLException("Unable to find LinuxServerGroup: "+linux_server_group);
		return obj;
	}

	public EmailPipeAddress getMajordomoPipeAddress() throws SQLException, IOException {
		EmailPipeAddress obj=table.connector.getEmailPipeAddresses().get(majordomo_pipe_address);
		if(obj==null) throw new SQLException("Unable to find EmailPipeAddress: "+majordomo_pipe_address);
		return obj;
	}

	public MajordomoList getMajordomoList(String listName) throws IOException, SQLException {
		return table.connector.getMajordomoLists().getMajordomoList(this, listName);
	}

	public List<MajordomoList> getMajordomoLists() throws IOException, SQLException {
		return table.connector.getMajordomoLists().getMajordomoLists(this);
	}

	public EmailAddress getMajordomoOwnerAddress() throws SQLException, IOException {
		EmailAddress obj=table.connector.getEmailAddresses().get(majordomo_owner_add);
		if(obj==null) throw new SQLException("Unable to find EmailAddress: "+majordomo_owner_add);
		return obj;
	}

	public EmailAddress getOwnerMajordomoAddress() throws SQLException, IOException {
		EmailAddress obj=table.connector.getEmailAddresses().get(owner_majordomo_add);
		if(obj==null) throw new SQLException("Unable to find EmailAddress: "+owner_majordomo_add);
		return obj;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.MAJORDOMO_SERVERS;
	}

	public MajordomoVersion getVersion() throws SQLException, IOException {
		MajordomoVersion obj=table.connector.getMajordomoVersions().get(version);
		if(obj==null) throw new SQLException("Unable to find MajordomoVersion: "+version);
		return obj;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		linux_server_account=result.getInt(2);
		linux_server_group=result.getInt(3);
		version=result.getString(4);
		majordomo_pipe_address=result.getInt(5);
		owner_majordomo_add=result.getInt(6);
		majordomo_owner_add=result.getInt(7);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		linux_server_account=in.readCompressedInt();
		linux_server_group=in.readCompressedInt();
		version=in.readUTF().intern();
		majordomo_pipe_address=in.readCompressedInt();
		owner_majordomo_add=in.readCompressedInt();
		majordomo_owner_add=in.readCompressedInt();
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.REMOVE,
			SchemaTable.TableID.MAJORDOMO_SERVERS,
			pkey
		);
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(linux_server_account);
		out.writeCompressedInt(linux_server_group);
		out.writeUTF(version);
		out.writeCompressedInt(majordomo_pipe_address);
		out.writeCompressedInt(owner_majordomo_add);
		out.writeCompressedInt(majordomo_owner_add);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
			out.writeShort(0);
			out.writeShort(7);
		}
	}
}
