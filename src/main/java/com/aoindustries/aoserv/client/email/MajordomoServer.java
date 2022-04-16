/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoapps.lang.validation.ValidationException;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.linux.GroupServer;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
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
 * @see  Domain
 * @see  MajordomoList
 *
 * @author  AO Industries, Inc.
 */
public final class MajordomoServer extends CachedObjectIntegerKey<MajordomoServer> implements Removable {

	static final int COLUMN_DOMAIN=0;
	static final String COLUMN_DOMAIN_name = "domain";

	/**
	 * The directory that stores the majordomo servers.
	 */
	public static final PosixPath MAJORDOMO_SERVER_DIRECTORY;
	static {
		try {
			MAJORDOMO_SERVER_DIRECTORY = PosixPath.valueOf("/etc/mail/majordomo");
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

	private int linux_server_account;
	private int linux_server_group;
	private String version;
	private int majordomo_pipe_address;
	private int owner_majordomo_add;
	private int majordomo_owner_add;

	/**
	 * @deprecated  Only required for implementation, do not use directly.
	 *
	 * @see  #init(java.sql.ResultSet)
	 * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
	 */
	@Deprecated/* Java 9: (forRemoval = true) */
	public MajordomoServer() {
		// Do nothing
	}

	public int addMajordomoList(
		String listName
	) throws SQLException, IOException {
		return table.getConnector().getEmail().getMajordomoList().addMajordomoList(this, listName);
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() {
		return Collections.emptyList();
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_DOMAIN: return pkey;
			case 1: return linux_server_account;
			case 2: return linux_server_group;
			case 3: return version;
			case 4: return majordomo_pipe_address;
			case 5: return owner_majordomo_add;
			case 6: return majordomo_owner_add;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public Domain getDomain() throws IOException, SQLException {
		Domain obj=table.getConnector().getEmail().getDomain().get(pkey);
		if(obj==null) throw new SQLException("Unable to find EmailDomain: "+pkey);
		return obj;
	}

	public int getLinuxServerAccount_pkey() {
		return linux_server_account;
	}

	public UserServer getLinuxServerAccount() throws SQLException, IOException {
		UserServer obj=table.getConnector().getLinux().getUserServer().get(linux_server_account);
		if(obj==null) throw new SQLException("Unable to find LinuxServerAccount: "+linux_server_account);
		return obj;
	}

	public int getLinuxServerGroup_pkey() {
		return linux_server_group;
	}

	public GroupServer getLinuxServerGroup() throws SQLException, IOException {
		GroupServer obj=table.getConnector().getLinux().getGroupServer().get(linux_server_group);
		if(obj==null) throw new SQLException("Unable to find LinuxServerGroup: "+linux_server_group);
		return obj;
	}

	public PipeAddress getMajordomoPipeAddress() throws SQLException, IOException {
		PipeAddress obj=table.getConnector().getEmail().getPipeAddress().get(majordomo_pipe_address);
		if(obj==null) throw new SQLException("Unable to find EmailPipeAddress: "+majordomo_pipe_address);
		return obj;
	}

	public MajordomoList getMajordomoList(String listName) throws IOException, SQLException {
		return table.getConnector().getEmail().getMajordomoList().getMajordomoList(this, listName);
	}

	public List<MajordomoList> getMajordomoLists() throws IOException, SQLException {
		return table.getConnector().getEmail().getMajordomoList().getMajordomoLists(this);
	}

	public int getMajordomoOwnerAddress_id() {
		return majordomo_owner_add;
	}

	public Address getMajordomoOwnerAddress() throws SQLException, IOException {
		Address obj=table.getConnector().getEmail().getAddress().get(majordomo_owner_add);
		if(obj==null) throw new SQLException("Unable to find EmailAddress: "+majordomo_owner_add);
		return obj;
	}

	public int getOwnerMajordomoAddress_id() {
		return owner_majordomo_add;
	}

	public Address getOwnerMajordomoAddress() throws SQLException, IOException {
		Address obj=table.getConnector().getEmail().getAddress().get(owner_majordomo_add);
		if(obj==null) throw new SQLException("Unable to find EmailAddress: "+owner_majordomo_add);
		return obj;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.MAJORDOMO_SERVERS;
	}

	public MajordomoVersion getVersion() throws SQLException, IOException {
		MajordomoVersion obj=table.getConnector().getEmail().getMajordomoVersion().get(version);
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
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
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
		table.getConnector().requestUpdateIL(
			true,
			AoservProtocol.CommandID.REMOVE,
			Table.TableID.MAJORDOMO_SERVERS,
			pkey
		);
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(linux_server_account);
		out.writeCompressedInt(linux_server_group);
		out.writeUTF(version);
		out.writeCompressedInt(majordomo_pipe_address);
		out.writeCompressedInt(owner_majordomo_add);
		out.writeCompressedInt(majordomo_owner_add);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30)<=0) {
			out.writeShort(0);
			out.writeShort(7);
		}
	}
}
