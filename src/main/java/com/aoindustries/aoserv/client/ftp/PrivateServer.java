/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.ftp;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.DomainName;
import com.aoapps.net.Email;
import com.aoapps.sql.SQLStreamables;
import com.aoapps.sql.UnmodifiableTimestamp;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.linux.GroupServer;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * When a <code>PrivateFTPServer</code> is attached to a
 * <code>NetBind</code>, the FTP server reponds as configured
 * in the <code>PrivateFTPServer</code>.
 *
 * @see  Bind
 *
 * @author  AO Industries, Inc.
 */
public final class PrivateServer extends CachedObjectIntegerKey<PrivateServer> {

	static final int COLUMN_NET_BIND=0;
	static final String COLUMN_NET_BIND_name = "net_bind";

	private PosixPath logfile;
	private DomainName hostname;
	private Email email;
	private UnmodifiableTimestamp created;
	private int pub_linux_server_account;
	private boolean allow_anonymous;

	/**
	 * @deprecated  Only required for implementation, do not use directly.
	 *
	 * @see  #init(java.sql.ResultSet)
	 * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
	 */
	@Deprecated/* Java 9: (forRemoval = true) */
	public PrivateServer() {
		// Do nothing
	}

	@Override
	@SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_NET_BIND: return pkey;
			case 1: return logfile;
			case 2: return hostname;
			case 3: return email;
			case 4: return created;
			case 5: return pub_linux_server_account;
			case 6: return allow_anonymous;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	@SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
	public UnmodifiableTimestamp getCreated() {
		return created;
	}

	public Email getEmail() {
		return email;
	}

	public DomainName getHostname() {
		return hostname;
	}

	public Bind getNetBind() throws SQLException, IOException {
		Bind nb=table.getConnector().getNet().getBind().get(pkey);
		if(nb==null) throw new SQLException("Unable to find NetBind: "+pkey);
		return nb;
	}

	public PosixPath getLogfile() {
		return logfile;
	}

	public int getLinuxServerAccount_pkey() {
		return pub_linux_server_account;
	}

	public UserServer getLinuxServerAccount() throws SQLException, IOException {
		UserServer lsa=table.getConnector().getLinux().getUserServer().get(pub_linux_server_account);
		if(lsa==null) throw new SQLException("Unable to find LinuxServerAccount: "+pub_linux_server_account);
		return lsa;
	}

	/**
	 * @deprecated  use getLinuxServerAccount().getPrimaryLinuxServerGroup()
	 */
	@Deprecated
	public GroupServer getLinuxServerGroup() throws SQLException, IOException {
		return getLinuxServerAccount().getPrimaryLinuxServerGroup();
	}

	public boolean allowAnonymous() {
		return allow_anonymous;
	}

	/**
	 * @deprecated  use getLinuxServerAccount().getHome()
	 */
	@Deprecated
	public PosixPath getRoot() throws SQLException, IOException {
		return getLinuxServerAccount().getHome();
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.PRIVATE_FTP_SERVERS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = result.getInt(1);
			logfile = PosixPath.valueOf(result.getString(2));
			hostname = DomainName.valueOf(result.getString(3));
			email = Email.valueOf(result.getString(4));
			created = UnmodifiableTimestamp.valueOf(result.getTimestamp(5));
			pub_linux_server_account=result.getInt(6);
			allow_anonymous=result.getBoolean(7);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			pkey = in.readCompressedInt();
			logfile = PosixPath.valueOf(in.readUTF());
			hostname = DomainName.valueOf(in.readUTF());
			email = Email.valueOf(in.readUTF());
			created = SQLStreamables.readUnmodifiableTimestamp(in);
			pub_linux_server_account = in.readCompressedInt();
			allow_anonymous = in.readBoolean();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public String toStringImpl() {
		return hostname.toString();
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_113)<0) throw new IOException("PrivateFTPServer on AOServProtocol version less than "+AoservProtocol.Version.VERSION_1_0_A_113.getVersion()+" is no longer supported.  Please upgrade your AOServ Client software packages.");
		out.writeCompressedInt(pkey);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_38)<=0) out.writeUTF("Upgrade AOServClient to version "+AoservProtocol.Version.VERSION_1_39+" or newer");
		out.writeUTF(logfile.toString());
		out.writeUTF(hostname.toString());
		out.writeUTF(email.toString());
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_122)<=0) out.writeCompressedInt(-1);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
			out.writeLong(created.getTime());
		} else {
			SQLStreamables.writeTimestamp(created, out);
		}
		out.writeCompressedInt(pub_linux_server_account);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_38)<=0) out.writeCompressedInt(-1);
		out.writeBoolean(allow_anonymous);
	}
}
