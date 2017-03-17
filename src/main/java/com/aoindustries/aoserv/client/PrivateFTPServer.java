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
import com.aoindustries.net.DomainName;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * When a <code>PrivateFTPServer</code> is attached to a
 * <code>NetBind</code>, the FTP server reponds as configured
 * in the <code>PrivateFTPServer</code>.
 *
 * @see  NetBind
 *
 * @author  AO Industries, Inc.
 */
final public class PrivateFTPServer extends CachedObjectIntegerKey<PrivateFTPServer> {

	static final int COLUMN_NET_BIND=0;
	static final String COLUMN_NET_BIND_name = "net_bind";

	private String logfile;
	private DomainName hostname;
	private String email;
	private long created;
	int pub_linux_server_account;
	private boolean allow_anonymous;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_NET_BIND: return pkey;
			case 1: return logfile;
			case 2: return hostname;
			case 3: return email;
			case 4: return getCreated();
			case 5: return pub_linux_server_account;
			case 6: return allow_anonymous;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public Timestamp getCreated() {
		return new Timestamp(created);
	}

	public String getEmail() {
		return email;
	}

	public DomainName getHostname() {
		return hostname;
	}

	public NetBind getNetBind() throws SQLException, IOException {
		NetBind nb=table.connector.getNetBinds().get(pkey);
		if(nb==null) throw new SQLException("Unable to find NetBind: "+pkey);
		return nb;
	}

	public String getLogfile() {
		return logfile;
	}

	public LinuxServerAccount getLinuxServerAccount() throws SQLException, IOException {
		LinuxServerAccount lsa=table.connector.getLinuxServerAccounts().get(pub_linux_server_account);
		if(lsa==null) throw new SQLException("Unable to find LinuxServerAccount: "+pub_linux_server_account);
		return lsa;
	}

	/**
	 * @deprecated  use getLinuxServerAccount().getPrimaryLinuxServerGroup()
	 */
	public LinuxServerGroup getLinuxServerGroup() throws SQLException, IOException {
		return getLinuxServerAccount().getPrimaryLinuxServerGroup();
	}

	public boolean allowAnonymous() {
		return allow_anonymous;
	}

	/**
	 * @deprecated  use getLinuxServerAccount().getHome()
	 */
	public String getRoot() throws SQLException, IOException {
		return getLinuxServerAccount().getHome();
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.PRIVATE_FTP_SERVERS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = result.getInt(1);
			logfile = result.getString(2);
			hostname = DomainName.valueOf(result.getString(3));
			email = result.getString(4);
			created = result.getTimestamp(5).getTime();
			pub_linux_server_account=result.getInt(6);
			allow_anonymous=result.getBoolean(7);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey=in.readCompressedInt();
			logfile=in.readUTF();
			hostname=DomainName.valueOf(in.readUTF());
			email=in.readUTF();
			created=in.readLong();
			pub_linux_server_account=in.readCompressedInt();
			allow_anonymous=in.readBoolean();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	String toStringImpl() {
		return hostname.toString();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_113)<0) throw new IOException("PrivateFTPServer on AOServProtocol version less than "+AOServProtocol.Version.VERSION_1_0_A_113.getVersion()+" is no longer supported.  Please upgrade your AOServ Client software packages.");
		out.writeCompressedInt(pkey);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_38)<=0) out.writeUTF("Upgrade AOServClient to version "+AOServProtocol.Version.VERSION_1_39+" or newer");
		out.writeUTF(logfile);
		out.writeUTF(hostname.toString());
		out.writeUTF(email);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_122)<=0) out.writeCompressedInt(-1);
		out.writeLong(created);
		out.writeCompressedInt(pub_linux_server_account);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_38)<=0) out.writeCompressedInt(-1);
		out.writeBoolean(allow_anonymous);
	}
}
